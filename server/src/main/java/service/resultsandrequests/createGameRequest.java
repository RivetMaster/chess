package service.resultsandrequests;

public record createGameRequest(String gameName, String authToken) implements Request {

    public createGameRequest setAuthToken(String authToken){
        return new createGameRequest(gameName, authToken);
    }

    @Override
    public boolean existingFields() {
        return gameName != null && authToken != null && !gameName.isBlank() && !authToken.isBlank();
    }
}
