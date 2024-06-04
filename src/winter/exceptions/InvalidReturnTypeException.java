package winter.exceptions;

public class InvalidReturnTypeException extends Exception {
    public InvalidReturnTypeException() {
        super();
    }

    public InvalidReturnTypeException(String msg) {
        super(msg);
    }
}
