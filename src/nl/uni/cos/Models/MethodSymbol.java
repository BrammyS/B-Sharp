package nl.uni.cos.Models;

import java.util.List;

/**
 * A {@link Method} symbol.
 */
public class MethodSymbol extends Symbol {
    private final List<DataType> args;

    /**
     * Initializes a new {@link MethodSymbol}.
     *
     * @param name The name of the {@link MethodSymbol}.
     * @param type The {@link DataType} of the {@link MethodSymbol}.
     * @param args The arguments in a {@link List} of {@link DataType} for the {@link MethodSymbol}.
     */
    public MethodSymbol(String name, DataType type, List<DataType> args) {
        super(name, type);
        this.args = args;
    }

    /**
     * Get a {@link List} of {@link DataType} of arguments for this  {@link MethodSymbol}.
     *
     * @return The {@link List} of {@link DataType} of arguments for the {@link MethodSymbol}.
     */
    public List<DataType> getArgs() {
        return args;
    }
}
