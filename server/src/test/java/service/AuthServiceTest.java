package service;

import dataaccess.AuthDAO;
import dataaccess.AuthMemoryDAO;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {
    static final AuthService service = new AuthService(new AuthMemoryDAO());

    @BeforeEach
    void clear() throws DataAccessException{
        service.clearAuths();
    }

    //test correctly adds authtoken and verifies correctly
    @Test
    void addAuth() throws InvalidAuthTokenException, DataAccessException{
        AuthData auth1 = service.addAuth("Tim");
        assert(service.verifyAuth(auth1.authToken()));
    }

    //test correctly adds and verifies multiple authorizations, username doesn't cause issues
    @Test
    void addAuths() throws InvalidAuthTokenException, DataAccessException{
        AuthData auth1 = service.addAuth("Tim");
        AuthData auth2 = service.addAuth("Jen");
        AuthData auth3 = service.addAuth("Tim");
        AuthData auth4 = service.addAuth("Meg");

        assert(service.verifyAuth(auth1.authToken()));
        assert(service.verifyAuth(auth2.authToken()));
        assert(service.verifyAuth(auth3.authToken()));
        assert(service.verifyAuth(auth4.authToken()));
    }

    //verifies authToken without being added causes invalid token exception
    @Test
    void noAuth() {
        String auth = AuthDAO.generateAuthToken();

        assertThrows(InvalidAuthTokenException.class, () -> service.verifyAuth(auth));
    }

    //verifies clearAuths works, authorizations don't stay past
    @Test
    void clearAuths() throws  DataAccessException{
        AuthData auth1 = service.addAuth("Tim");
        AuthData auth2 = service.addAuth("Jen");
        AuthData auth3 = service.addAuth("Tim");
        AuthData auth4 = service.addAuth("Meg");

        service.clearAuths();

        assertThrows(InvalidAuthTokenException.class, () -> service.verifyAuth(auth1.authToken()));
        assertThrows(InvalidAuthTokenException.class, () -> service.verifyAuth(auth2.authToken()));
        assertThrows(InvalidAuthTokenException.class, () -> service.verifyAuth(auth3.authToken()));
        assertThrows(InvalidAuthTokenException.class, () -> service.verifyAuth(auth4.authToken()));
    }
}
