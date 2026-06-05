package service;

import dataaccess.AuthDAO;
import dataaccess.AuthMemoryDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exceptions.InvalidAuthTokenException;
import service.exceptions.InvalidLogInException;
import service.exceptions.UnavailableException;
import service.resultsandrequests.logInRequest;
import service.resultsandrequests.logOutRequest;
import service.resultsandrequests.registerUserRequest;
import service.resultsandrequests.registerUserResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static final AuthDAO authDAO = new AuthMemoryDAO();
    static final UserService service = new UserService(authDAO);

    @BeforeEach
    void clearGames() throws DataAccessException{
        service.clearUsers();
        authDAO.clearAuth();
    }

    //log out invalid negative test
    @Test
    void logOutNotIn()  {
        assertThrows(InvalidAuthTokenException.class, () ->
                service.logOut(new logOutRequest(new AuthData("3", "Tim"))));
    }

    //log in invalid negative test
    @Test
    void logInNotRegistered () {
        assertThrows(InvalidLogInException.class, () ->
                service.logIn(new logInRequest("Betty", "1234")));
    }

    //register same username twice fail, negative test
    @Test
    void registerSameNameTwice() throws UnavailableException, DataAccessException, InvalidLogInException {
        service.register(new registerUserRequest("Ann", "5678", "hit@gmail.com"));
        assertThrows(UnavailableException.class, () ->
                service.register(new registerUserRequest("Ann", "1234", "hi@gmail.com")));
    }

    //register user, positive test
    @Test
    void registerUser() throws UnavailableException, DataAccessException, InvalidLogInException {
        registerUserRequest userInfo = new registerUserRequest("Isaac", "password", "hi@gmail.com");
        service.register(userInfo);
        assert(service.getNumUsers() == 1);
        assert(service.getUser("Isaac").equals(userInfo.toUserData()));
    }

    //clear users, positive test
    @Test
    void clearUsers() throws UnavailableException, DataAccessException, InvalidLogInException {
        registerUserRequest userInfo = new registerUserRequest("Isaac", "password", "hi@gmail.com");
        registerUserRequest userInfo2 = new registerUserRequest("Isaiah", "password", "hi@gmail.com");
        service.register(userInfo);
        service.register(userInfo2);
        assert(service.getNumUsers() == 2);
        service.clearUsers();
        assert(service.getNumUsers() == 0);
    }

    //log in and log out user positive test
    @Test
    void logInAndOut() throws UnavailableException, DataAccessException, InvalidAuthTokenException, InvalidLogInException {
        AuthService authServ = new AuthService(authDAO);
        registerUserRequest userInfo = new registerUserRequest("Bethany", "password", "hi@gmail.com");
        registerUserResult auth = service.register(userInfo);
        assert(service.getNumUsers() == 1);
        assert(authServ.verifyAuth(auth.authToken()));
        service.logOut(new logOutRequest(new AuthData(auth.authToken(), "Bethany")));
        assert(service.getNumUsers() == 1);
        assertThrows(InvalidAuthTokenException.class, () -> authServ.verifyAuth(auth.authToken()));
    }
}
