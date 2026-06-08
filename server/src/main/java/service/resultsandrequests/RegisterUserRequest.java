package service.resultsandrequests;

import model.UserData;

public record RegisterUserRequest(String username, String password, String email, boolean sql) implements Request {
    public RegisterUserRequest(String username, String password, String email){
        this(username, password, email, false);
    }

    public UserData toUserData(){
        return new UserData(username(), password(), email());
    }

    @Override
    public boolean existingFields() {
        return username != null && password != null && email != null && !username.isBlank() && !password.isBlank() && !email.isBlank();
    }
}
