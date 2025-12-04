package nl.uni.cos;

import nl.uni.cos.Exceptions.AssembleException;
import nl.uni.cos.Services.Implementation.ConsoleLogger;
import nl.uni.cos.Services.Logger;
import nl.uni.cos.Visitors.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Compiles source code in a custom language into Jasmin and then assembles a
 * JVM-compatible .class file.
 * <p>
 */
public class Compiler {
    private final Logger logger = new ConsoleLogger(this.getClass().getName());

    /**
     * The number of errors detected by the lexer and parser.
     */
    private int errorCount = 0;

    /**
     * Main method.
     *
     * @param args Array of command line arguments. You can use this to supply the file name to
     *             compile.
     */
    public static void main(String[] args) {
        try {
            Compiler compiler = new Compiler();

            // Check that the user supplied a name of the source file
            if (args.length == 0) {
                System.err.println("Usage: java Compiler <name of source>");
                return;
            }

            // Split the file name
            // It first strips the extension, so that: tests/myFile.exlang becomes tests/myFile.
            // Then, it removes everything that seems a path, so we end up with just 'myFile' as
            // the class name.
            Path sourceCodePath = Paths.get(args[0]);

            String sourceFileName = sourceCodePath.getFileName().toString();
            int dotIndex = sourceFileName.lastIndexOf('.');
            String className = sourceFileName.substring(0, dotIndex == -1 ? sourceFileName.length() : dotIndex);

            // Read the file and compile it.
            JasminBytecode jasminBytecode = compiler.compileFile(sourceCodePath.toString(), className);
            if (jasminBytecode == null) {
                System.err.println("No Jasmin output");
                return;
            }

            // Determine which directory to write files to
            Path targetDirectory = sourceCodePath.getParent();
            if (targetDirectory == null) {
                targetDirectory = Paths.get(".");
            }

            // Write Jasmin-code to a file
            String jasminFilename = targetDirectory.resolve(className + ".j").toString();
            jasminBytecode.writeJasminToFile(jasminFilename);

            // Try to assemble the Jasmin byte code and write that to a file
            AssembledClass assembledClass = AssembledClass.assemble(jasminBytecode);
            String classFilename = targetDirectory.resolve(className + ".class").toString();
            assembledClass.writeClassToFile(classFilename);
        } catch (IOException | AssembleException e) {
            System.err.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Compiles a complete source code file.
     *
     * @param inputPath Path to the source code to compile.
     * @param className Name of the class to create.
     * @throws IOException       if files could not be read or written
     * @throws AssembleException if Jasmin code was not valid
     */
    public JasminBytecode compileFile(String inputPath, String className)
            throws IOException, AssembleException {
        return compile(CharStreams.fromFileName(inputPath), className);
    }

    /**
     * Compiles a string.
     *
     * @param sourceCode The source code to compile.
     * @param className  Name of the class to create.
     */
    public JasminBytecode compileString(String sourceCode, String className) {
        return compile(CharStreams.fromString(sourceCode), className);
    }

    /**
     * Compiles a file. The source code is lexed (turned into tokens), parsed (a parse tree
     * created) then Jasmin code is generated and assembled into a class.
     *
     * @param input     Stream to the source code input.
     * @param className Name of the class to create.
     */
    private JasminBytecode compile(CharStream input, String className) {
        // Phase 1/2: Run the lexer and parser
        ParseTree parseTree = runLexerAndParser(input);

        // ANTLR tries to do its best in creating a parse tree, even if the source code contains
        // errors. So, check if that is the case and bail out if so.
        if (errorCount > 0)
            return null;

        // Phase 3: Check the source code for semantic errors
        if (!runChecker(parseTree))
            return null;

        // Phase 4: Generate code
        return generateCode(parseTree, className);
    }

    /**
     * Takes the character input and turn it into tokens according to the grammar.
     * Then, tries to form a parse tree from the given tokens. In case of errors, the error listener is
     * called, but the parser still tries to create a parse tree.
     *
     * @param input The input
     * @return A parse tree
     */
    private ParseTree runLexerAndParser(CharStream input) {
        BSharpLexer lexer = new BSharpLexer(input);
        lexer.addErrorListener(getErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        BSharpParser parser = new BSharpParser(tokens);
        parser.addErrorListener(getErrorListener());
        return parser.program();
    }

    /**
     * Called to check if the source code was semantically correct. This method is only called when
     * there were no syntax errors.
     *
     * @param parseTree The parse tree generated by the parser
     * @return True if all code is semantically correct
     */
    private boolean runChecker(ParseTree parseTree) {

        visit(new MethodDeclarationVisitor(), parseTree);
        visit(new TypeScopeBuilder(), parseTree);
        visit(new CodeChecker(), parseTree);

        return true;
    }

    /**
     * Generate the Jasmin code for the source code. This method is only called after checking that
     * the code is syntactically and semantically correct, so you need not check for any errors.
     *
     * @param parseTree The parseTree to generate code for
     * @return All Jasmin code that is generated
     */
    private JasminBytecode generateCode(ParseTree parseTree, String className) {
        JasminBytecode jasminBytecode = new JasminBytecode(className);

        // Generate code from the parse tree.
        visit(new CodeGenerator(jasminBytecode), parseTree);

        return jasminBytecode;
    }

    /**
     * Creates and returns an error listener for use in the lexer and parser that just increases
     * the errorCount-attribute in this class so we can find out if the source code had a syntax
     * error.
     *
     * @return An error listener for use with lexer.addErrorListener() and parser.addErrorListener()
     */
    private ANTLRErrorListener getErrorListener() {
        return new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine,
                                    String msg, RecognitionException e) {
                errorCount++;
            }
        };
    }

    private <T extends BSharpVisitor<G>, G> void visit(T visitor, ParseTree parseTree) {
        long start = System.nanoTime();
        visitor.visit(parseTree);
        long finish = System.nanoTime();
        float timeElapsed = finish - start;

        logger.log(String.format("Ran %s in %.2fms.", visitor.getClass().getSimpleName(), timeElapsed / 1000000));
    }
}
