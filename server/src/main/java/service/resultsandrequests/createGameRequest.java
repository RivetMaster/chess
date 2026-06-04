package service.resultsandrequests;

public record createGameRequest(String gameName, String authToken) implements Request {
}
