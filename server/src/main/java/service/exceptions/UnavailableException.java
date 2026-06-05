package service.exceptions;

public class UnavailableException extends Exception{
    public UnavailableException(String message) {
        super(message);
    }
    public UnavailableException(String message, Throwable ex) {
        super(message, ex);
    }
}
