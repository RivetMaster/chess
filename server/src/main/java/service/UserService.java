package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.*;
import service.exceptions.*;
import service.resultsandrequests.*;


import java.util.ArrayList;

public class UserService {

    private final AuthService authServ;

    private final ArrayList<UserData> users;

    public UserService(AuthDAO authDAO){
        authServ = new AuthService(authDAO);
        users = new ArrayList<>();
    }

    //register new user in database with login  info, and log them in.
    public registerUserResult register(registerUserRequest req) throws DataAccessException, UnavailableException, InvalidLogInException {
        if(getUser(req.username()) != null) {
            throw new UnavailableException("Username Taken");
        }
        users.add(new UserData(req.username(), req.password(), req.email()));
        logInResult log = logIn(new logInRequest(req.username(), req.password()));
        return new registerUserResult(log.username(), log.authToken());
    }

    //login user if username and password match in database
    public logInResult logIn(logInRequest req) throws DataAccessException, InvalidLogInException {
        for(UserData u : users){
            if(u.username().equals(req.username()) && u.password().equals(req.password())){
                AuthData auth = authServ.addAuth(req.username());
                return new logInResult(req.username(), auth.authToken());
            }
        }
        throw new InvalidLogInException("Invalid Login");
    }

    //log out by deleting authToken from database. Don't delete user from database
    public VoidResult logOut(logOutRequest req) throws DataAccessException, InvalidAuthTokenException {
        authServ.delAuth(req.authToken());
        return new VoidResult();
    }

    public UserData getUser(String username){
        for(UserData u : users){
            if(u.username().equals(username)){
                return u;
            }
        }
        return null;
    }

    public int getNumUsers(){
        return users.size();
    }

    //clear database of all users
    public void clearUsers(){
        users.clear();
    }
}
