package winter.data.exception.annotation;

/**
 * Indicates that a duplicate URL mapping was detected in the Winter framework.
 * <p>
 * This runtime exception is thrown when a controller method attempts to
 * register a
 * URL mapping that already exists, violating uniqueness constraints. It is
 * raised by {@link winter.data.Mapping#addMethod} during controller scanning or
 * registration.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class DuplicateMappingException extends RuntimeException {

    /**
     * Constructs an exception with a message describing the duplicate mapping.
     *
     * @param msg the detail message explaining the error
     */
    public DuplicateMappingException(String msg) {
        super(msg);
    }

    /**
     * Constructs an exception with a message and a cause for the duplicate mapping.
     *
     * @param msg   the detail message explaining the error
     * @param cause the underlying cause of the exception
     */
    public DuplicateMappingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}