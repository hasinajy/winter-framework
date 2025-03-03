package winter.data.exception.client;

/**
 * Indicates that a requested URL mapping was not found in the Winter framework.
 * <p>
 * This exception is thrown when a client request targets a URL that has no
 * corresponding
 * controller method mapping, handled by
 * {@link winter.service.ExceptionHandler}
 * to return a 404 response.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class MappingNotFoundException extends Exception {

    /**
     * Constructs a new MappingNotFoundException with no detail message.
     */
    public MappingNotFoundException() {
        super();
    }

    /**
     * Constructs a new MappingNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining why the mapping was not found
     */
    public MappingNotFoundException(String message) {
        super(message);
    }
}