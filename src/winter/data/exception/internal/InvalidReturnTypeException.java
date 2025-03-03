package winter.data.exception.internal;

/**
 * Indicates that a method’s return type is invalid in the Winter framework.
 * <p>
 * This exception is thrown when a controller method’s return type does not meet
 * the
 * framework’s expectations (e.g., unsupported type for rendering or
 * processing),
 * detected during request handling.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidReturnTypeException extends Exception {

    /**
     * Constructs a new InvalidReturnTypeException with no detail message.
     */
    public InvalidReturnTypeException() {
        super();
    }

    /**
     * Constructs a new InvalidReturnTypeException with the specified detail
     * message.
     *
     * @param msg the detail message explaining why the return type is invalid
     */
    public InvalidReturnTypeException(String msg) {
        super(msg);
    }
}