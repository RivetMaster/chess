package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exceptions.InvalidAuthTokenException;
import service.exceptions.InvalidLogInException;
import service.exceptions.UnavailableException;
import service.resultsandrequests.LogInRequest;
import service.resultsandrequests.LogOutRequest;
import service.resultsandrequests.RegisterUserRequest;
import service.resultsandrequests.RegisterUserResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static final AuthDAO AUTH_DAO = new AuthMemoryDAO();
    static final UserDAO USER_DAO = new UserMemoryDAO();
    static final UserService SERVICE = new UserService(AUTH_DAO, USER_DAO);

    @BeforeEach
    void clearGames() throws DataAccessException{
        SERVICE.clearUsers();
        AUTH_DAO.clearAuth();
    }

    //log out invalid negative test
    @Test
    void logOutNotIn()  {
        assertThrows(InvalidAuthTokenException.class, () ->
                SERVICE.logOut(new LogOutRequest("Tim")));
    }

    //log in invalid negative test
    @Test
    void logInNotRegistered () {
        assertThrows(InvalidLogInException.class, () ->
                SERVICE.logIn(new LogInRequest("Betty", "1234")));
    }

    //register same username twice fail, negative test
    @Test
    void registerSameNameTwice() throws UnavailableException, DataAccessException, InvalidLogInException {
        SERVICE.register(new RegisterUserRequest("Ann", "5678", "hit@gmail.com"));
        assertThrows(UnavailableException.class, () ->
                SERVICE.register(new RegisterUserRequest("Ann", "1234", "hi@gmail.com")));
    }

    //register user, positive test
    @Test
    void registerUser() throws UnavailableException, DataAccessException, InvalidLogInException {
        RegisterUserRequest userInfo = new RegisterUserRequest("Isaac", "password", "hi@gmail.com");
        SERVICE.register(userInfo);
        assert(SERVICE.getNumUsers() == 1);
        assert(SERVICE.getUser("Isaac").equals(userInfo.toUserData()));
    }

    //clear users, positive test
    @Test
    void clearUsers() throws UnavailableException, DataAccessException, InvalidLogInException {
        RegisterUserRequest userInfo = new RegisterUserRequest("Isaac", "password", "hi@gmail.com");
        RegisterUserRequest userInfo2 = new RegisterUserRequest("Isaiah", "password", "hi@gmail.com");
        SERVICE.register(userInfo);
        SERVICE.register(userInfo2);
        assert(SERVICE.getNumUsers() == 2);
        SERVICE.clearUsers();
        assert(SERVICE.getNumUsers() == 0);
    }

    //log in and log out user positive test
    @Test
    void logInAndOut() throws UnavailableException, DataAccessException, InvalidAuthTokenException, InvalidLogInException {
        AuthService authServ = new AuthService(AUTH_DAO);
        RegisterUserRequest userInfo = new RegisterUserRequest("Bethany", "password", "hi@gmail.com");
        RegisterUserResult auth = SERVICE.register(userInfo);
        assert(SERVICE.getNumUsers() == 1);
        assert(authServ.verifyAuth(auth.authToken()));
        SERVICE.logOut(new LogOutRequest(auth.authToken()));
        assert(SERVICE.getNumUsers() == 1);
        assertThrows(InvalidAuthTokenException.class, () -> authServ.verifyAuth(auth.authToken()));
    }
}
