package nl.uni.cos.CodeCheckerTests;

import nl.uni.cos.OutPutTests.OutputTestBase;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static nl.uni.cos.Models.ErrorMessageTemplates.MissingReturnTemp;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MissingReturnErrorTests extends CodeCheckerErrorTestBase {
    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> codeCheckerParamSource() {
        return Stream.of(
                arguments("MissingReturn1", new String[]{"Line 7 - " + String.format(MissingReturnTemp.getTemplate(), "Number")}),
                arguments("MissingReturn2", new String[]{"Line 9 - " + String.format(MissingReturnTemp.getTemplate(), "Number")})
        );
    }

    @Override
    String getDir() {
        return "MissingReturnStatements";
    }
}
