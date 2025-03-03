package winter.data.exception.annotation;

/**
 * Indicates that a required annotation was not found in the Winter framework.
 * <p>
 * This exception is thrown when an expected annotation (e.g.,
 * {@link winter.data.annotation.http.RequestParam})
 * is missing from a class, method, field, or parameter during processing, such
 * as by
 * {@link winter.service.ControllerHandler} or
 * {@link winter.data.client.FormData}.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class AnnotationNotFoundException extends Exception {

    /**
     * Constructs an exception with a message describing the missing annotation.
     *
     * @param msg the detail message explaining the error
     */
    public AnnotationNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Constructs an exception with a message and a cause for the missing
     * annotation.
     *
     * @param msg   the detail message explaining the error
     * @param cause the underlying cause of the exception
     */
    public AnnotationNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}