package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import server.InvalidRequestException;
import service.exceptions.*;
import service.resultsandrequests.*;


public class UserService {

    private final AuthService authServ;

    private final UserDAO users;

    public UserService(AuthDAO authDAO, UserDAO userDAO){
        authServ = new AuthService(authDAO);
        users = userDAO;
    }

    //register new user in database with login  info, and log them in.
    public RegisterUserResult register(RegisterUserRequest req) throws DataAccessException, UnavailableException, InvalidLogInException {
        try{
            getUser(req.username());
            throw new UnavailableException("Username Taken");
        } catch(DataAccessException e){ //username doesn't exist
            users.createUser(new UserData(req.username(), req.password(), req.email()));
            LogInResult log = logIn(new LogInRequest(req.username(), req.password()));
            return new RegisterUserResult(log.username(), log.authToken());
        }
    }

    //login user if username and password match in database
    public LogInResult logIn(LogInRequest req) throws DataAccessException, InvalidLogInException {
        try{
            UserData u = users.getUser(req.username());
            if(u != null && users.pwEquals(req.password(), u.password()) ){
                AuthData auth = authServ.addAuth(req.username());
                return new LogInResult(req.username(), auth.authToken());
            }
        } catch(DataAccessException | InvalidRequestException e){
            throw new InvalidLogInException("Invalid Login");
        }
        throw new InvalidLogInException("Invalid Login");
    }

    //log out by deleting authToken from database. Don't delete user from database
    public VoidResult logOut(LogOutRequest req) throws DataAccessException, InvalidAuthTokenException {
        authServ.delAuth(req.authToken());
        return new VoidResult();
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.getUser(username);
    }

    public int getNumUsers() throws DataAccessException{
        return users.getNumUsers();
    }

    //clear database of all users
    public void clearUsers() throws DataAccessException {
        users.clearUsers();
    }
}
