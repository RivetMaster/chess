package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public boolean verifyAuth(String authToken) throws InvalidAuthTokenException, DataAccessException {
        if(authDAO.getAuth(authToken) == null){
            throw new InvalidAuthTokenException("Unauthorized");
        }
        return true;
    }

    public AuthData addAuth(String username) throws DataAccessException{
        return authDAO.createAuth(username);
    }

    public void clearAuths() throws DataAccessException{
        authDAO.clearAuth();
    }

    public void delAuth(AuthData auth) throws DataAccessException{
        authDAO.deleteAuth(auth);
    }

}
