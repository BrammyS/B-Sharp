package nl.uni.cos.Visitors;

import nl.uni.cos.BSharpParser;
import nl.uni.cos.Exceptions.CompilerException;
import nl.uni.cos.JasminBytecode;
import nl.uni.cos.Models.DataType;
import nl.uni.cos.Models.Method;
import nl.uni.cos.Models.Symbol;
import nl.uni.cos.Models.VarSymbol;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Locale;

/**
 * The {@link BSharpVisitor} that will build the jasmin byte code.
 */
public class CodeGenerator extends BSharpVisitor<Void> {
    private final JasminBytecode jasminBytecode;
    private int labelCounter = 0;

    /**
     * Initializes a new {@link BSharpParser}.
     *
     * @param jasminBytecode The class that contains the jasmin bytecode.
     */
    public CodeGenerator(JasminBytecode jasminBytecode) {
        this.jasminBytecode = jasminBytecode;
        generateDefaultCode();
    }

    /**
     * Add the default byte code to the {@link JasminBytecode}.
     */
    private void generateDefaultCode() {
        jasminBytecode.add(".bytecode 49.0")
                .add(String.format(".class public %s", jasminBytecode.getClassName()))
                .add(".super java/lang/Object")
                .add(".method public <init>()V")
                .add(".limit stack 1")
                .add(".limit locals 1")
                .add("aload_0")
                .add("invokespecial java/lang/Object/<init>()V")
                .add("return")
                .add(".end method");

        // Java Main method.
        jasminBytecode.add(".method public static main([Ljava/lang/String;)V")
                .add(".limit stack 2")
                .add(".limit locals 2");

        // Call the BSharp main method.
        jasminBytecode.add(String.format("new %s", jasminBytecode.getClassName()))
                .add("dup")
                .add(String.format("invokespecial %s.<init>()V", jasminBytecode.getClassName()))
                .add("astore_1")
                .add("aload_1")
                .add(String.format("invokevirtual %s.Main()V", jasminBytecode.getClassName()));

        jasminBytecode.add("return")
                .add(".end method");
    }

    @Override
    public Void visitProgram(BSharpParser.ProgramContext ctx) {
        visitAll(ctx.methodDeclaration());
        return null;
    }

    // region Method declaration

    @Override
    public Void visitMethodDeclaration(BSharpParser.MethodDeclarationContext ctx) {
        findScope(ctx);
        jasminBytecode.appendText(String.format(".method public %s", ctx.IDENTIFIER().getText()));

        visit(ctx.parametersDeclaration());

        // Method return type.
        if (ctx.primitiveTypes() != null) {
            visit(ctx.primitiveTypes());
        } else if (ctx.VOID() != null) {
            jasminBytecode.appendText("V");
        } else {
            throw new UnsupportedOperationException();
        }

        jasminBytecode.buildLine();

        jasminBytecode.add(String.format(".limit stack %s", currentScope.getMaxStackSize()))
                .add(String.format(".limit locals %s", currentScope.getMaxLocalsSize()));

        visit(ctx.codeBlock());

        if (findMethod(ctx.IDENTIFIER()).type() == DataType.VOID) {
            jasminBytecode.add("return");
        }

        jasminBytecode.add(".end method");

        return null;
    }

    @Override
    public Void visitParametersDeclaration(BSharpParser.ParametersDeclarationContext ctx) {
        jasminBytecode.appendText(ctx.PAREN_OPEN().getText());

        visitAll(ctx.parameterDeclaration());

        jasminBytecode.appendText(ctx.PAREN_CLOSE().getText());

        return null;
    }

    @Override
    public Void visitParameterDeclaration(BSharpParser.ParameterDeclarationContext ctx) {
        visit(ctx.primitiveTypes());

        return null;
    }

    // endregion

    // region Build in methods

    @Override
    public Void visitWriteLineMethod(BSharpParser.WriteLineMethodContext ctx) {
        jasminBytecode.add("getstatic java/lang/System/out Ljava/io/PrintStream;");

        visit(ctx.expression());

        jasminBytecode.appendText("invokevirtual java/io/PrintStream/println(")
                .appendText(getOutputTypeCode(getDataType(ctx), true))
                .appendText(")V")
                .buildLine();

        return null;
    }

    @Override
    public Void visitReadConsoleInt(BSharpParser.ReadConsoleIntContext ctx) {
        jasminBytecode.addScannerInit()
                .add(String.format("invokevirtual java/util/Scanner.nextInt()%s", getOutputTypeCode(DataType.INT, true)));

        return null;
    }

    @Override
    public Void visitReadConsoleLine(BSharpParser.ReadConsoleLineContext ctx) {
        jasminBytecode.addScannerInit()
                .add(String.format("invokevirtual java/util/Scanner.nextLine()%s", getOutputTypeCode(DataType.STRING)));

        return null;
    }

    @Override
    public Void visitReadConsoleFloat(BSharpParser.ReadConsoleFloatContext ctx) {
        jasminBytecode.addScannerInit()
                .add(String.format("invokevirtual java/util/Scanner.nextFloat()%s", getOutputTypeCode(DataType.FLOAT, true)));

        return null;
    }

    @Override
    public Void visitReadConsoleBool(BSharpParser.ReadConsoleBoolContext ctx) {
        jasminBytecode.addScannerInit()
                .add(String.format("invokevirtual java/util/Scanner.nextBoolean()%s", getOutputTypeCode(DataType.BOOL, true)));


        return null;
    }

    @Override
    public Void visitRandomInt(BSharpParser.RandomIntContext ctx) {
        jasminBytecode.add("new java/util/Random")
                .add("dup")
                .add("invokespecial java/util/Random.<init>()V");

        if (ctx.expression() != null) {
            visit(ctx.expression());
        }

        jasminBytecode.appendText("invokevirtual java/util/Random.nextInt")
                .appendText('(')
                .appendText(ctx.expression() != null ? "I" : "")
                .appendText(")I")
                .buildLine();

        return null;
    }

    // endregion

    // region Statements

    @Override
    public Void visitIfStatement(BSharpParser.IfStatementContext ctx) {
        findScope(ctx);

        visit(ctx.parameterExpression());

        String ifLabel = getUniqueLabel();

        jasminBytecode.add(String.format("ifeq %s", ifLabel));
        visit(ctx.statement(0));

        if (ctx.ELSE() != null) {
            String elseLabel = getUniqueLabel();
            jasminBytecode.add(String.format("goto %s", elseLabel))
                    .add(String.format("%s:", ifLabel));
            findScope(ctx.ELSE());
            visit(ctx.statement(1));
            jasminBytecode.add(String.format("%s:", elseLabel));
        } else {
            jasminBytecode.add(String.format("%s:", ifLabel));
        }


        return null;
    }

    @Override
    public Void visitWhileStatement(BSharpParser.WhileStatementContext ctx) {
        findScope(ctx);
        String whileLabel = getUniqueLabel();
        String endWhileLabel = getUniqueLabel();

        jasminBytecode.add(String.format("%s:", whileLabel));
        visit(ctx.parameterExpression());
        jasminBytecode.add(String.format("ifeq %s", endWhileLabel));
        visit(ctx.statement());
        jasminBytecode.add(String.format("goto %s", whileLabel))
                .add(String.format("%s:", endWhileLabel));
        return null;
    }

    @Override
    public Void visitReturnStatement(BSharpParser.ReturnStatementContext ctx) {
        if (ctx.expressions() != null) {
            visit(ctx.expressions());
        }

        jasminBytecode.appendText(getTypeCode(getDataType(ctx)))
                .appendText("return")
                .buildLine();

        return null;
    }

    @Override
    public Void visitCodeBlock(BSharpParser.CodeBlockContext ctx) {
        findScope(ctx);
        visitAll(ctx.codeBlockStatement());

        return null;
    }

    @Override
    public Void visitCodeBlockStatement(BSharpParser.CodeBlockStatementContext ctx) {
        if (ctx.varDeclaration() != null) {
            visit(ctx.varDeclaration());
        } else if (ctx.statement() != null) {
            visit(ctx.statement());
        } else {
            throw new UnsupportedOperationException();
        }

        return null;
    }

    @Override
    public Void visitVarDeclaration(BSharpParser.VarDeclarationContext ctx) {
        for (BSharpParser.VarDeclaratorContext varContext : ctx.varDeclarator()) {
            visit(varContext);
        }

        return null;
    }

    @Override
    public Void visitVarDeclarator(BSharpParser.VarDeclaratorContext ctx) {
        if (ctx.expression() == null) {
            return null;
        }

        visit(ctx.expression());
        storeOrLoadVar(ctx.IDENTIFIER(), false);
        return null;
    }

    // endregion

    // region Expressions

    @Override
    public Void visitPrimaryExpression(BSharpParser.PrimaryExpressionContext ctx) {
        if (ctx.IDENTIFIER() == null) {
            return super.visitPrimaryExpression(ctx);
        }

        storeOrLoadVar(ctx.IDENTIFIER(), true);
        return null;
    }


    @Override
    public Void visitCalculationExpression(BSharpParser.CalculationExpressionContext ctx) {
        visit(ctx.left);
        visit(ctx.right);

        DataType type = getDataType(ctx);
        jasminBytecode.appendText(getOutputTypeCode(type));

        jasminBytecode.appendText(switch (ctx.op.getType()) {
            case BSharpParser.MOD -> "rem";
            case BSharpParser.MUL -> "mul";
            case BSharpParser.DIV -> "div";
            case BSharpParser.ADD -> "add";
            case BSharpParser.SUB -> "sub";
            default -> throw new UnsupportedOperationException();
        });
        jasminBytecode.buildLine();

        return null;
    }

    @Override
    public Void visitAndOrExpression(BSharpParser.AndOrExpressionContext ctx) {
        String label1 = getUniqueLabel();
        String label2 = getUniqueLabel();

        ArrayList<BSharpParser.ExpressionContext> sides = new ArrayList<>();
        sides.add(ctx.left);
        sides.add(ctx.right);
        for (BSharpParser.ExpressionContext side : sides) {
            visit(side);
            String ifType = switch (ctx.op.getType()) {
                case BSharpParser.AND -> "eq";
                case BSharpParser.OR -> "ne";
                default -> throw new UnsupportedOperationException();
            };

            jasminBytecode.add(String.format("if%s %s", ifType, label1));
        }

        // The results need to be flipped depending on whether it's an OR statement.
        jasminBytecode.add("iconst_" + (ctx.AND() != null ? 1 : 0))
                .add(String.format("goto %s", label2))
                .add(String.format("%s:", label1))
                .add("iconst_" + (ctx.AND() != null ? 0 : 1))
                .add(String.format("%s:", label2));

        return null;
    }

    @Override
    public Void visitCompareExpression(BSharpParser.CompareExpressionContext ctx) {
        visit(ctx.left);
        visit(ctx.right);

        DataType type = getDataType(ctx);
        if (type == DataType.FLOAT) jasminBytecode.add("fcmpl");

        jasminBytecode.appendText("if");
        if (type == DataType.INT | type == DataType.BOOL) jasminBytecode.appendText("_icmp");
        else if (type == DataType.STRING) jasminBytecode.appendText("_acmp");

        jasminBytecode.appendText(switch (ctx.op.getType()) {
            case BSharpParser.LE -> "le";
            case BSharpParser.LT -> "lt";
            case BSharpParser.GE -> "ge";
            case BSharpParser.GT -> "gt";
            case BSharpParser.EQUALS -> "ne";
            case BSharpParser.NOT_EQUALS -> "eq";
            default -> throw new UnsupportedOperationException();
        });

        String label1 = getUniqueLabel();
        String label2 = getUniqueLabel();

        jasminBytecode.appendText(String.format(" %s", label1))
                .buildLine();

        jasminBytecode.add("iconst_1")
                .add(String.format("goto %s", label2))
                .add(String.format("%s:", label1))
                .add("iconst_0")
                .add(String.format("%s:", label2));

        return null;
    }

    @Override
    public Void visitAssignExpression(BSharpParser.AssignExpressionContext ctx) {
        visit(ctx.right);

        // Duplicate the value if it is needed after the assignment.
        if (!(ctx.getParent().getRuleContext() instanceof BSharpParser.ExpressionStatementContext)) {
            jasminBytecode.add("dup");
        }

        if (ctx.left.getRuleContext() instanceof BSharpParser.PrimaryExpressionContext primaryExpression) {
            storeOrLoadVar(primaryExpression.IDENTIFIER(), false);
        } else {
            throw new UnsupportedOperationException();
        }

        return null;
    }

    @Override
    public Void visitBangExpression(BSharpParser.BangExpressionContext ctx) {
        visit(ctx.expression());

        String label1 = getUniqueLabel();
        String label2 = getUniqueLabel();

        jasminBytecode.add(String.format("ifne %s", label1))
                .add("iconst_1")
                .add(String.format("goto %s", label2))
                .add(String.format("%s:", label1))
                .add("iconst_0")
                .add(String.format("%s:", label2));

        return null;
    }

    @Override
    public Void visitParameterExpression(BSharpParser.ParameterExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Void visitMethodCall(BSharpParser.MethodCallContext ctx) {
        if (ctx.buildInMethodCalls() != null) {
            visit(ctx.buildInMethodCalls());
        } else if (ctx.IDENTIFIER() != null) {

            jasminBytecode.add("aload_0");

            if (ctx.expressions() != null) {
                visit(ctx.expressions());
            }

            Method method = findMethod(ctx.IDENTIFIER());
            jasminBytecode.appendText(String.format("invokevirtual %s.", jasminBytecode.getClassName()))
                    .appendText(ctx.IDENTIFIER().getText())
                    .appendText('(');

            // Add the method argument types.
            for (DataType argType : method.args()) {
                jasminBytecode.appendText(getOutputTypeCode(argType, true));
            }

            jasminBytecode.appendText(')')
                    .appendText(getOutputTypeCode(method.type(), true))
                    .buildLine();
        } else {
            throw new UnsupportedOperationException();
        }

        return null;
    }

    // endregion

    // region Primitives

    @Override
    public Void visitIntPrimitive(BSharpParser.IntPrimitiveContext ctx) {
        jasminBytecode.appendText(getOutputTypeCode(DataType.INT, true));
        return null;
    }

    @Override
    public Void visitBoolPrimitive(BSharpParser.BoolPrimitiveContext ctx) {
        jasminBytecode.appendText(getOutputTypeCode(DataType.BOOL, true));
        return null;
    }

    @Override
    public Void visitStringPrimitive(BSharpParser.StringPrimitiveContext ctx) {
        jasminBytecode.appendText(getOutputTypeCode(DataType.STRING, true));
        return null;
    }

    @Override
    public Void visitFloatPrimitive(BSharpParser.FloatPrimitiveContext ctx) {
        jasminBytecode.appendText(getOutputTypeCode(DataType.FLOAT, true));
        return null;
    }

    @Override
    public Void visitCharPrimitive(BSharpParser.CharPrimitiveContext ctx) {
        jasminBytecode.appendText(getOutputTypeCode(DataType.CHAR, true));
        return null;
    }

    // endregion

    @Override
    public Void visitLiteral(BSharpParser.LiteralContext ctx) {
        if (ctx.BOOL_LITERAL() != null) {
            switch (ctx.BOOL_LITERAL().getText()) {
                case "true" -> jasminBytecode.add("iconst_1");
                case "false" -> jasminBytecode.add("iconst_0");
            }
        } else if (ctx.STRING_LITERAL() != null || ctx.DECIMAL_LITERAL() != null) {
            jasminBytecode.add(String.format("ldc %s", ctx.content.getText()));
        } else if (ctx.CHAR_LITERAL() != null) {
            jasminBytecode.add(String.format("bipush %s", (byte) ctx.content.getText().charAt(1)));
        } else if (ctx.DIGIT_LITERAL() != null) {
            addNumToStack(ctx);
        } else {
            throw new UnsupportedOperationException();
        }

        return null;
    }

    private void addNumToStack(BSharpParser.LiteralContext ctx) {
        long num = Long.parseLong(ctx.content.getText());

        if (num >= -1 && num <= 5) {
            if (num == -1) jasminBytecode.add("iconst_m1");
            else jasminBytecode.add(String.format("iconst_%s", num));
            return;
        }

        if (num >= Byte.MIN_VALUE && num <= Byte.MAX_VALUE) {
            jasminBytecode.appendText("bipush");
        } else if (num >= Short.MIN_VALUE && num <= Short.MAX_VALUE) {
            jasminBytecode.appendText("sipush");
        } else {
            jasminBytecode.appendText("ldc");
        }

        jasminBytecode.appendText(String.format(" %s", num))
                .buildLine();
    }

    /**
     * Get a unique label.
     * Format: L{increment}
     *
     * @return A new unique label.
     */
    private String getUniqueLabel() {
        return String.format("L%s", labelCounter++);
    }

    /**
     * Add the byte code to store or load a variable.
     *
     * @param identifier The {@link TerminalNode} of the variable.
     * @param isLoad     whether the variable should be loaded or stored.
     */
    private void storeOrLoadVar(TerminalNode identifier, boolean isLoad) {
        Symbol symbol = currentScope.findSymbol(identifier);

        if (!(symbol instanceof VarSymbol var)) {
            throw new CompilerException("Symbol was not a variable");
        }

        jasminBytecode.appendText(getTypeCode(symbol.getType()))
                .appendText(isLoad ? "load" : "store");

        jasminBytecode.appendText(var.getLocalSlot() <= 3 ? '_' : ' ')
                .appendText(var.getLocalSlot())
                .buildLine();
    }

    /**
     * Get a type code used for outputs.
     *
     * @param type The {@link DataType}.
     * @return The type code {@link String}.
     */
    private String getOutputTypeCode(DataType type) {
        return getOutputTypeCode(type, false);
    }

    /**
     * Get a type code used for outputs.
     *
     * @param type   The {@link DataType}.
     * @param isCaps Whether the type code needs to be all caps.
     * @return The type code {@link String}.
     */
    private String getOutputTypeCode(DataType type, boolean isCaps) {
        String typeCode = switch (type) {
            case INT -> "i";
            case BOOL -> "z";
            case FLOAT -> "f";
            case STRING -> "Ljava/lang/String;";
            case CHAR -> "c";
            case VOID -> "v";
            default -> throw new UnsupportedOperationException();
        };

        return isCaps && type != DataType.STRING ? typeCode.toUpperCase(Locale.ROOT) : typeCode;
    }

    /**
     * Get the Type Code {@link String} for a specific {@link DataType}.
     *
     * @param type The {@link DataType}.
     * @return The type code {@link String}.
     */
    private String getTypeCode(DataType type) {
        return switch (type) {
            case INT, BOOL, CHAR -> "i";
            case FLOAT -> "f";
            case STRING -> "a";
            case VOID -> "";
            default -> throw new UnsupportedOperationException();
        };
    }
}
