package nl.uni.cos.CodeCheckerTests;

import nl.uni.cos.Models.DataType;
import nl.uni.cos.OutPutTests.OutputTestBase;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static nl.uni.cos.Models.ErrorMessageTemplates.UnsupportedOperatorTemp;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class UnsupportedOperatorErrorTests extends CodeCheckerErrorTestBase {
    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> codeCheckerParamSource() {
        return Stream.of(
                arguments("StringCalculations", new String[]{
                        "Line 2 - " + String.format(UnsupportedOperatorTemp.getTemplate(), DataType.STRING, '+'),
                        "Line 3 - " + String.format(UnsupportedOperatorTemp.getTemplate(), DataType.STRING, '/'),
                        "Line 4 - " + String.format(UnsupportedOperatorTemp.getTemplate(), DataType.STRING, '*'),
                        "Line 5 - " + String.format(UnsupportedOperatorTemp.getTemplate(), DataType.STRING, '%'),
                        "Line 6 - " + String.format(UnsupportedOperatorTemp.getTemplate(), DataType.STRING, '-'),
                }),
                arguments("AndOrExpressions", new String[]{
                        "Line 2 - " + String.format(UnsupportedOperatorTemp.getTemplate(), DataType.INT, "&&"),
                        "Line 3 - " + String.format(UnsupportedOperatorTemp.getTemplate(), DataType.INT, "||"),
                        "Line 4 - " + String.format(UnsupportedOperatorTemp.getTemplate(), DataType.STRING, "&&"),
                        "Line 5 - " + String.format(UnsupportedOperatorTemp.getTemplate(), DataType.STRING, "||"),
                        "Line 6 - " + String.format(UnsupportedOperatorTemp.getTemplate(), DataType.FLOAT, "&&"),
                        "Line 7 - " + String.format(UnsupportedOperatorTemp.getTemplate(), DataType.FLOAT, "||"),
                })
        );
    }

    @Override
    String getDir() {
        return "UnsupportedOperators";
    }
}
