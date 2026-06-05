package service.resultsandrequests;



public record LogInResult(String username, String authToken) implements Result {
}
