package nl.uni.cos.OutPutTests;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class VarAssignmentsTests extends OutputTestBase {

    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> outputTextParamSource() {
        return Stream.of(
                arguments("VarAssignment1", "", new String[]{"123"}),
                arguments("VarAssignment2", "", new String[]{"123"}),
                arguments("VarAssignment3", "", new String[]{"123"}),
                arguments("VarAssignment4", "", new String[]{
                        "321",
                        "69",
                        "69",
                        "69",
                        "123123123",
                        "123123123",
                        "6754",
                })
        );
    }

    @Override
    String getDir() {
        return "VarAssignments";
    }
}
