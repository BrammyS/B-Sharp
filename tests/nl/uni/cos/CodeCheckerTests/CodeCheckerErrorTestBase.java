package nl.uni.cos.CodeCheckerTests;

import nl.uni.cos.Compiler;
import nl.uni.cos.CompilerTestBase;
import nl.uni.cos.Exceptions.CompilerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class CodeCheckerErrorTestBase extends CompilerTestBase {
    abstract String getDir();

    @ParameterizedTest
    @MethodSource("codeCheckerParamSource")
    @DisplayName("Check error result")
    public void checkErrorResult(String file, String[] errors) {
        String path;
        if (getDir() == null) {
            path = String.format("testFiles/%s.bsharp", file);
        } else {
            path = String.format("testFiles/%s/%s.bsharp", getDir(), file);
        }

        Compiler c = new Compiler();
        Exception exception = assertThrows(CompilerException.class, () -> c.compileFile(path, file));

        ArrayList<String> expectedErrors = new ArrayList<>();
        expectedErrors.add(String.format("%s Errors found.", errors.length));
        expectedErrors.addAll(Arrays.stream(errors).toList());

        String[] actualErrors = exception.getMessage().split("\n");
        assertArrayEquals(expectedErrors.toArray(), actualErrors);
    }
}
