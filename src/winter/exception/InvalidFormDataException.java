package winter.exception;

public class InvalidFormDataException extends Exception {
    public InvalidFormDataException() {
        super("Invalid form data provided.");
    }

    public InvalidFormDataException(String message) {
        super(message);
    }

    public InvalidFormDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFormDataException(Throwable cause) {
        super(cause);
    }
}
