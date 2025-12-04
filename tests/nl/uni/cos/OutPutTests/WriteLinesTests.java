package nl.uni.cos.OutPutTests;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class WriteLinesTests extends OutputTestBase {

    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> outputTextParamSource() {
        return Stream.of(
                arguments("WriteLineBool", "", new String[]{"true"}),
                arguments("WriteLineBoolExpressions", "", new String[]
                        {
                                "true",
                                "true",
                                "true",
                                "false",
                                "false",
                                "false",
                                "false",
                                "true"
                        }),
                arguments("WriteLineDouble", "", new String[]{"123.123"}),
                arguments("WriteLineInt", "", new String[]{"123"}),
                arguments("WriteLineString", "", new String[]{"text"}),
                arguments("WriteLineChar", "", new String[]{"a", "b", "c"})
        );
    }

    @Override
    String getDir() {
        return "WriteLines";
    }
}
