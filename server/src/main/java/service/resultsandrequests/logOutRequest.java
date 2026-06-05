package service.resultsandrequests;

import model.AuthData;

public record logOutRequest(AuthData auth) implements Request{

    @Override
    public boolean existingFields() {
        return auth != null && auth.username() != null && auth.authToken() != null && !auth.username().isBlank() && !auth.authToken().isBlank();
    }
}
