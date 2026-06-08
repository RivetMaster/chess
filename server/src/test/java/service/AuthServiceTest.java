package service;

import dataaccess.AuthDAO;
import dataaccess.AuthMemoryDAO;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.InvalidRequestException;
import service.exceptions.InvalidAuthTokenException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {
    static final AuthService SERVICE = new AuthService(new AuthMemoryDAO());

    @BeforeEach
    void clear() throws DataAccessException{
        SERVICE.clearAuths();
    }

    //test correctly adds authtoken and verifies correctly
    //positive test for addAuth and verify auth
    @Test
    void addAuth() throws InvalidAuthTokenException, DataAccessException, InvalidRequestException {
        AuthData auth1 = SERVICE.addAuth("Tim");
        assert(SERVICE.verifyAuth(auth1.authToken()));
    }

    //test correctly adds and verifies multiple authorizations, username doesn't cause issues
    //positive test for addAuth and verify Auth
    @Test
    void addAuths() throws InvalidAuthTokenException, DataAccessException, InvalidRequestException {
        AuthData auth1 = SERVICE.addAuth("Tim");
        AuthData auth2 = SERVICE.addAuth("Jen");
        AuthData auth3 = SERVICE.addAuth("Tim");
        AuthData auth4 = SERVICE.addAuth("Meg");

        assert(SERVICE.verifyAuth(auth1.authToken()));
        assert(SERVICE.verifyAuth(auth2.authToken()));
        assert(SERVICE.verifyAuth(auth3.authToken()));
        assert(SERVICE.verifyAuth(auth4.authToken()));
    }

    //verifies authToken without being added causes invalid token exception
    //negative test for verify auth
    @Test
    void noAuth() {
        String auth = AuthDAO.generateAuthToken();

        assertThrows(InvalidAuthTokenException.class, () -> SERVICE.verifyAuth(auth));
    }

    //negative test for addAuth
    @Test
    void addAuthNull() {
        assertThrows(InvalidRequestException.class, () -> SERVICE.addAuth(null));
    }

    //verifies clearAuths works, authorizations don't stay past
    //positive test for clearAuths
    @Test
    void clearAuths() throws DataAccessException, InvalidRequestException {
        AuthData auth1 = SERVICE.addAuth("Tim");
        AuthData auth2 = SERVICE.addAuth("Jen");
        AuthData auth3 = SERVICE.addAuth("Tim");
        AuthData auth4 = SERVICE.addAuth("Meg");

        SERVICE.clearAuths();

        assertThrows(InvalidAuthTokenException.class, () -> SERVICE.verifyAuth(auth1.authToken()));
        assertThrows(InvalidAuthTokenException.class, () -> SERVICE.verifyAuth(auth2.authToken()));
        assertThrows(InvalidAuthTokenException.class, () -> SERVICE.verifyAuth(auth3.authToken()));
        assertThrows(InvalidAuthTokenException.class, () -> SERVICE.verifyAuth(auth4.authToken()));
    }

    //test delete authData throws error, leaves other authorizations alone
    //positive test for delAuth
    @Test
    void deleteAuth() throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        AuthData auth1 = SERVICE.addAuth("Ham");
        AuthData auth2 = SERVICE.addAuth("Cheese");

        SERVICE.delAuth(auth1.authToken());

        assertThrows(InvalidAuthTokenException.class, () -> SERVICE.verifyAuth(auth1.authToken()));
        assert(SERVICE.verifyAuth(auth2.authToken()));
    }

    //test deleting non-existent authData throws error
    //negative test for delAuth
    @Test
    void deleteNonAuth() throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        AuthData auth1 = SERVICE.addAuth("Jim");
        AuthData auth2 = new AuthData(AuthDAO.generateAuthToken(), "Mary");

        assertThrows(InvalidAuthTokenException.class,() -> SERVICE.delAuth(auth2.authToken()));
        assert(SERVICE.verifyAuth(auth1.authToken()));
    }

    @Test
    void nullAuth(){
        assertThrows(InvalidAuthTokenException.class, () -> SERVICE.verifyAuth(null));
    }


}
