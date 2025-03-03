package winter.data.exception.client;

/**
 * Indicates that invalid form data was provided in a request to the Winter
 * framework.
 * <p>
 * This exception is thrown when form data fails validation (e.g., incorrect
 * type or missing
 * required fields), during parameter processing by
 * {@link winter.service.ControllerHandler}
 * or {@link winter.util.DataUtil}.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidFormDataException extends Exception {

    /**
     * Constructs a new InvalidFormDataException with a default message.
     * <p>
     * The default message is "Invalid form data provided."
     * </p>
     */
    public InvalidFormDataException() {
        super("Invalid form data provided");
    }

    /**
     * Constructs a new InvalidFormDataException with the specified detail message.
     *
     * @param message the detail message explaining the invalid form data
     */
    public InvalidFormDataException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidFormDataException with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining the invalid form data
     * @param cause   the underlying cause of the exception (e.g., a validation
     *                error)
     */
    public InvalidFormDataException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidFormDataException with the specified cause.
     *
     * @param cause the underlying cause of the exception (e.g., a validation error)
     */
    public InvalidFormDataException(Throwable cause) {
        super(cause);
    }
}