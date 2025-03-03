package winter.data.exception.initialization;

/**
 * Indicates that a package provider configuration was not found during
 * initialization in the Winter framework.
 * <p>
 * This exception is thrown when the framework cannot locate a required package
 * configuration
 * (e.g., "ControllersPackage" init parameter), during the scanning
 * process by
 * {@link winter.service.ControllerScanner}.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class PackageProviderNotFoundException extends Exception {

    /**
     * Constructs a new PackageProviderNotFoundException with the specified detail
     * message.
     *
     * @param msg the detail message explaining why the package provider was not
     *            found
     */
    public PackageProviderNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new PackageProviderNotFoundException with the specified detail
     * message and cause.
     *
     * @param msg   the detail message explaining why the package provider was not
     *              found
     * @param cause the underlying cause of the exception (e.g., a configuration
     *              error)
     */
    public PackageProviderNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}