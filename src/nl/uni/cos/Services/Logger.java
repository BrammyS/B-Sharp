package nl.uni.cos.Services;

/**
 * A simple interface for a logger class.
 */
public interface Logger {
    /**
     * Log a message.
     *
     * @param message The message that will be logged.
     */
    void log(String message);

    /**
     * Log a debug message.
     *
     * @param message The debug message that will be logged.
     */
    void logDebug(String message);
}
