package winter.data.exception.internal;

/**
 * Indicates that an error occurred while saving a file in the Winter framework.
 * <p>
 * This exception is thrown when a file operation fails, such as writing
 * uploaded content
 * to disk, typically by {@link winter.data.servletabstraction.File#save}. It
 * wraps I/O-related
 * errors or invalid data conditions.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class FileSaveException extends Exception {

    /**
     * Constructs a new FileSaveException with the specified detail message.
     *
     * @param message the detail message explaining why the file save failed
     */
    public FileSaveException(String message) {
        super(message);
    }

    /**
     * Constructs a new FileSaveException with the specified detail message and
     * cause.
     *
     * @param message the detail message explaining why the file save failed
     * @param cause   the underlying cause of the exception (e.g., an
     *                {@link java.io.IOException})
     */
    public FileSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}