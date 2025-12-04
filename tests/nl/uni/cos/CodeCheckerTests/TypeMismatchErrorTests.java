package nl.uni.cos.CodeCheckerTests;

import nl.uni.cos.Models.DataType;
import nl.uni.cos.OutPutTests.OutputTestBase;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static nl.uni.cos.Models.ErrorMessageTemplates.TypeMismatchTemp;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TypeMismatchErrorTests extends CodeCheckerErrorTestBase {
    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> codeCheckerParamSource() {
        return Stream.of(
                arguments("TypeMismatchCalc1", format(2, DataType.INT, DataType.FLOAT)),
                arguments("TypeMismatchCalc2", format(2, DataType.FLOAT, DataType.INT)),
                arguments("TypeMismatchCalc3", format(2, DataType.FLOAT, DataType.BOOL)),
                arguments("TypeMismatchCalc4", format(2, DataType.INT, DataType.BOOL)),
                arguments("TypeMismatchCalc5", format(2, DataType.BOOL, DataType.INT)),
                arguments("TypeMismatchCalc6", format(2, DataType.BOOL, DataType.FLOAT)),
                arguments("VarTypeMismatch1", format(2, DataType.INT, DataType.STRING)),
                arguments("VarTypeMismatch2", format(2, DataType.INT, DataType.BOOL)),
                arguments("VarTypeMismatch3", format(2, DataType.INT, DataType.FLOAT)),
                arguments("VarTypeMismatch4", format(3, DataType.INT, DataType.STRING)),
                arguments("VarTypeMismatch5", format(4, DataType.STRING, DataType.INT)),
                arguments("TypeMismatchIf1", format(2, DataType.BOOL, DataType.FLOAT)),
                arguments("TypeMismatchIf2", format(2, DataType.BOOL, DataType.STRING)),
                arguments("TypeMismatchIf3", format(2, DataType.BOOL, DataType.INT)),
                arguments("TypeMismatchWhile1", format(2, DataType.BOOL, DataType.FLOAT)),
                arguments("TypeMismatchWhile2", format(2, DataType.BOOL, DataType.STRING)),
                arguments("TypeMismatchWhile3", format(2, DataType.BOOL, DataType.INT))
        );
    }

    @Override
    String getDir() {
        return "TypeMismatch";
    }

    private static String[] format(int line, DataType left, DataType right){
        return new String[]{"Line " + line + " - " + String.format(TypeMismatchTemp.getTemplate(), left, right)};
    }
}
