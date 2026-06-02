package dataaccess;

import model.*;

public interface UserDAO {
    //createUser: Create a new user.
    void createUser(UserData user);
    //getUser: Retrieve a user with the given username.
    UserData getUser(String username);
    void clearUsers();
}
