package nl.uni.cos.Visitors;

import nl.uni.cos.BSharpParser;
import nl.uni.cos.Models.DataType;
import nl.uni.cos.Models.Method;
import nl.uni.cos.Models.Scope;
import nl.uni.cos.Models.Symbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * The {@link BSharpVisitor} that will visit the {@link ParseTree}
 * and build the underlying {@link Scope}s. and {@link DataType}s for it.
 */
public class TypeScopeBuilder extends BaseDataTypeVisitor {
    @Override
    public DataType visitProgram(BSharpParser.ProgramContext ctx) {
        Scope scope = enterScope(ctx);
        scope.addVar(DataType.CLASS.name(), DataType.CLASS, true);

        for (BSharpParser.MethodDeclarationContext methodCtx : ctx.methodDeclaration()) {
            visit(methodCtx);
        }

        return null;
    }

    // region Method declaration

    @Override
    public DataType visitMethodDeclaration(BSharpParser.MethodDeclarationContext ctx) {
        DataType methodType = ctx.primitiveTypes() != null
                ? visit(ctx.primitiveTypes())
                : DataType.VOID;

        enterScope(ctx);
        visit(ctx.parametersDeclaration());
        visit(ctx.codeBlock());
        leaveScope();

        return methodType;
    }

    @Override
    public DataType visitParametersDeclaration(BSharpParser.ParametersDeclarationContext ctx) {
        visitAll(ctx.parameterDeclaration());
        return null;
    }

    @Override
    public DataType visitParameterDeclaration(BSharpParser.ParameterDeclarationContext ctx) {
        DataType varType = visit(ctx.primitiveTypes());
        currentScope.addVar(ctx.IDENTIFIER(), varType, true);
        return varType;
    }

    // endregion

    // region Build in methods

    @Override
    public DataType visitWriteLineMethod(BSharpParser.WriteLineMethodContext ctx) {
        currentScope.incrementStack();
        DataType argType = visit(ctx.expression());
        currentScope.addMethod(ctx.WRITE_LINE(), DataType.VOID, argType);
        addDataType(ctx, argType);

        // Decrement the stack size by 2 because of the literal value and the PrintStream object are consumed.
        currentScope.decrementStack(2);

        return DataType.VOID;
    }

    @Override
    public DataType visitReadConsoleInt(BSharpParser.ReadConsoleIntContext ctx) {
        currentScope.incrementStack(3);
        currentScope.addMethod(ctx.READ_CONSOLE_INT(), DataType.INT);
        currentScope.decrementStack(3);
        return super.visitReadConsoleInt(ctx);
    }

    @Override
    public DataType visitReadConsoleLine(BSharpParser.ReadConsoleLineContext ctx) {
        currentScope.incrementStack(3);
        currentScope.addMethod(ctx.READ_CONSOLE_LINE(), DataType.STRING);
        currentScope.decrementStack(3);
        return super.visitReadConsoleLine(ctx);
    }

    @Override
    public DataType visitReadConsoleBool(BSharpParser.ReadConsoleBoolContext ctx) {
        currentScope.incrementStack(3);
        currentScope.addMethod(ctx.READ_CONSOLE_BOOL(), DataType.BOOL);
        currentScope.decrementStack(3);
        return super.visitReadConsoleBool(ctx);
    }

    @Override
    public DataType visitReadConsoleFloat(BSharpParser.ReadConsoleFloatContext ctx) {
        currentScope.incrementStack(3);
        currentScope.addMethod(ctx.READ_CONSOLE_FLOAT(), DataType.FLOAT);
        currentScope.decrementStack(3);
        return super.visitReadConsoleFloat(ctx);
    }

    @Override
    public DataType visitRandomInt(BSharpParser.RandomIntContext ctx) {
        currentScope.incrementStack(3);
        currentScope.addMethod(ctx.RANDOM_INT(), DataType.INT);

        if (ctx.expression() != null) {
            visit(ctx.expression());
        }

        currentScope.decrementStack(3);

        return super.visitRandomInt(ctx);
    }

    // endregion

    // region Statements

    @Override
    public DataType visitIfStatement(BSharpParser.IfStatementContext ctx) {
        // IF
        enterScope(ctx);
        visit(ctx.parameterExpression());
        currentScope.decrementStack();
        visit(ctx.statement(0));
        leaveScope();

        // ELSE
        if (ctx.ELSE() != null) {
            enterScope(ctx.ELSE());
            visit(ctx.statement(1));
            leaveScope();
        }

        return null;
    }

    @Override
    public DataType visitWhileStatement(BSharpParser.WhileStatementContext ctx) {
        enterScope(ctx);
        visit(ctx.parameterExpression());
        currentScope.decrementStack();
        visit(ctx.statement());
        leaveScope();

        return null;
    }

    @Override
    public DataType visitReturnStatement(BSharpParser.ReturnStatementContext ctx) {
        DataType expressionType = ctx.expressions() != null
                ? visit(ctx.expressions())
                : DataType.VOID;

        addDataType(ctx, expressionType);
        currentScope.setHasReturnStatement(true);
        return expressionType;
    }

    @Override
    public DataType visitCodeBlock(BSharpParser.CodeBlockContext ctx) {
        enterScope(ctx);
        visitAll(ctx.codeBlockStatement());
        leaveScope();
        return null;
    }

    @Override
    public DataType visitCodeBlockStatement(BSharpParser.CodeBlockStatementContext ctx) {
        if (ctx.varDeclaration() != null) {
            return visit(ctx.varDeclaration());
        } else if (ctx.statement() != null) {
            return visit(ctx.statement());
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public DataType visitVarDeclaration(BSharpParser.VarDeclarationContext ctx) {
        DataType varType = null;
        for (BSharpParser.VarDeclaratorContext varContext : ctx.varDeclarator()) {
            DataType declaratorType = visit(varContext);
            varType = ctx.primitiveTypes() != null
                    ? visit(ctx.primitiveTypes())
                    : declaratorType;

            boolean isInitialized = varContext.expression() != null;

            if (isInitialized)
                currentScope.decrementStack();

            currentScope.addVar(varContext.IDENTIFIER(), varType, isInitialized);
        }

        return varType;
    }

    // endregion

    // region Expressions

    @Override
    public DataType visitPrimaryExpression(BSharpParser.PrimaryExpressionContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            Symbol symbol = currentScope.findSymbol(ctx.IDENTIFIER());
            currentScope.incrementStack();
            return symbol != null ? symbol.getType() : null;
        }

        return super.visitPrimaryExpression(ctx);
    }

    @Override
    public DataType visitMethodCall(BSharpParser.MethodCallContext ctx) {
        if (ctx.buildInMethodCalls() != null) {
            DataType type = visit(ctx.buildInMethodCalls());
            if (type != DataType.VOID) {
                currentScope.incrementStack();
            }
            return type;
        }

        // Custom method call.
        if (ctx.IDENTIFIER() != null) {
            // needed because `this` is loaded onto the stack.
            currentScope.incrementStack();
            Method method = findMethod(ctx.IDENTIFIER());

            if (method == null) {
                // The Method does not exist.
                // The code checker will throw the correct error for this.
                return DataType.VOID;
            }

            if (ctx.expressions() != null)
                visit(ctx.expressions());

            // `This` has been consumed from the stack.
            currentScope.decrementStack();

            currentScope.addMethod(ctx.IDENTIFIER(), method.type(), method.args());
            if (method.type() != DataType.VOID)
                currentScope.incrementStack();

            return method.type();
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public DataType visitCalculationExpression(BSharpParser.CalculationExpressionContext ctx) {
        // Calculation expression always have 2 locals on the stack, consumes them both, and adds the result back to the stack.
        DataType type = visitTypeExpression(ctx, ctx.left, ctx.right);
        currentScope.decrementStack();
        return type;
    }

    @Override
    public DataType visitAndOrExpression(BSharpParser.AndOrExpressionContext ctx) {
        return visitBoolExpression(ctx, ctx.left, ctx.right);
    }

    @Override
    public DataType visitCompareExpression(BSharpParser.CompareExpressionContext ctx) {
        return visitBoolExpression(ctx, ctx.left, ctx.right);
    }

    private DataType visitBoolExpression(ParserRuleContext ctx, BSharpParser.ExpressionContext left, BSharpParser.ExpressionContext right) {
        DataType type = visit(left);
        visit(right);
        addDataType(ctx, type);

        // Bool expression always have 2 locals on the stack, consumes them both, and adds the result back to the stack.
        currentScope.decrementStack();
        return DataType.BOOL;
    }

    @Override
    public DataType visitAssignExpression(BSharpParser.AssignExpressionContext ctx) {
        // Decrement stack size because the values is now a part of the locals.
        currentScope.decrementStack();

        // Increment stack size if the value is being used after the assignment.
        if (!(ctx.getParent().getRuleContext() instanceof BSharpParser.ExpressionStatementContext)) {
            currentScope.incrementStack();
        }

        visit(ctx.left);
        DataType type = visit(ctx.right);
        addDataType(ctx, type);
        return type;
    }

    private DataType visitTypeExpression(ParserRuleContext ctx, BSharpParser.ExpressionContext left, BSharpParser.ExpressionContext right) {
        DataType leftType = visit(left);
        DataType rightType = visit(right);
        DataType type = leftType != null ? leftType : rightType;

        addDataType(ctx, type);
        return type;
    }

    // endregion

    // region Primitives

    @Override
    public DataType visitBoolPrimitive(BSharpParser.BoolPrimitiveContext ctx) {
        addDataType(ctx, DataType.BOOL);
        return super.visitBoolPrimitive(ctx);
    }

    @Override
    public DataType visitIntPrimitive(BSharpParser.IntPrimitiveContext ctx) {
        addDataType(ctx, DataType.INT);
        return super.visitIntPrimitive(ctx);
    }

    @Override
    public DataType visitStringPrimitive(BSharpParser.StringPrimitiveContext ctx) {
        addDataType(ctx, DataType.STRING);
        return super.visitStringPrimitive(ctx);
    }

    @Override
    public DataType visitFloatPrimitive(BSharpParser.FloatPrimitiveContext ctx) {
        addDataType(ctx, DataType.FLOAT);
        return super.visitFloatPrimitive(ctx);
    }

    @Override
    public DataType visitCharPrimitive(BSharpParser.CharPrimitiveContext ctx) {
        addDataType(ctx, DataType.CHAR);
        return super.visitCharPrimitive(ctx);
    }

    // endregion

    @Override
    public DataType visitLiteral(BSharpParser.LiteralContext ctx) {
        currentScope.incrementStack();
        return super.visitLiteral(ctx);
    }

    /**
     * Enter a new {@link Scope} for a {@link ParseTree}.
     *
     * @param ctx The {@link ParseTree}.
     * @return The new {@link Scope} with the previous {@link Scope} as the parent.
     */
    private Scope enterScope(ParseTree ctx) {
        Scope scope = currentScope != null
                ? currentScope.enterScope()
                : new Scope(null);

        addScope(ctx, scope);
        currentScope = scope;
        return scope;
    }
}
