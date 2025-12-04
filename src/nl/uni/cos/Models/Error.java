package nl.uni.cos.Models;

import nl.uni.cos.Visitors.CodeChecker;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * The model for all {@link Error}s that the {@link CodeChecker} finds.
 * <p>
 * {@link #ctx} The {@link ParserRuleContext} of where the error occurred.
 * {@link #message} The message explaining the {@link Error}.
 * {@link #useEnd} Whether the end of the {@link ParserRuleContext} should be used to get the line number.
 */
public record Error(ParserRuleContext ctx, String message, boolean useEnd) {
}

