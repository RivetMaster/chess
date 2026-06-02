package dataaccess;

import model.UserData;

public interface UserDAO {
    //Create a new user.
    void createUser(UserData user) throws DataAccessException;
    //Retrieve a user with the given username.
    UserData getUser(String username) throws DataAccessException;
    //clear all users
    void clearUsers() throws DataAccessException;
}
