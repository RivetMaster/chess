package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class AuthMemoryDAO implements AuthDAO{
    ArrayList<AuthData> auths;

    public AuthMemoryDAO(){
        auths = new ArrayList<>();
    }

    //Create a new authorization.
    public AuthData createAuth(String username) throws DataAccessException{
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
        return null;
    }

    //Delete an authorization so that it is no longer valid.
    public void deleteAuth(AuthData auth) throws DataAccessException{
        auths.remove(auth);
    }
    //delete all authorizations
    public void clearAuth() throws DataAccessException{
        auths.clear();
    }
}
