package dataaccess;

import model.UserData;
import server.InvalidRequestException;

public interface UserDAO {
    //Create a new user.
    void createUser(UserData user) throws DataAccessException, InvalidRequestException;
    //Retrieve a user with the given username.
    UserData getUser(String username) throws DataAccessException, InvalidRequestException;
    //clear all users
    void clearUsers() throws DataAccessException;
    //return number of users
    int getNumUsers() throws DataAccessException;
    //return if passwords match
    boolean pwEquals(String inputPW, String storedPW);
}
