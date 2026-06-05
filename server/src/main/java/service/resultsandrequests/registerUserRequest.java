package service.resultsandrequests;

import model.UserData;

public record registerUserRequest(String username, String password, String email) implements Request {
    public UserData toUserData(){
        return new UserData(username(), password(), email());
    }
}
