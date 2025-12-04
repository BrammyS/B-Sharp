package nl.uni.cos.OutPutTests;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MethodCallsTests extends OutputTestBase {

    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> outputTextParamSource() {
        return Stream.of(
                arguments("MethodCall1", "", new String[]{"Hello World!"}),
                arguments("MethodCall2", "", new String[]{"Hello World 1!", "Hello World 1!", "Hello World 2!", "Hello World 2!"}),
                arguments("MethodCall3", "", new String[]{"Hello World 2!"})
        );
    }

    @Override
    String getDir() {
        return "MethodCalls";
    }
}
