package nl.uni.cos.OutPutTests;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class WhileStatementTests extends OutputTestBase {

    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> outputTextParamSource() {
        return Stream.of(
                arguments("WhileStatement1", "1", new String[]{"Hello World!"}),
                arguments("WhileStatement1", "2", new String[]{
                        "Hello World!",
                        "Hello World!"
                }),
                arguments("WhileStatement1", "5", new String[]{
                        "Hello World!",
                        "Hello World!",
                        "Hello World!",
                        "Hello World!",
                        "Hello World!"
                }),
                arguments("LinearCongruentialGenerator", "", new String[]{"4", "1", "6", "0", "3", "5", "4", "1", "6", "0"})
        );
    }

    @Override
    String getDir() {
        return "WhileStatements";
    }
}
