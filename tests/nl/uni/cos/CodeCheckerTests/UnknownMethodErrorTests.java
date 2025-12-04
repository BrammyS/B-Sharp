package nl.uni.cos.CodeCheckerTests;

import nl.uni.cos.OutPutTests.OutputTestBase;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static nl.uni.cos.Models.ErrorMessageTemplates.UnknownMethodTemp;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class UnknownMethodErrorTests extends CodeCheckerErrorTestBase {
    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> codeCheckerParamSource() {
        return Stream.of(
                arguments("UnknownMethod1", new String[]{"Line 2 - " + String.format(UnknownMethodTemp.getTemplate(), "TestMethod")}),
                arguments("UnknownMethod2", new String[]{
                        "Line 2 - " + String.format(UnknownMethodTemp.getTemplate(), "TestMethod"),
                        "Line 3 - " + String.format(UnknownMethodTemp.getTemplate(), "TestMethod"),
                        "Line 4 - " + String.format(UnknownMethodTemp.getTemplate(), "TestMethod")
                })
        );
    }

    @Override
    String getDir() {
        return "UnknownMethods";
    }
}
