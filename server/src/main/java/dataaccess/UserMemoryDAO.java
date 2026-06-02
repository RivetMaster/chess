package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class UserMemoryDAO implements  UserDAO{

    ArrayList<UserData> users;

    public UserMemoryDAO(){
        users = new ArrayList<>();
    }

    //Create a new user.
    public void createUser(UserData user) {
        users.add(user);
    }

    //Retrieve a user with the given username.
    public UserData getUser(String username) {
        for(UserData u : users){
            if(u.username().equals(username)){
                return u;
            }
        }
        return null;
    }

    //clear all users
    public void clearUsers() {
        users.clear();
    }

}
