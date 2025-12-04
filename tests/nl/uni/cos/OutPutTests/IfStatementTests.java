package nl.uni.cos.OutPutTests;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class IfStatementTests extends OutputTestBase {

    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> outputTextParamSource() {
        return Stream.of(
                arguments("IfStatement1", "1", new String[]{"Hello World!"}),
                arguments("IfStatement1", "0", new String[]{"Bye World!"})
        );
    }

    @Override
    String getDir() {
        return "IfStatements";
    }
}
