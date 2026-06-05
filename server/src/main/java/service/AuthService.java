package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import service.exceptions.InvalidAuthTokenException;

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

    public void delAuth(String authToken) throws DataAccessException, InvalidAuthTokenException{
        authDAO.deleteAuth(authToken);
    }

    public String getUsername(String authToken) throws DataAccessException, InvalidAuthTokenException {
        if(verifyAuth(authToken)){
            return authDAO.getAuth(authToken).username();
        }
        return null;
    }

}
