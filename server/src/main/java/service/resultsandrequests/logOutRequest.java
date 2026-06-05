package service.resultsandrequests;


public record logOutRequest(String authToken) implements Request{

    @Override
    public boolean existingFields() {
        return authToken != null && !authToken.isBlank();
    }
}
