package nl.uni.cos.Visitors;

import nl.uni.cos.BSharpParser;
import nl.uni.cos.Models.*;
import org.antlr.v4.runtime.ParserRuleContext;

import static nl.uni.cos.Models.ErrorMessageTemplates.*;

/**
 * The {@link BSharpVisitor} that will check the code if it contains any errors.
 * For example calling a variable before it is created.
 */
public class CodeChecker extends BaseDataTypeVisitor {
    private Method currentMethod;

    @Override
    public DataType visitProgram(BSharpParser.ProgramContext ctx) {
        // Check if the main method exists, and check if it's valid.
        Method mainMethod = findMethod("Main");
        if (mainMethod == null) {
            addError(ctx, MissingMainTemp.getTemplate());
        } else if (mainMethod.args().size() != 0) {
            addError(ctx, MainParamsTemp.getTemplate());
        }

        visitAll(ctx.methodDeclaration());
        throwErrors();
        return null;
    }

    // region Method declaration

    @Override
    public DataType visitMethodDeclaration(BSharpParser.MethodDeclarationContext ctx) {
        findScope(ctx);

        int methodCount = getMethodCount(ctx.IDENTIFIER());
        if (methodCount > 1) {
            addError(ctx, String.format(DuplicatedMethodNamesTemp.getTemplate(), ctx.IDENTIFIER().getText()));
        }

        Scope codeBlockScope = getScope(ctx.codeBlock());
        currentMethod = findMethod(ctx.IDENTIFIER());
        if (!codeBlockScope.hasReturnStatement() && currentMethod.type() != DataType.VOID) {
            addError(ctx, String.format(MissingReturnTemp.getTemplate(), ctx.IDENTIFIER().getText()), true);
        }

        visit(ctx.codeBlock());
        leaveScope();

        return currentMethod.type();
    }

    // endregion

    // region Build in methods

    @Override
    public DataType visitWriteLineMethod(BSharpParser.WriteLineMethodContext ctx) {
        DataType expressionType = visit(ctx.expression());
        if (expressionType == DataType.CLASS) {
            addError(ctx, String.format(WriteLineClassTemp.getTemplate(), DataType.CLASS));
        }

        return DataType.VOID;
    }

    @Override
    public DataType visitRandomInt(BSharpParser.RandomIntContext ctx) {
        if (ctx.expression() != null) {
            DataType expressionType = visit(ctx.expression());
            if (expressionType != DataType.INT) {
                addTypeMismatchError(ctx, DataType.INT, expressionType);
            }
        }

        return super.visitRandomInt(ctx);
    }

    // endregion

    // region Statements

    @Override
    public DataType visitIfStatement(BSharpParser.IfStatementContext ctx) {
        findScope(ctx);
        DataType expressionType = visit(ctx.parameterExpression());
        if (expressionType != DataType.BOOL) {
            addTypeMismatchError(ctx, DataType.BOOL, expressionType);
        }

        visit(ctx.statement(0));
        leaveScope();

        if (ctx.ELSE() != null) {
            findScope(ctx.ELSE());
            visit(ctx.statement(1));
            leaveScope();
        }

        return DataType.VOID;
    }

    @Override
    public DataType visitWhileStatement(BSharpParser.WhileStatementContext ctx) {
        findScope(ctx);
        DataType expressionType = visit(ctx.parameterExpression());
        if (expressionType != DataType.BOOL) {
            addTypeMismatchError(ctx, DataType.BOOL, expressionType);
        }

        visit(ctx.statement());
        leaveScope();

        return DataType.VOID;
    }

    @Override
    public DataType visitReturnStatement(BSharpParser.ReturnStatementContext ctx) {
        DataType expressionType = getDataType(ctx);
        if (expressionType != currentMethod.type()) {
            addTypeMismatchError(ctx, currentMethod.type(), expressionType);
        }

        return expressionType;
    }

    @Override
    public DataType visitCodeBlock(BSharpParser.CodeBlockContext ctx) {
        findScope(ctx);
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
        DataType primitiveType = null;
        for (BSharpParser.VarDeclaratorContext varContext : ctx.varDeclarator()) {
            DataType declaratorType = visit(varContext);
            primitiveType = ctx.VAR() != null
                    ? declaratorType
                    : visit(ctx.primitiveTypes());

            if (primitiveType != declaratorType && declaratorType != null) {
                addTypeMismatchError(ctx, primitiveType, declaratorType);
            }
        }

        return primitiveType;
    }

    @Override
    public DataType visitVarDeclarator(BSharpParser.VarDeclaratorContext ctx) {
        Symbol variable = currentScope.findSymbol(ctx.IDENTIFIER());
        if (variable instanceof MethodSymbol) {
            addError(ctx, String.format(NotAVariableTemp.getTemplate(), ctx.IDENTIFIER().getText()));
        }

        if (variable.getType() == null) {
            addError(ctx, String.format(UnknownTypeTemp.getTemplate(), ctx.IDENTIFIER().getText()));
        }

        if (currentScope.countSymbol(ctx.IDENTIFIER()) > 1) {
            addError(ctx, String.format(DuplicatedVarNamesTemp.getTemplate(), ctx.IDENTIFIER().getText()));
        }

        if (variable.getType() == DataType.VOID) {
            addError(ctx, String.format(IncorrectVariableTypeTemp.getTemplate(), DataType.VOID, ctx.IDENTIFIER().getText()));
        }

        if (ctx.expression() != null) {
            return visit(ctx.expression());
        }

        return null;
    }

    // endregion

    // region Expressions

    @Override
    public DataType visitPrimaryExpression(BSharpParser.PrimaryExpressionContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            Symbol symbol = currentScope.findSymbol(ctx.IDENTIFIER());

            if (symbol == null) {
                addError(ctx, String.format(UnknownVariableTemp.getTemplate(), ctx.IDENTIFIER().getText()));
                return null;
            }

            // Check if the identifier is an initialized variable.
            if (symbol instanceof MethodSymbol) {
                addError(ctx, String.format(NotAVariableTemp.getTemplate(), ctx.IDENTIFIER().getText()));
            } else if (symbol instanceof VarSymbol varSymbol && !varSymbol.isInitialized()) {
                addError(ctx, String.format(VarNotInitializedTemp.getTemplate(), ctx.IDENTIFIER().getText()));
            }

            return symbol.getType();
        }

        return super.visitPrimaryExpression(ctx);
    }

    @Override
    public DataType visitCalculationExpression(BSharpParser.CalculationExpressionContext ctx) {
        DataType leftType = visit(ctx.left);
        DataType rightType = visit(ctx.right);

        if (leftType != rightType) {
            addTypeMismatchError(ctx, leftType, rightType);
        }

        if (leftType == DataType.STRING
                && (ctx.ADD() != null
                || ctx.MUL() != null
                || ctx.SUB() != null
                || ctx.DIV() != null
                || ctx.MOD() != null)) {
            addError(ctx, String.format(UnsupportedOperatorTemp.getTemplate(), leftType, ctx.op.getText()));
        }

        return leftType;
    }

    @Override
    public DataType visitAndOrExpression(BSharpParser.AndOrExpressionContext ctx) {
        DataType leftType = visit(ctx.left);
        DataType rightType = visit(ctx.right);

        if (leftType != DataType.BOOL) {
            addError(ctx, String.format(UnsupportedOperatorTemp.getTemplate(), leftType, ctx.op.getText()));
        }

        if (leftType != rightType) {
            addTypeMismatchError(ctx, leftType, rightType);
        }

        return DataType.BOOL;
    }

    @Override
    public DataType visitCompareExpression(BSharpParser.CompareExpressionContext ctx) {
        DataType leftType = visit(ctx.left);
        DataType rightType = visit(ctx.right);

        if (leftType != rightType) {
            addTypeMismatchError(ctx, leftType, rightType);
        }

        if (leftType == DataType.STRING && ctx.EQUALS() == null && ctx.NOT_EQUALS() == null) {
            addError(ctx, String.format(UnsupportedOperatorTemp.getTemplate(), leftType, ctx.op.getText()));
        }

        return DataType.BOOL;
    }

    @Override
    public DataType visitParameterExpression(BSharpParser.ParameterExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public DataType visitMethodCall(BSharpParser.MethodCallContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            Method method = findMethod(ctx.IDENTIFIER());

            if (method == null) {
                addError(ctx, String.format(UnknownMethodTemp.getTemplate(), ctx.IDENTIFIER().getText()));
                return DataType.VOID;
            }

            return method.type();
        }

        if (ctx.buildInMethodCalls() != null) {
            return visit(ctx.buildInMethodCalls());
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public DataType visitAssignExpression(BSharpParser.AssignExpressionContext ctx) {
        if (ctx.left.getRuleContext() instanceof BSharpParser.PrimaryExpressionContext primaryExpression) {
            if (primaryExpression.IDENTIFIER() == null) {
                addError(ctx, VarAssignExpectedTemp.getTemplate());
            } else if (currentScope.findSymbol(primaryExpression.IDENTIFIER()) instanceof VarSymbol varSymbol) {
                varSymbol.setInitialized(true);
            }
        }

        DataType leftType = visit(ctx.left);
        DataType rightType = visit(ctx.right);

        if (leftType != rightType) {
            addTypeMismatchError(ctx, leftType, rightType);
        }

        return leftType;
    }

    @Override
    public DataType visitBangExpression(BSharpParser.BangExpressionContext ctx) {
        DataType expressionType = visit(ctx.expression());
        if (expressionType != DataType.BOOL)
            addTypeMismatchError(ctx, expressionType, DataType.BOOL);

        return DataType.BOOL;
    }

    // endregion

    /**
     * Add a TypeMismatchTemp error to the error list.
     *
     * @param ctx       The {@link ParserRuleContext} where the error is located.
     * @param leftType  The {@link DataType} on the left.
     * @param rightType The {@link DataType} on the right.
     */
    private void addTypeMismatchError(ParserRuleContext ctx, DataType leftType, DataType rightType) {
        addError(ctx, String.format(TypeMismatchTemp.getTemplate(), leftType, rightType));
    }
}
