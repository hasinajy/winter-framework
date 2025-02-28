package winter.data.exception.initialization;

public class InvalidPackageNameException extends Exception {
    public InvalidPackageNameException(String msg) {
        super(msg);
    }

    public InvalidPackageNameException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
