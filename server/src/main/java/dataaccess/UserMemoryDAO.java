package dataaccess;

import model.UserData;
import server.InvalidRequestException;

import java.util.ArrayList;

public class UserMemoryDAO implements  UserDAO{

    ArrayList<UserData> users;

    public UserMemoryDAO(){
        users = new ArrayList<>();
    }

    //Create a new user.
    public void createUser(UserData user) throws InvalidRequestException {
        if(user.verifyFields()){
            users.add(user);
        } else{
            throw new InvalidRequestException("Expecting username, password, and email.");
        }
    }

    //Retrieve a user with the given username.
    public UserData getUser(String username) throws InvalidRequestException{
        for(UserData u : users){
            if(u.username().equals(username)){
                return u;
            }
        }
        throw new InvalidRequestException("User Does Not Exist");
    }

    public int getNumUsers(){
        return users.size();
    }

    //clear all users
    public void clearUsers() {
        users.clear();
    }

    @Override
    public boolean pwEquals(String inputPW, String storedPW){
        return inputPW.equals(storedPW);
    }

}
