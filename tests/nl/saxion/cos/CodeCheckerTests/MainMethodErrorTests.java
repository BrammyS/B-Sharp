package nl.uni.cos.CodeCheckerTests;

import nl.uni.cos.OutPutTests.OutputTestBase;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static nl.uni.cos.Models.ErrorMessageTemplates.MainParamsTemp;
import static nl.uni.cos.Models.ErrorMessageTemplates.MissingMainTemp;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MainMethodErrorTests extends CodeCheckerErrorTestBase {
    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> codeCheckerParamSource() {
        return Stream.of(
                arguments("MissingMain", new String[]{"Line 1 - " + MissingMainTemp}),
                arguments("MainParameters", new String[]{"Line 1 - " + MainParamsTemp})
        );
    }

    @Override
    String getDir() {
        return "MainMethodErrors";
    }
}
