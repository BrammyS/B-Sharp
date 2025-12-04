package nl.uni.cos.OutPutTests;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class RequirementTests extends OutputTestBase {

    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> outputTextParamSource() {
        return Stream.of(
                arguments("Math", "", new String[]{"10"}),
                arguments("ReadConsoleInt", "6", new String[]{"true"}),
                arguments("ReadConsoleInt", "5", new String[]{"false"}),
                arguments("VarKeyword", "", new String[]{"This is a string value stored in a var."}),
                arguments("IfStatement", "11", new String[]{"You made a mistake", "false"}),
                arguments("IfStatement", "10", new String[]{"false", "false"}),
                arguments("MethodParams", "", new String[]{"Correct"}),
                arguments("whileLoop", "10", new String[]{"Correct guess!"})
        );
    }

    @Override
    String getDir() {
        return "Requirements";
    }
}
