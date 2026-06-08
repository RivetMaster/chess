package service.resultsandrequests;

public record LogInRequest(String username, String password, boolean sql) implements Request{

    @Override
    public boolean existingFields() {
        return username != null && password != null && !username.isBlank() && !password.isBlank();
    }
}
