package dataaccess;

import model.AuthData;
import service.exceptions.InvalidAuthTokenException;

import java.util.ArrayList;

public class AuthMemoryDAO implements AuthDAO{
    ArrayList<AuthData> auths;

    public AuthMemoryDAO(){
        auths = new ArrayList<>();
    }

    //Create a new authorization.
    public AuthData createAuth(String username) {
        String authToken = AuthDAO.generateAuthToken();
        AuthData authdata = new AuthData(authToken, username);
        auths.add(authdata);
        return authdata;
    }

    //Retrieve an authorization given an authToken.
    public AuthData getAuth(String authToken) {
        for(AuthData a : auths){
            if(a.authToken().equals(authToken)){
                return a;
            }
        }
        return null;
    }

    //Delete an authorization so that it is no longer valid.
    public void deleteAuth(String authToken) throws InvalidAuthTokenException{
        boolean success = auths.removeIf(a -> a.authToken().equals(authToken));
        if(!success){
            throw new InvalidAuthTokenException("Authorization Does Not Exist");
        }
    }

    //delete all authorizations
    public void clearAuth() {
        auths.clear();
    }
}
