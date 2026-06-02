package service;

public class InvalidAuthTokenException extends Exception {
    public InvalidAuthTokenException(String message) {
        super(message);
    }
    public InvalidAuthTokenException(String message, Throwable ex) {
        super(message, ex);
    }
}
