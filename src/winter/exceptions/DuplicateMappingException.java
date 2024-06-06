package winter.exceptions;

public class DuplicateMappingException extends RuntimeException {
    public DuplicateMappingException(String msg) {
        super(msg);
    }

    public DuplicateMappingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
