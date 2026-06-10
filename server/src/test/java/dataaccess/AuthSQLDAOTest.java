package dataaccess;

import exceptions.DataAccessException;
import model.AuthData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import exceptions.InvalidRequestException;
import exceptions.InvalidAuthTokenException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthSQLDAOTest {

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

    //positive test for create auth and for getNumAuths
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void createAuth(Class<? extends AuthDAO> dao) throws DataAccessException, InvalidRequestException {
        AuthDAO authDAO = getDataAccess(dao);

        authDAO.createAuth("janet");
        assert(authDAO.numAuths() == 1);
        authDAO.createAuth("Harriot");
        assert(authDAO.numAuths() == 2);
    }

    //negative test for createAuth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void createAuthNull(Class<? extends AuthDAO> dao) throws DataAccessException {
        AuthDAO authDAO = getDataAccess(dao);

        assertThrows(InvalidRequestException.class, () -> authDAO.createAuth(null));
        assert(authDAO.numAuths() == 0);
    }

    //negative test for getAuth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void getAuthNotExist(Class<? extends AuthDAO> dao) throws DataAccessException {
        AuthDAO authDAO = getDataAccess(dao);

        assertThrows(InvalidAuthTokenException.class, () -> authDAO.getAuth("Bob"));
    }

    //positive test for getAuth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void getAuth(Class<? extends AuthDAO> dao) throws DataAccessException, InvalidRequestException, InvalidAuthTokenException {
        AuthDAO authDAO = getDataAccess(dao);

        AuthData auth1 = authDAO.createAuth("ollie");
        assert(authDAO.numAuths() == 1);
        AuthData auth2 = authDAO.getAuth(auth1.authToken());
        assert(auth1.equals(auth2));
    }

    //positive test for clearAuth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void clearAuth(Class<? extends AuthDAO> dao) throws DataAccessException, InvalidRequestException {
        AuthDAO authDAO = getDataAccess(dao);

        authDAO.createAuth("william");
        authDAO.createAuth("juliet");
        authDAO.createAuth("romeo");
        authDAO.createAuth("bear");
        assert(authDAO.numAuths() == 4);
        authDAO.clearAuth();
        assert(authDAO.numAuths() == 0);
    }

    //positive test for delAuth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void delAuth(Class<? extends AuthDAO> dao) throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        AuthDAO authDAO = getDataAccess(dao);

        AuthData auth1 = authDAO.createAuth("sheryl");
        assert(authDAO.numAuths() == 1);
        assert(authDAO.getAuth(auth1.authToken()).equals(auth1));
        authDAO.deleteAuth(auth1.authToken());
        assertThrows(InvalidAuthTokenException.class, () -> authDAO.getAuth(auth1.authToken()));
        assert(authDAO.numAuths() == 0);
    }

    //negative test for delAuth
    @ParameterizedTest
    @ValueSource(classes = {AuthSQLDAO.class, AuthMemoryDAO.class})
    void delAuthNull(Class<? extends AuthDAO> dao) throws DataAccessException {
        AuthDAO authDAO = getDataAccess(dao);

        assertThrows(InvalidAuthTokenException.class, () -> authDAO.deleteAuth("billy"));
    }

}
