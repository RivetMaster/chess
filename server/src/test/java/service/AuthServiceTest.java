package service;

import dataaccess.*;

import model.AuthData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import server.InvalidRequestException;
import service.exceptions.InvalidAuthTokenException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private AuthDAO getDataAccess(Class<? extends AuthDAO> databaseClass) throws DataAccessException {
        AuthDAO db;
        if (databaseClass.equals(AuthSQLDAO.class)) {
            db = new AuthSQLDAO();
        } else {
            db = new AuthMemoryDAO();
        }
        db.clearAuth(); //before each clear
        return db;
    }

    //test correctly adds authtoken and verifies correctly
    //positive test for addAuth and verify auth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void addAuth(Class<? extends AuthDAO> dao) throws InvalidAuthTokenException, DataAccessException, InvalidRequestException {
        AuthDAO authDAO = getDataAccess(dao);
        AuthService service = new AuthService(authDAO);

        AuthData auth1 = service.addAuth("Tim");
        assert(service.verifyAuth(auth1.authToken()));
    }

    //test correctly adds and verifies multiple authorizations
    //positive test for addAuth and verify Auth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void addAuths(Class<? extends AuthDAO> dao) throws InvalidAuthTokenException, DataAccessException, InvalidRequestException {
        AuthDAO authDAO = getDataAccess(dao);
        AuthService service = new AuthService(authDAO);

        AuthData auth1 = service.addAuth("Tim");
        AuthData auth2 = service.addAuth("Jen");
        AuthData auth4 = service.addAuth("Meg");

        assert(service.verifyAuth(auth1.authToken()));
        assert(service.verifyAuth(auth2.authToken()));
        assert(service.verifyAuth(auth4.authToken()));
    }

    //verifies authToken without being added causes invalid token exception
    //negative test for verify auth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void noAuth(Class<? extends AuthDAO> dao) throws DataAccessException {
        AuthDAO authDAO = getDataAccess(dao);
        AuthService service = new AuthService(authDAO);

        String auth = AuthDAO.generateAuthToken();

        assertThrows(InvalidAuthTokenException.class, () -> service.verifyAuth(auth));
    }

    //negative test for addAuth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void addAuthNull(Class<? extends AuthDAO> dao) throws DataAccessException {
        AuthDAO authDAO = getDataAccess(dao);
        AuthService service = new AuthService(authDAO);

        assertThrows(InvalidRequestException.class, () -> service.addAuth(null));
    }

    //verifies clearAuths works, authorizations don't stay past
    //positive test for clearAuths
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void clearAuths(Class<? extends AuthDAO> dao) throws DataAccessException, InvalidRequestException {
        AuthDAO authDAO = getDataAccess(dao);
        AuthService service = new AuthService(authDAO);

        AuthData auth1 = service.addAuth("Tim");
        AuthData auth2 = service.addAuth("Jen");
        AuthData auth4 = service.addAuth("Meg");

        service.clearAuths();

        assertThrows(InvalidAuthTokenException.class, () -> service.verifyAuth(auth1.authToken()));
        assertThrows(InvalidAuthTokenException.class, () -> service.verifyAuth(auth2.authToken()));
        assertThrows(InvalidAuthTokenException.class, () -> service.verifyAuth(auth4.authToken()));
    }

    //test delete authData throws error, leaves other authorizations alone
    //positive test for delAuth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void deleteAuth(Class<? extends AuthDAO> dao) throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        AuthDAO authDAO = getDataAccess(dao);
        AuthService service = new AuthService(authDAO);

        AuthData auth1 = service.addAuth("Ham");
        AuthData auth2 = service.addAuth("Cheese");

        service.delAuth(auth1.authToken());

        assertThrows(InvalidAuthTokenException.class, () -> service.verifyAuth(auth1.authToken()));
        assert(service.verifyAuth(auth2.authToken()));
    }

    //test deleting non-existent authData throws error
    //negative test for delAuth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void deleteNonAuth(Class<? extends AuthDAO> dao) throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        AuthDAO authDAO = getDataAccess(dao);
        AuthService service = new AuthService(authDAO);

        AuthData auth1 = service.addAuth("Jim");
        AuthData auth2 = new AuthData(AuthDAO.generateAuthToken(), "Mary");

        assertThrows(InvalidAuthTokenException.class,() -> service.delAuth(auth2.authToken()));
        assert(service.verifyAuth(auth1.authToken()));
    }

    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void nullAuth(Class<? extends AuthDAO> dao) throws DataAccessException {
        AuthDAO authDAO = getDataAccess(dao);
        AuthService service = new AuthService(authDAO);

        assertThrows(InvalidAuthTokenException.class, () -> service.verifyAuth(null));
    }


}
