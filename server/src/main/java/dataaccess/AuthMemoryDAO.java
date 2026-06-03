package dataaccess;

import model.AuthData;

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
    public void deleteAuth(AuthData auth) throws DataAccessException{
        if(auths.contains(auth)){
            auths.remove(auth);
        } else{
            throw new DataAccessException("Authorization Does Not Exist");
        }

    }

    //delete all authorizations
    public void clearAuth() {
        auths.clear();
    }
}
