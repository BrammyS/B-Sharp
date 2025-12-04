package nl.uni.cos.OutPutTests;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class CalculationsTests extends OutputTestBase {

    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> outputTextParamSource() {
        return Stream.of(
                arguments("Calculation1", "", new String[]{"2"}),
                arguments("Calculation2", "", new String[]{"10"}),
                arguments("Calculation3", "", new String[]{"8"}),
                arguments("Calculation4", "", new String[]{"749"}),
                arguments("Calculation5", "", new String[]{"10910"}),
                arguments("Calculation6", "", new String[]{"-123123"}),
                arguments("Calculation7", "", new String[]{"-23123"}),
                arguments("Calculation8", "", new String[]{"2.2"}),
                arguments("Calculation9", "", new String[]{"9.594498"})
        );
    }

    @Override
    String getDir() {
        return "Calculations";
    }
}
