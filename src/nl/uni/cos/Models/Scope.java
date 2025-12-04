package nl.uni.cos.Models;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scope {
    private final HashMap<String, Symbol> symbols = new HashMap<>();
    private final Scope parent;
    private int maxLocalsSize = 0, maxStackSize = 0, currentStackSize = 0;
    private boolean hasReturnStatement = false;

    /**
     * Initializes a new {@link Scope}.
     *
     * @param parent The parent {@link Scope}.
     */
    public Scope(Scope parent) {
        this.parent = parent;
    }

    /**
     * Try to find a {@link Symbol}.
     *
     * @param name The {@link TerminalNode} containing the name of the {@link Symbol}.
     * @return The {@link Symbol} that has been found or null.
     */
    public Symbol findSymbol(TerminalNode name) {
        return findSymbol(name.getText());
    }

    /**
     * Try to find a {@link Symbol}.
     *
     * @param name The name of the {@link Symbol}.
     * @return The {@link Symbol} that has been found or null.
     */
    public Symbol findSymbol(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        }

        if (parent == null)
            return null;

        return parent.findSymbol(name);
    }

    /**
     * Count the amount of {@link Symbol} with a specific name.
     *
     * @param name The {@link TerminalNode} containing the name of the {@link Symbol}.
     * @return The amount of {@link Symbol}s with the provided name.
     */
    public int countSymbol(TerminalNode name) {
        return countSymbol(name.getText());
    }


    /**
     * Count the amount of {@link Symbol} with a specific name.
     *
     * @param name The name of the {@link Symbol}.
     * @return The amount of {@link Symbol}s with the provided name.
     */
    public int countSymbol(String name) {
        int counter = 0;
        if (symbols.containsKey(name)) {
            counter++;
        }

        if (parent == null)
            return 0;

        return counter + parent.countSymbol(name);
    }

    /**
     * Enter a child {@link Scope}.
     *
     * @return The new child {@link Scope}.
     */
    public Scope enterScope() {
        Scope childScope = new Scope(this);

        // Don't pass down the max stack size
        if (parent != null)
            childScope.setMaxStackSize(maxStackSize);

        return childScope;
    }

    /**
     * Leave the current {@link Scope} and enter the parent {@link Scope}.
     *
     * @return The parent {@link Scope}.
     */
    public Scope leaveScope() {
        if (parent != null) {
            if (maxStackSize > parent.getMaxStackSize())
                parent.setMaxStackSize(maxStackSize);

            if (findAvailableLocalSlot() > parent.getMaxLocalsSize())
                parent.setMaxLocalsSize(findAvailableLocalSlot());
        }

        return parent;
    }

    /**
     * Find an available local slot on the stack.
     *
     * @return An available local slot position.
     */
    public int findAvailableLocalSlot() {
        int currentScopeSize = 0;

        for (Symbol symbol : symbols.values()) {
            if (symbol instanceof VarSymbol)
                currentScopeSize++;
        }

        if (parent != null) {
            currentScopeSize += parent.findAvailableLocalSlot();
        }

        return currentScopeSize;
    }

    /**
     * Add a {@link MethodSymbol} to the {@link #symbols} table.
     *
     * @param identifier The identifier for the {@link MethodSymbol}.
     * @param type       The {@link DataType} of the {@link MethodSymbol}.
     */
    public void addMethod(TerminalNode identifier, DataType type) {
        addMethod(identifier, type, new ArrayList<>());
    }

    /**
     * Add a {@link MethodSymbol} to the {@link #symbols} table.
     *
     * @param identifier The identifier for the {@link MethodSymbol}.
     * @param type       The {@link DataType} of the {@link MethodSymbol}.
     * @param arg        The parameter {@link DataType} for the {@link MethodSymbol}.
     */
    public void addMethod(TerminalNode identifier, DataType type, DataType arg) {
        ArrayList<DataType> args = new ArrayList<>();
        args.add(arg);

        addMethod(identifier, type, args);
    }

    /**
     * Add a {@link MethodSymbol} to the {@link #symbols} table.
     *
     * @param identifier The identifier for the {@link MethodSymbol}.
     * @param type       The {@link DataType} of the {@link MethodSymbol}.
     * @param args       The {@link List} of parameter {@link DataType}s for the {@link MethodSymbol}.
     */
    public void addMethod(TerminalNode identifier, DataType type, List<DataType> args) {
        addSymbol(new MethodSymbol(identifier.getText(), type, args));
    }

    /**
     * Add a new {@link VarSymbol} to the {@link #symbols} table.
     *
     * @param identifier    The {@link TerminalNode} identifier of the {@link VarSymbol}.
     * @param type          The {@link DataType} of the {@link VarSymbol}.
     * @param isInitialized Whether the {@link VarSymbol} is initialized.
     */
    public void addVar(TerminalNode identifier, DataType type, boolean isInitialized) {
        addVar(identifier.getText(), type, isInitialized);
    }

    /**
     * Add a new {@link VarSymbol} to the {@link #symbols} table.
     *
     * @param identifier    The {@link String} identifier of the {@link VarSymbol}.
     * @param type          The {@link DataType} of the {@link VarSymbol}.
     * @param isInitialized Whether the {@link VarSymbol} is initialized.
     */
    public void addVar(String identifier, DataType type, boolean isInitialized) {
        addSymbol(new VarSymbol(identifier, type, findAvailableLocalSlot(), isInitialized));
    }

    /**
     * Add a new {@link Symbol} to the {@link #symbols} table.
     *
     * @param symbol The new {@link Symbol}.
     */
    private void addSymbol(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }

    /**
     * Whether the current {@link Scope} has a return statement.
     *
     * @return true / false depending on if the scope has a return statement.
     */
    public boolean hasReturnStatement() {
        return hasReturnStatement;
    }

    public void setHasReturnStatement(boolean hasReturnStatement) {
        this.hasReturnStatement = hasReturnStatement;
    }

    public void incrementStack(int amount) {
        currentStackSize += amount;
        if (currentStackSize > maxStackSize)
            maxStackSize = currentStackSize;
    }

    public void incrementStack() {
        incrementStack(1);
    }

    public void decrementStack(int amount) {
        currentStackSize -= amount;
    }

    public void decrementStack() {
        decrementStack(1);
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public int getMaxLocalsSize() {
        return maxLocalsSize;
    }

    public void setMaxLocalsSize(int maxLocalsSize) {
        this.maxLocalsSize = maxLocalsSize;
    }
}
