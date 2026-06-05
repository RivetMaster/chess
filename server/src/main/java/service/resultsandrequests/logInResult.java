package service.resultsandrequests;



public record logInResult(String username, String authToken) implements Result {
}
