package resultsandrequests;

public record ListGamesRequest(String authToken) implements Request {

    @Override
    public boolean existingFields() {
        return authToken != null  && !authToken.isBlank();
    }
}
