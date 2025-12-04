package nl.uni.cos.Visitors;

import nl.uni.cos.BSharpParser;
import nl.uni.cos.BSharpParserBaseVisitor;
import nl.uni.cos.Exceptions.CompilerException;
import nl.uni.cos.Models.DataType;
import nl.uni.cos.Models.Error;
import nl.uni.cos.Models.Method;
import nl.uni.cos.Models.Scope;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The Base for all the BSharp visitor classes.
 *
 * @param <T> The type that the visitor methods will need to return.
 */
public abstract class BSharpVisitor<T> extends BSharpParserBaseVisitor<T> {
    /**
     * Contains the {@link DataType} for a specific {@link ParserRuleContext}.
     */
    private static ParseTreeProperty<DataType> types = new ParseTreeProperty<>();
    /**
     * Contains all the {@link Method}s that are present in the BSharp file.
     */
    private static List<Method> methods = new ArrayList<>();
    /**
     * The Errors that have been found in the code.
     */
    private static LinkedList<Error> errors = new LinkedList<>();
    /**
     * Contains all the scopes.
     */
    private static ParseTreeProperty<Scope> scopes = new ParseTreeProperty<>();
    /**
     * The current scope for that the visitor is in.
     */
    protected Scope currentScope;

    /**
     * Resets all the {@link #types}, {@link #methods}, {@link #scopes} and {@link #errors}.
     * This should only be used during unit testing to rest the test environment.
     */
    public static void reset() {
        types = new ParseTreeProperty<>();
        methods = new ArrayList<>();
        scopes = new ParseTreeProperty<>();
        errors = new LinkedList<>();
    }

    /**
     * Visit all the {@link ParserRuleContext}s in a {@link List}.
     *
     * @param parserRuleContexts all the {@link ParserRuleContext} that will be visited.
     * @return An {@link ArrayList} with all the types that were returned by the visitors.
     */
    protected ArrayList<T> visitAll(List<? extends ParserRuleContext> parserRuleContexts) {
        ArrayList<T> returns = new ArrayList<>();
        for (ParserRuleContext parserRuleContext : parserRuleContexts) {
            returns.add(visit(parserRuleContext));
        }

        return returns;
    }

    /**
     * Add an error.
     *
     * @param ctx The {@link ParserRuleContext} of the error.
     * @param msg The message of the error.
     */
    protected void addError(ParserRuleContext ctx, String msg) {
        addError(ctx, msg, false);
    }

    /**
     * Add an error.
     *
     * @param ctx    The {@link ParserRuleContext} of the error.
     * @param msg    The message of the error.
     * @param useEnd Whether the end or start like should be used.
     */
    protected void addError(ParserRuleContext ctx, String msg, boolean useEnd) {
        errors.add(new Error(ctx, msg, useEnd));
    }

    /**
     * Adds a {@link Scope}.
     *
     * @param ctx   The {@link ParseTree} of the current scope.
     * @param scope The Scope.
     */
    protected void addScope(ParseTree ctx, Scope scope) {
        scopes.put(ctx, scope);
    }

    /**
     * Get the {@link Scope} for a {@link ParseTree}/.
     *
     * @param ctx The {@link ParseTree} that is linked to the {@link Scope}.
     * @return The {@link Scope} or null when no {@link Scope} was found.
     */
    protected Scope getScope(ParseTree ctx) {
        return scopes.get(ctx);
    }

    /**
     * Add a new {@link DataType} for a specific {@link ParseTree}.
     *
     * @param ctx  The {@link ParseTree} for the new {@link DataType}.
     * @param type The new {@link DataType}.
     * @return The added {@link DataType}.
     */
    protected DataType addDataType(ParseTree ctx, DataType type) {
        types.put(ctx, type);
        return type;
    }

    protected DataType getDataType(ParseTree ctx) {
        return types.get(ctx);
    }

    /**
     * Add a {@link Method} to the {@link #methods} {@link List}.
     *
     * @param identifier The identifier of the {@link Method}.
     * @param returnType The return {@link DataType} of the {@link Method}.
     * @param args       A {@link List} with the {@link DataType}s of the arguments of the {@link Method}.
     */
    protected void addMethod(TerminalNode identifier, DataType returnType, List<DataType> args) {
        methods.add(new Method(identifier.getText(), returnType, args));
    }

    /**
     * Find a {@link Method} in the {@link #methods} {@link List}.
     *
     * @param identifier The identifier of the {@link Method}.
     * @return The {@link Method} that was found or null.
     */
    protected Method findMethod(TerminalNode identifier) {
        return findMethod(identifier.getText());
    }

    /**
     * Find a {@link Method} in the {@link #methods} {@link List}.
     *
     * @param identifier The identifier of the {@link Method}.
     * @return The {@link Method} that was found or null.
     */
    protected Method findMethod(String identifier) {
        for (Method method : methods) {
            if (method.identifier().equals(identifier)) {
                return method;
            }
        }

        return null;
    }

    /**
     * Count the total amount of {@link Method}s with a specific identifier.
     *
     * @param identifier The identifier of the {@link Method}.
     * @return The amount of methods that have been found.
     */
    protected int getMethodCount(TerminalNode identifier) {
        return getMethodCount(identifier.getText());
    }

    /**
     * Count the total amount of {@link Method}s with a specific identifier.
     *
     * @param identifier The identifier of the {@link Method}.
     * @return The amount of methods that have been found.
     */
    protected int getMethodCount(String identifier) {
        int counter = 0;

        for (Method method : methods) {
            if (method.identifier().equals(identifier)) {
                counter++;
            }
        }

        return counter;
    }

    /**
     * Throws a {@link CompilerException} if one or more errors have been found.
     */
    protected void throwErrors() {
        if (errors.size() == 0)
            return;

        final String messageTemplate = "Line %s - %s\n";

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s Errors found.\n", errors.size()));

        for (Error error : errors) {
            int line = error.useEnd()
                    ? error.ctx().getStop().getLine()
                    : error.ctx().getStart().getLine();
            builder.append(String.format(messageTemplate, line, error.message()));
        }

        throw new CompilerException(builder.toString());
    }

    /**
     * Find the current {@link Scope} for a specific {@link ParseTree}.
     *
     * @param ctx The {@link ParseTree}.
     */
    protected void findScope(ParseTree ctx) {
        currentScope = getScope(ctx);
    }

    /**
     * Leave the current {@link Scope} and get the parent {@link Scope}.
     */
    protected void leaveScope() {
        currentScope = currentScope.leaveScope();
    }

    @Override
    public T visitPrimaryExpression(BSharpParser.PrimaryExpressionContext ctx) {
        if (ctx.literal() != null) {
            return visit(ctx.literal());
        } else if (ctx.methodCall() != null) {
            return visit(ctx.methodCall());
        } else if (ctx.parameterExpression() != null) {
            return visit(ctx.parameterExpression());
        }

        throw new UnsupportedOperationException();
    }
}
