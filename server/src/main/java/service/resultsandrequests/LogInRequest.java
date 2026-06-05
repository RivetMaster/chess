package service.resultsandrequests;

public record LogInRequest(String username, String password) implements Request{

    @Override
    public boolean existingFields() {
        return username != null && password != null && !username.isBlank() && !password.isBlank();
    }
}
