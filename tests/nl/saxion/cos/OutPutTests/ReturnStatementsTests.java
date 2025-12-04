package nl.uni.cos.OutPutTests;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ReturnStatementsTests extends OutputTestBase {

    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> outputTextParamSource() {
        return Stream.of(
                arguments("ReturnBool", "", new String[]{"true"}),
                arguments("ReturnFloat", "", new String[]{"1.123"}),
                arguments("ReturnInt", "", new String[]{"1"}),
                arguments("ReturnString", "", new String[]{"Hello World!"}),
                arguments("ReturnVoid", "", new String[]{""})
        );
    }

    @Override
    String getDir() {
        return "ReturnStatements";
    }
}
