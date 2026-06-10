package resultsandrequests;

public record RegisterUserResult(String username, String authToken) implements Result{
}
