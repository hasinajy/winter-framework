package winter.exception;

/**
 * Custom exception for invalid HTTP request verbs.
 */
public class InvalidRequestVerbException extends Exception {

    /**
     * Constructs a new InvalidRequestVerbException with the specified detail
     * message.
     *
     * @param message The detail message, which provides more information about the
     *                invalid verb.
     */
    public InvalidRequestVerbException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidRequestVerbException with the specified detail
     * message and cause.
     *
     * @param message The detail message, which provides more information about the
     *                invalid verb.
     * @param cause   The cause of the exception, which may be another exception.
     */
    public InvalidRequestVerbException(String message, Throwable cause) {
        super(message, cause);
    }
}
