package service;

public class ErrorResult implements Result {
    Exception Error;
    String message;

    public ErrorResult(Exception error, String message){
        Error = error;
        this.message = message;
    }
}
