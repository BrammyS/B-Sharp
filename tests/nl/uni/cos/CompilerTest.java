package nl.uni.cos;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * This file shows a few different ways you can do automated tests:
 * <p>
 * - syntaxErrorsAreFound(): Checks that a file with syntax errors stops compilation.
 * <p>
 * Not shown is a test where the file contains no syntax errors, but the checker should find some
 * error. You can of course add that yourself.
 */
class CompilerTest extends CompilerTestBase {

    private static Stream<Arguments> outputTextParamSource() {
        return Stream.of(
                arguments("void Main{}"),
                arguments("void Main()"),
                arguments("void Main(){int test}"),
                arguments("void Main(){ * 1}"),
                arguments("void Main(){\"test\" + 1}"),
                arguments("void Main(){while(if()) WriteLine(1);}")
        );
    }

    @ParameterizedTest
    @MethodSource("outputTextParamSource")
    void syntaxErrorsAreFound(String codeString) throws Exception {
        Compiler c = new Compiler();
        JasminBytecode code = c.compileString(codeString, "IncorrectCode");
        assertNull(code);
    }
}
