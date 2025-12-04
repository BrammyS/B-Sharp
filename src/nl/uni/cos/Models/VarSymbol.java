package nl.uni.cos.Models;

public class VarSymbol extends Symbol {
    private final int localSlot;
    private boolean isInitialized;

    /**
     * Initialize a new {@link VarSymbol}.
     *
     * @param name The name of the {@link VarSymbol}.
     * @param type The {@link DataType} of the {@link VarSymbol}.
     * @param localSlot The slot of where the {@link VarSymbol} is located on the stack.
     * @param isInitialized Whether the {@link VarSymbol} is initialized.
     */
    public VarSymbol(String name, DataType type, int localSlot, boolean isInitialized) {
        super(name, type);
        this.localSlot = localSlot;
        this.isInitialized = isInitialized;
    }

    public int getLocalSlot() {
        return localSlot;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }
}
