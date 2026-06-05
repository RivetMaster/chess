package service.resultsandrequests;

import model.UserData;

public record registerUserRequest(String username, String password, String email) implements Request {
    public UserData toUserData(){
        return new UserData(username(), password(), email());
    }

    @Override
    public boolean existingFields() {
        return username != null && password != null && email != null && !username.isBlank() && !password.isBlank() && !email.isBlank();
    }
}
