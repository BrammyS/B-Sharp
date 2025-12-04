package nl.uni.cos.Models;

public class Symbol {
    private final String name;
    private final DataType type;

    /**
     * Initialize a new {@link Symbol}.
     *
     * @param name The name of the {@link Symbol}.
     * @param type The {@link DataType} of the {@link Symbol}.
     */
    protected Symbol(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }
}
