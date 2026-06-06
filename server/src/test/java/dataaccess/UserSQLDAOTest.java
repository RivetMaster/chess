package dataaccess;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.exceptions.*;
import service.resultsandrequests.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserSQLDAOTest{

    private UserDAO getDataAccess(Class<? extends UserDAO> databaseClass) throws DataAccessException {
        UserDAO db;
        if (databaseClass.equals(UserSQLDAO.class)) {
            db = new UserSQLDAO();
        } else {
            db = new UserMemoryDAO();
        }
        db.clearUsers(); //before each clear
        return db;
    }

    //positive test for create user
    @ParameterizedTest
    @ValueSource(classes = {UserSQLDAO.class, UserMemoryDAO.class})
    void addUser(Class<? extends UserDAO> dao) throws DataAccessException {
        UserDAO userDAO = getDataAccess(dao);

        userDAO.createUser(new UserData("Mary", "password", "hi@gmail"));
        assert(userDAO.getNumUsers() == 1);
        userDAO.createUser(new UserData("John", "hijaking", "urmom@verizon"));
        assert(userDAO.getNumUsers() == 2);
    }

    //positive test for clear users
    @ParameterizedTest
    @ValueSource(classes = {UserSQLDAO.class, UserMemoryDAO.class})
    void clearUsers(Class<? extends UserDAO> dao) throws DataAccessException {
        UserDAO userDAO = getDataAccess(dao);

        userDAO.createUser(new UserData("Mary", "password", "hi@gmail"));
        userDAO.createUser(new UserData("John", "hijaking", "urmom@verizon"));
        userDAO.createUser(new UserData("Michelle", "traveling", "hack@pie"));
        assert(userDAO.getNumUsers() == 3);
        userDAO.clearUsers();
        assert(userDAO.getNumUsers() == 0);
    }

    //negative test for add User
    @ParameterizedTest
    @ValueSource(classes = {UserSQLDAO.class, UserMemoryDAO.class})
    void addUserNull(Class<? extends UserDAO> dao) throws DataAccessException {
        UserDAO userDAO = getDataAccess(dao);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(new UserData("Mary", "password", null)));
    }

    //negative test for getUser
    @ParameterizedTest
    @ValueSource(classes = {UserSQLDAO.class, UserMemoryDAO.class})
    void getUserDoesntExist(Class<? extends UserDAO> dao) throws DataAccessException {
        UserDAO userDAO = getDataAccess(dao);

        assertThrows(DataAccessException.class, () -> userDAO.getUser("Pam"));
    }

}
