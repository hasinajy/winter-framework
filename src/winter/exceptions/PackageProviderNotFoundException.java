package winter.exceptions;

public class PackageProviderNotFoundException extends Exception {
    public PackageProviderNotFoundException(String msg) {
        super(msg);
    }

    public PackageProviderNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
