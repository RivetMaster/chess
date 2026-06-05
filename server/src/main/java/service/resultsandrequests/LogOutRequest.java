package service.resultsandrequests;


public record LogOutRequest(String authToken) implements Request{

    @Override
    public boolean existingFields() {
        return authToken != null && !authToken.isBlank();
    }
}
