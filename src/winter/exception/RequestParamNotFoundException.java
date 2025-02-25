package winter.exception;

public class RequestParamNotFoundException extends Exception {
    public RequestParamNotFoundException(String message) {
        super(message);
    }

    public RequestParamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
