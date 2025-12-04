package nl.uni.cos.OutPutTests;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ReadConsoleTests extends OutputTestBase {

    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> outputTextParamSource() {
        return Stream.of(
                arguments("ReadConsoleInt", "2", new String[]{"2"}),
                arguments("ReadConsoleBool", "false", new String[]{"false"}),
                arguments("ReadConsoleBool", "true", new String[]{"true"}),
                arguments("ReadConsoleLine", "Hello World!", new String[]{"Hello World!"}),
                arguments("ReadConsoleFloat", "123.123", new String[]{"123.123"})
        );
    }

    @Override
    String getDir() {
        return "ReadConsoles";
    }
}
