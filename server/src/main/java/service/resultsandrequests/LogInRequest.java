package service.resultsandrequests;

public record LogInRequest(String username, String password, boolean sql) implements Request{

    public LogInRequest(String username, String password) {
        this(username, password, false);
    }

    @Override
    public boolean existingFields() {
        return username != null && password != null && !username.isBlank() && !password.isBlank();
    }
}
