package nl.uni.cos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class JasminBytecode {
    /**
     * After successful compilation, this contains all Jasmin commands.
     */
    private final LinkedList<String> jasminCode;

    /**
     * Name of the compiled class
     */
    private final String className;

    /**
     * The {@link StringBuilder} that will build a single line.
     */
    private final StringBuilder lineBuilder = new StringBuilder();

    /**
     * Create an instance with an empty list of Jasmin lines.
     */
    public JasminBytecode(String className) {
        this.className = className;
        this.jasminCode = new LinkedList<>();
    }

    /**
     * Create an instance that keeps track of the compiled Jasmin code. You can
     * pass this to AssembledClass::assemble() to actually build a class file
     * from this.
     *
     * @param className  The name of the class that was compiled.
     * @param jasminCode A list of Jasmin instructions.
     */
    public JasminBytecode(String className, List<String> jasminCode) {
        this.className = className;
        this.jasminCode = new LinkedList<>(jasminCode);
    }

    /**
     * Add an empty line to this byte code.
     */
    public JasminBytecode add() {
        return add("");
    }

    /**
     * Add a line of Jasmin code.
     *
     * @param line A single line of Jasmin code.
     * @return A reference to this object, so that you can chain calls to add like this:
     * <pre>
     *              jasminCode.add( "ldc 3" )
     *                        .add( "ldc 5" )
     *                        .add( "iadd" );
     *              </pre>
     */
    public JasminBytecode add(String line) {
        jasminCode.add(line);
        return this;
    }

    /**
     * Add text to the current line that is being build.
     *
     * @param text The text that will be added to the current line.
     * @return A reference to this object, so that you can chain calls to add like this:
     * <pre>
     *              jasminCode.addText( ".method public static" )
     *                        .addText( "main" )
     *                        .addText( "(" )
     *                        .addText( "[Ljava/lang/String" )
     *                        .addText( ")" )
     *                        .addText( "V" );
     *              </pre>
     */
    public JasminBytecode appendText(String text) {
        lineBuilder.append(text);
        return this;
    }

    /**
     * Add a character to the current line that is being build.
     *
     * @param character The character that will be added to the current line.
     * @return A reference to this object, so that you can chain calls to add like this:
     * <pre>
     *              jasminCode.addText( ".method public static" )
     *                        .addText( "main" )
     *                        .addText( "(" )
     *                        .addText( "[Ljava/lang/String" )
     *                        .addText( ")" )
     *                        .addText( "V" );
     *              </pre>
     */
    public JasminBytecode appendText(char character) {
        lineBuilder.append(character);
        return this;
    }

    /**
     * Add a number to the current line that is being build.
     *
     * @param num The number that will be added to the current line.
     * @return A reference to this object, so that you can chain calls to add like this:
     * <pre>
     *              jasminCode.addText( ".method public static" )
     *                        .addText( "main" )
     *                        .addText( "(" )
     *                        .addText( "[Ljava/lang/String" )
     *                        .addText( ")" )
     *                        .addText( "V" );
     *              </pre>
     */
    public JasminBytecode appendText(int num) {
        lineBuilder.append(num);
        return this;
    }

    /**
     * Build the current line.
     */
    public void buildLine() {
        add(lineBuilder.toString());
        lineBuilder.setLength(0);
    }

    /**
     * Add a whole number of lines to this JasminBytecode.
     * Pro-tip: you can also use this to combine two JasminBytecode instances:
     * <pre>
     *     jasminCode1.addAll( jasminCode2.getLines() );
     * </pre>
     *
     * @param lines The lines to add.
     * @return A reference to this object, so you can chain calls.
     * @see #add(String)
     */
    public JasminBytecode addAll(List<String> lines) {
        jasminCode.addAll(lines);
        return this;
    }

    public JasminBytecode addScannerInit() {
        jasminCode.add("new java/util/Scanner");
        jasminCode.add("dup");
        jasminCode.add("getstatic java/lang/System.in Ljava/io/InputStream;");
        jasminCode.add("invokespecial java/util/Scanner.<init>(Ljava/io/InputStream;)V");

        return this;
    }

    /**
     * Write the jasmin byte code (in text form) to a file. You can use this to
     * debug your code.
     *
     * @param jasminFileName Path to write the Jasmin code to.
     * @throws IOException if the file could not be written, e.g. because of
     *                     security rights.
     */
    public void writeJasminToFile(String jasminFileName) throws IOException {
        PrintWriter jasminOut = new PrintWriter(new FileWriter(jasminFileName));
        for (String line : jasminCode)
            jasminOut.println(line);
        jasminOut.close();
    }

    public String getClassName() {
        return className;
    }

    public List<String> getLines() {
        return jasminCode;
    }
}
