package winter.data.exception.initialization;

/**
 * Indicates that an invalid package name was provided during initialization in
 * the Winter framework.
 * <p>
 * This exception is thrown when a package name specified for controller
 * scanning or other
 * initialization tasks does not conform to expected naming conventions,
 * typically detected
 * by {@link winter.service.ControllerScanner} or validated via
 * {@link winter.util.DataUtil#isValidPackageName}.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidPackageNameException extends Exception {

    /**
     * Constructs a new InvalidPackageNameException with the specified detail
     * message.
     *
     * @param msg the detail message explaining why the package name is invalid
     */
    public InvalidPackageNameException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new InvalidPackageNameException with the specified detail
     * message and cause.
     *
     * @param msg   the detail message explaining why the package name is invalid
     * @param cause the underlying cause of the exception (e.g., a parsing error)
     */
    public InvalidPackageNameException(String msg, Throwable cause) {
        super(msg, cause);
    }
}