package dataaccess;

import java.sql.Connection;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import server.InvalidRequestException;

import java.sql.*;

import static dataaccess.QuerySQLDatabase.*;

public class UserSQLDAO implements UserDAO{

    public UserSQLDAO() throws DataAccessException {
        String[] createStatements = {"""
            CREATE TABLE IF NOT EXISTS `Users` (
              `username` VARCHAR(256) NOT NULL,
              `password` VARCHAR(256) NOT NULL,
              `email` VARCHAR(256) NOT NULL,
              PRIMARY KEY (`username`),
              UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE);
            """};
        configureDatabase(createStatements);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException, InvalidRequestException {
        if(!user.verifyFields()){
            throw new InvalidRequestException("Expecting username, password, email");
        }
        var statement = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException, InvalidRequestException {
        var statement = "SELECT * FROM Users WHERE username=?";
        String user, pass, email;
        try(Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        user = rs.getString(1);
                        pass = rs.getString(2);
                        email = rs.getString(3);
                    } else{
                        throw new InvalidRequestException("User Does Not Exist");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to get user: %s", e.getMessage()));
        }
        return new UserData(user, pass, email);
    }

    @Override
    public void clearUsers() throws DataAccessException {
        var statement = "TRUNCATE Users";
        executeUpdate(statement);
    }

    @Override
    public int getNumUsers() throws DataAccessException {
        var statement = "SELECT COUNT(0) FROM Users";
        try{
            return getCount(statement);
        } catch(Exception e) {
            throw new DataAccessException(String.format("Unable to count users: %s", e.getMessage()));
        }
    }

    @Override
    public boolean pwEquals(String inputPW, String storedPW){
        return BCrypt.checkpw(inputPW, storedPW);
    }


}
