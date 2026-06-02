package dataaccess;

import model.AuthData;
import java.util.UUID;

public interface AuthDAO{
//createAuth: Create a new authorization.
    AuthData createAuth(String username);
//getAuth: Retrieve an authorization given an authToken.
    AuthData getAuth(String authToken);
//deleteAuth: Delete an authorization so that it is no longer valid.
    void deleteAuth(AuthData auth);

    void clearAuth();

    private static String generateAuthToken(){
        return UUID.randomUUID().toString();
    }
}