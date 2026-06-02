package service;

public class ErrorResult implements Result {
    Exception Error;

    public ErrorResult(Exception error){
        Error = error;

    }
}
