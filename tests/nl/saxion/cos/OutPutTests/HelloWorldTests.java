package nl.uni.cos.OutPutTests;

import nl.uni.cos.Compiler;
import nl.uni.cos.JasminBytecode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class HelloWorldTests extends OutputTestBase {
    private static final String file = "testFiles/HelloWorld/HelloWorld";

    /**
     * Used by the {@link OutputTestBase}.
     */
    private static Stream<Arguments> outputTextParamSource() {
        return Stream.of(
                arguments("HelloWorld", "", new String[]{"Hello World!"})
        );
    }

    @Override
    String getDir() {
        return "HelloWorld";
    }

    @Test
    void checkHelloWorldByteCode() throws Exception {
        Compiler c = new Compiler();
        JasminBytecode code = c.compileFile(file + ".bsharp", "HelloWorld");
        assertNotNull(code);

        // Check that the bytecode matches what we expect
        List<String> expectedOutput = Files.readAllLines(Paths.get(file + ".expected_j"));
        assertArrayEquals(expectedOutput.toArray(), code.getLines().toArray());
    }
}
