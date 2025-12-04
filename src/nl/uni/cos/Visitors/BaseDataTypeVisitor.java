package nl.uni.cos.Visitors;

import nl.uni.cos.BSharpParser;
import nl.uni.cos.Models.DataType;

/**
 * The base for all {@link BSharpVisitor} that use {@link DataType} as the return value.
 */
public abstract class BaseDataTypeVisitor extends BSharpVisitor<DataType> {

    // region Build in methods

    @Override
    public DataType visitReadConsoleInt(BSharpParser.ReadConsoleIntContext ctx) {
        return DataType.INT;
    }

    @Override
    public DataType visitReadConsoleLine(BSharpParser.ReadConsoleLineContext ctx) {
        return DataType.STRING;
    }

    @Override
    public DataType visitReadConsoleFloat(BSharpParser.ReadConsoleFloatContext ctx) {
        return DataType.FLOAT;
    }

    @Override
    public DataType visitReadConsoleBool(BSharpParser.ReadConsoleBoolContext ctx) {
        return DataType.BOOL;
    }

    @Override
    public DataType visitRandomInt(BSharpParser.RandomIntContext ctx) {
        return DataType.INT;
    }

    // endregion

    // region Primitives

    @Override
    public DataType visitBoolPrimitive(BSharpParser.BoolPrimitiveContext ctx) {
        return DataType.BOOL;
    }

    @Override
    public DataType visitIntPrimitive(BSharpParser.IntPrimitiveContext ctx) {
        return DataType.INT;
    }

    @Override
    public DataType visitStringPrimitive(BSharpParser.StringPrimitiveContext ctx) {
        return DataType.STRING;
    }

    @Override
    public DataType visitFloatPrimitive(BSharpParser.FloatPrimitiveContext ctx) {
        return DataType.FLOAT;
    }

    @Override
    public DataType visitCharPrimitive(BSharpParser.CharPrimitiveContext ctx) {
        return DataType.CHAR;
    }

    // endregion

    @Override
    public DataType visitLiteral(BSharpParser.LiteralContext ctx) {

        DataType literalType;
        if (ctx.STRING_LITERAL() != null) {
            literalType = DataType.STRING;
        } else if (ctx.DIGIT_LITERAL() != null) {
            literalType = DataType.INT;
        } else if (ctx.DECIMAL_LITERAL() != null) {
            literalType = DataType.FLOAT;
        } else if (ctx.BOOL_LITERAL() != null) {
            literalType = DataType.BOOL;
        } else if (ctx.CHAR_LITERAL() != null) {
            literalType = DataType.CHAR;
        } else {
            throw new UnsupportedOperationException();
        }

        return literalType;
    }

}
