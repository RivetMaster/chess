package service.resultsandrequests;

public record createGameRequest(String gameName, String authToken) implements Request {

    @Override
    public boolean existingFields() {
        return gameName != null && authToken != null && !gameName.isBlank() && !authToken.isBlank();
    }
}
