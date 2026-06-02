package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;

public class AuthService {

    AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void verifyAuth(String authToken) throws InvalidAuthTokenException, DataAccessException {
        if(authDAO.getAuth(authToken) == null){
            throw new InvalidAuthTokenException("Unauthorized");
        }
    }
}
