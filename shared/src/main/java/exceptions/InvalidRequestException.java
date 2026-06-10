package exceptions;

public class InvalidRequestException extends Exception{
    public InvalidRequestException(String message) {
        super(message);
    }
    public InvalidRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}
