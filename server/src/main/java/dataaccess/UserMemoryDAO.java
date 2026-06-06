package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class UserMemoryDAO implements  UserDAO{

    ArrayList<UserData> users;

    public UserMemoryDAO(){
        users = new ArrayList<>();
    }

    //Create a new user.
    public void createUser(UserData user) throws DataAccessException{
        if(user.username() != null && user.password() != null && user.email() != null){
            users.add(user);
        } else{
            throw new DataAccessException("Expecting username, password, and email.");
        }
    }

    //Retrieve a user with the given username.
    public UserData getUser(String username) throws DataAccessException{
        for(UserData u : users){
            if(u.username().equals(username)){
                return u;
            }
        }
        throw new DataAccessException("User Does Not Exist");
    }

    public int getNumUsers(){
        return users.size();
    }

    //clear all users
    public void clearUsers() {
        users.clear();
    }

}
