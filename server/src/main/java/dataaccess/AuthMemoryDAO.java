package dataaccess;

import model.AuthData;
import server.InvalidRequestException;
import service.exceptions.InvalidAuthTokenException;

import java.util.ArrayList;

public class AuthMemoryDAO implements AuthDAO{
    ArrayList<AuthData> auths;

    public AuthMemoryDAO(){
        auths = new ArrayList<>();
    }

    //Create a new authorization.
    public AuthData createAuth(String username) throws InvalidRequestException {
        if(username == null || username.isBlank()){
            throw new InvalidRequestException("Expecting Username");
        }
        String authToken = AuthDAO.generateAuthToken();
        AuthData authdata = new AuthData(authToken, username);
        auths.add(authdata);
        return authdata;
    }

    //Retrieve an authorization given an authToken.
    public AuthData getAuth(String authToken) throws DataAccessException{
        for(AuthData a : auths){
            if(a.authToken().equals(authToken)){
                return a;
            }
        }
        throw new DataAccessException("Unable to get authentication");
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

    //return num of authorizations
    @Override
    public int numAuths(){
        return auths.size();
    }
}
