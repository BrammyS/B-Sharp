package nl.uni.cos.CodeCheckerTests;

import nl.uni.cos.OutPutTests.OutputTestBase;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static nl.uni.cos.Models.ErrorMessageTemplates.DuplicatedMethodNamesTemp;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class DuplicatedMethodNameErrorTests extends CodeCheckerErrorTestBase {
    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> codeCheckerParamSource() {
        return Stream.of(
                arguments("DuplicatedMethodNames1", new String[]{
                        "Line 1 - " + String.format(DuplicatedMethodNamesTemp.getTemplate(), "Main"),
                        "Line 5 - " + String.format(DuplicatedMethodNamesTemp.getTemplate(), "Main")
                }),
                arguments("DuplicatedMethodNames2", new String[]{
                        "Line 1 - " + String.format(DuplicatedMethodNamesTemp.getTemplate(), "Main"),
                        "Line 5 - " + String.format(DuplicatedMethodNamesTemp.getTemplate(), "Main"),
                        "Line 9 - " + String.format(DuplicatedMethodNamesTemp.getTemplate(), "Number"),
                        "Line 13 - " + String.format(DuplicatedMethodNamesTemp.getTemplate(), "Number")
                })
        );
    }

    @Override
    String getDir() {
        return "DuplicatedMethodNames";
    }
}
