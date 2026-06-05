package service.resultsandrequests;

public record logInRequest(String username, String password) implements Request{
}
