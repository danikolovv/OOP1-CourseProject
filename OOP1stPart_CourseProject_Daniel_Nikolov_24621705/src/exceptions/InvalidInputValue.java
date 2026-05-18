package exceptions;

public class InvalidInputValue extends RuntimeException {
    public InvalidInputValue(String message) {
        super(message);
    }
}
