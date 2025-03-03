package winter.data.exception.client;

/**
 * Indicates that an invalid HTTP request verb was encountered in the Winter
 * framework.
 * <p>
 * This exception is thrown when a request uses an HTTP verb (e.g., GET, POST)
 * that is not
 * supported or recognized for a specific mapping, typically detected during
 * request
 * processing.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidRequestVerbException extends Exception {

    /**
     * Constructs a new InvalidRequestVerbException with the specified detail
     * message.
     *
     * @param message the detail message providing more information about the
     *                invalid verb
     */
    public InvalidRequestVerbException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidRequestVerbException with the specified detail
     * message and cause.
     *
     * @param message the detail message providing more information about the
     *                invalid verb
     * @param cause   the underlying cause of the exception (e.g., another exception
     *                leading to this error)
     */
    public InvalidRequestVerbException(String message, Throwable cause) {
        super(message, cause);
    }
}