package nl.uni.cos.Services.Implementation;

import nl.uni.cos.Services.Logger;

import java.util.logging.Level;

/**
 * A Console implementation of the {@link Logger} interface.
 */
public class ConsoleLogger implements Logger {
    private final java.util.logging.Logger LOGGER;

    /**
     * Initializes a new {@link ConsoleLogger}.
     *
     * @param className The class name that is using the {@link ConsoleLogger}.
     */
    public ConsoleLogger(String className) {
        LOGGER = java.util.logging.Logger.getLogger(className);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(String message) {
        LOGGER.log(Level.INFO, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logDebug(String message) {
        LOGGER.log(Level.FINE, message);
    }
}
