package service.resultsandrequests;

public record CreateGameRequest(String gameName, String authToken) implements Request {

    public CreateGameRequest setAuthToken(String authToken){
        return new CreateGameRequest(gameName, authToken);
    }

    @Override
    public boolean existingFields() {
        return gameName != null && authToken != null && !gameName.isBlank() && !authToken.isBlank();
    }
}
