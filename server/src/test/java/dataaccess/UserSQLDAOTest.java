package dataaccess;

import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mindrot.jbcrypt.BCrypt;
import server.InvalidRequestException;
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

    //positive test for create user and for getNumUsers
    @ParameterizedTest
    @ValueSource(classes = {UserSQLDAO.class, UserMemoryDAO.class})
    void addUser(Class<? extends UserDAO> dao) throws DataAccessException, InvalidRequestException {
        UserDAO userDAO = getDataAccess(dao);

        userDAO.createUser(new UserData("Mary", "password", "hi@gmail"));
        assert(userDAO.getNumUsers() == 1);
        userDAO.createUser(new UserData("John", "hijaking", "urmom@verizon"));
        assert(userDAO.getNumUsers() == 2);
    }

    //positive test for clear users
    @ParameterizedTest
    @ValueSource(classes = {UserSQLDAO.class, UserMemoryDAO.class})
    void clearUsers(Class<? extends UserDAO> dao) throws DataAccessException, InvalidRequestException {
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

        assertThrows(InvalidRequestException.class, () -> userDAO.createUser(new UserData("Mary", "password", null)));
    }

    //negative test for getUser
    @ParameterizedTest
    @ValueSource(classes = {UserSQLDAO.class, UserMemoryDAO.class})
    void getUserDoesntExist(Class<? extends UserDAO> dao) throws DataAccessException {
        UserDAO userDAO = getDataAccess(dao);

        assertThrows(InvalidRequestException.class, () -> userDAO.getUser("Pam"));
    }

    //positive test for getUser
    @ParameterizedTest
    @ValueSource(classes = {UserSQLDAO.class, UserMemoryDAO.class})
    void getUsers(Class<? extends UserDAO> dao) throws DataAccessException, InvalidRequestException {
        UserDAO userDAO = getDataAccess(dao);

        UserData user1 = new UserData("Sue", "password2", "hick@gmail");
        UserData user2 = new UserData("Lee", "moneymaker", "purple@verizon");
        userDAO.createUser(user1);
        userDAO.createUser(user2);
        assert(userDAO.getNumUsers() == 2);
        UserData user3 = userDAO.getUser("Sue");
        UserData user4 = userDAO.getUser("Lee");
        if(dao.equals(UserSQLDAO.class)) {
            assert(user1.username().equals(user3.username()) && user1.email().equals(user3.email()) &&
                    BCrypt.checkpw(user1.password(), user3.password()));
            assert(user2.username().equals(user4.username()) && user2.email().equals(user4.email()) &&
                    BCrypt.checkpw(user2.password(), user4.password()));
        } else{
            assert(user1.equals(user3));
            assert(user2.equals(user4));
        }

    }

}
