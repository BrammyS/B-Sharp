package nl.uni.cos.OutPutTests;

import nl.uni.cos.Compiler;
import nl.uni.cos.CompilerTestBase;
import nl.uni.cos.JasminBytecode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Used as a test base for testing a BSharp file.
 * <p>
 * Note: A Static variable of {@link java.util.stream.Stream<Arguments>}
 * with the name of outputTextParamSource needs to be set.
 */
public abstract class OutputTestBase extends CompilerTestBase {
    abstract String getDir();

    @ParameterizedTest
    @MethodSource("outputTextParamSource")
    @DisplayName("Check text output result")
    public void checkOutputText(String file, String input, String[] result) throws Exception {
        String path = String.format("testFiles/%s/%s.bsharp", getDir(), file);

        Compiler c = new Compiler();
        JasminBytecode code = c.compileFile(path, file);
        assertNotNull(code);

        provideInput(input);

        // Check that output matches what we expect
        List<String> output = runCode(code);
        assertArrayEquals(result, output.toArray());
    }
}
