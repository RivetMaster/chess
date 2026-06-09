package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import server.InvalidRequestException;
import service.exceptions.*;
import service.resultsandrequests.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserDAO userGetDataAccess(Class<? extends UserDAO> databaseClass) throws DataAccessException {
        UserDAO db;
        if (databaseClass.equals(UserSQLDAO.class)) {
            db = new UserSQLDAO();
        } else {
            db = new UserMemoryDAO();
        }
        db.clearUsers(); //before each clear
        return db;
    }

    private AuthDAO authGetDataAccess(Class<? extends AuthDAO> databaseClass) throws DataAccessException {
        AuthDAO db;
        if (databaseClass.equals(AuthSQLDAO.class)) {
            db = new AuthSQLDAO();
        } else {
            db = new AuthMemoryDAO();
        }
        db.clearAuth(); //before each clear
        return db;
    }

    private static Stream<Arguments> provideClasses() {
        return Stream.of(
                Arguments.of(AuthMemoryDAO.class, UserMemoryDAO.class),
                Arguments.of(AuthSQLDAO.class, UserMemoryDAO.class),
                Arguments.of(AuthMemoryDAO.class, UserSQLDAO.class),
                Arguments.of(AuthSQLDAO.class, UserSQLDAO.class)
        );
    }

    //log out invalid negative test
    @ParameterizedTest
    @MethodSource("provideClasses")
    void logOutNotIn(Class<? extends AuthDAO> adao, Class<? extends UserDAO> udao) throws DataAccessException {
        AuthDAO authDAO = authGetDataAccess(adao);
        UserDAO userDAO = userGetDataAccess(udao);
        UserService service = new UserService(authDAO, userDAO);

        assertThrows(InvalidAuthTokenException.class, () ->
                service.logOut(new LogOutRequest("Tim")));
    }

    //log in invalid negative test
    @ParameterizedTest
    @MethodSource("provideClasses")
    void logInNotRegistered (Class<? extends AuthDAO> adao, Class<? extends UserDAO> udao) throws DataAccessException {
        AuthDAO authDAO = authGetDataAccess(adao);
        UserDAO userDAO = userGetDataAccess(udao);
        UserService service = new UserService(authDAO, userDAO);

        assertThrows(InvalidLogInException.class, () ->
                service.logIn(new LogInRequest("Betty", "1234")));
    }

    //register same username twice fail, negative test
    @ParameterizedTest
    @MethodSource("provideClasses")
    void registerSameNameTwice(Class<? extends AuthDAO> adao, Class<? extends UserDAO> udao)
            throws UnavailableException, DataAccessException, InvalidLogInException, InvalidRequestException {
        AuthDAO authDAO = authGetDataAccess(adao);
        UserDAO userDAO = userGetDataAccess(udao);
        UserService service = new UserService(authDAO, userDAO);

        service.register(new RegisterUserRequest("Ann", "5678", "hit@gmail.com"));
        assertThrows(UnavailableException.class, () ->
                service.register(new RegisterUserRequest("Ann", "1234", "hi@gmail.com")));
    }

    //register user, positive test
    @ParameterizedTest
    @MethodSource("provideClasses")
    void registerUser(Class<? extends AuthDAO> adao, Class<? extends UserDAO> udao)
            throws UnavailableException, DataAccessException, InvalidLogInException, InvalidRequestException {
        AuthDAO authDAO = authGetDataAccess(adao);
        UserDAO userDAO = userGetDataAccess(udao);
        UserService service = new UserService(authDAO, userDAO);

        RegisterUserRequest userInfo = new RegisterUserRequest("Isaac", "password", "hi@gmail.com");
        service.register(userInfo);
        assert(service.getNumUsers() == 1);
        UserData userOriginal = userInfo.toUserData();
        UserData userStored = service.getUser("Isaac");
        assert (userOriginal.username().equals(userStored.username()) &&
                userOriginal.email().equals(userStored.email()) &&
                userDAO.pwEquals(userOriginal.password(), userStored.password()));
    }

    //clear users, positive test
    @ParameterizedTest
    @MethodSource("provideClasses")
    void clearUsers(Class<? extends AuthDAO> adao, Class<? extends UserDAO> udao)
            throws UnavailableException, DataAccessException, InvalidLogInException, InvalidRequestException {
        AuthDAO authDAO = authGetDataAccess(adao);
        UserDAO userDAO = userGetDataAccess(udao);
        UserService service = new UserService(authDAO, userDAO);

        RegisterUserRequest userInfo = new RegisterUserRequest("Isaac", "password", "hi@gmail.com");
        RegisterUserRequest userInfo2 = new RegisterUserRequest("Isaiah", "password", "hi@gmail.com");
        service.register(userInfo);
        service.register(userInfo2);

        assert(service.getNumUsers() == 2);

        service.clearUsers();

        assert(service.getNumUsers() == 0);
    }

    //log in and log out user positive test
    @ParameterizedTest
    @MethodSource("provideClasses")
    void logInAndOut(Class<? extends AuthDAO> adao, Class<? extends UserDAO> udao)
            throws UnavailableException, DataAccessException, InvalidAuthTokenException, InvalidLogInException, InvalidRequestException {
        AuthDAO authDAO = authGetDataAccess(adao);
        UserDAO userDAO = userGetDataAccess(udao);
        UserService service = new UserService(authDAO, userDAO);

        AuthService authServ = new AuthService(authDAO);
        RegisterUserRequest userInfo = new RegisterUserRequest("Bethany", "password", "hi@gmail.com");
        RegisterUserResult auth = service.register(userInfo);
        assert(service.getNumUsers() == 1);
        assert(authServ.verifyAuth(auth.authToken()));
        service.logOut(new LogOutRequest(auth.authToken()));
        assert(service.getNumUsers() == 1);
        assertThrows(InvalidAuthTokenException.class, () -> authServ.verifyAuth(auth.authToken()));
    }
}
