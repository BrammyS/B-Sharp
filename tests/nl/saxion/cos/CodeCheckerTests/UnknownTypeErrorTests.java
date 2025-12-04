package nl.uni.cos.CodeCheckerTests;

import nl.uni.cos.OutPutTests.OutputTestBase;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static nl.uni.cos.Models.ErrorMessageTemplates.UnknownTypeTemp;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class UnknownTypeErrorTests extends CodeCheckerErrorTestBase {
    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> codeCheckerParamSource() {
        return Stream.of(
                arguments("UnknownVarType", new String[]{"Line 2 - " + String.format(UnknownTypeTemp.getTemplate(), "test")})
        );
    }

    @Override
    String getDir() {
        return null;
    }
}
