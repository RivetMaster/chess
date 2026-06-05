package service.resultsandrequests;

public record listGamesRequest(String authToken) implements Request {

    @Override
    public boolean existingFields() {
        return authToken != null  && !authToken.isBlank();
    }
}
