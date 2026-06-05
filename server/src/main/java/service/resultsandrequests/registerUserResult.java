package service.resultsandrequests;

public record registerUserResult(String username, String authToken) implements Result{
}
