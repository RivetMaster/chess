package dataaccess;

import model.AuthData;
import server.InvalidRequestException;
import service.exceptions.InvalidAuthTokenException;

import java.util.UUID;

public interface AuthDAO{
    //Create a new authorization.
    AuthData createAuth(String username) throws DataAccessException, InvalidRequestException;
    //Retrieve an authorization given an authToken.
    AuthData getAuth(String authToken) throws DataAccessException;
    //Delete an authorization so that it is no longer valid.
    void deleteAuth(String authToken) throws DataAccessException, InvalidAuthTokenException;
    //delete all authorizations
    void clearAuth() throws DataAccessException;
    //generate new authorization token
    static String generateAuthToken(){
        return UUID.randomUUID().toString();
    }
    //return number of authorizations
    int numAuths() throws DataAccessException;
}