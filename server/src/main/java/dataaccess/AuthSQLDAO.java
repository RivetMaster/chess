package dataaccess;

import java.sql.Connection;
import model.*;
import server.InvalidRequestException;
import service.exceptions.InvalidAuthTokenException;

import java.sql.*;

public class AuthSQLDAO implements AuthDAO{
    public AuthSQLDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException, InvalidRequestException {
        if(username == null || username.isBlank()){
            throw new InvalidRequestException("Expecting Username");
        }
        var statement = "INSERT INTO Auths (username, authToken) VALUES (?, ?)";
        String authToken = AuthDAO.generateAuthToken();
        executeUpdate(statement, username, authToken);
        return new AuthData(authToken, username);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, InvalidAuthTokenException {
        var statement = "SELECT * FROM Auths WHERE authToken=?";
        String user, auth;
        try(Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        user = rs.getString(1);
                        auth = rs.getString(2);
                    } else{
                        throw new InvalidAuthTokenException("Invalid Authentication");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to get authentication: %s", e.getMessage()));
        }
        return new AuthData(auth, user);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, InvalidAuthTokenException {
        var statement = "DELETE FROM Auths WHERE authToken=?";
        int num = numAuths();
        executeUpdate(statement, authToken);
        if(num == numAuths()){
            throw new InvalidAuthTokenException("Authentication Does Not Exist");
        }
    }

    @Override
    public int numAuths() throws DataAccessException{
        int num;
        var statement = "SELECT COUNT(0) from Auths";
        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    num = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to count auths: %s", e.getMessage()));
        }
        return num;
    }

    @Override
    public void clearAuth() throws DataAccessException {
        var statement = "TRUNCATE Auths";
        executeUpdate(statement);
    }


    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {"""
            CREATE TABLE IF NOT EXISTS `Auths` (
            `username` varchar(256) NOT NULL,
            `authToken` varchar(256) NOT NULL,
            PRIMARY KEY (`username`,`authToken`),
            UNIQUE KEY `authToken_UNIQUE` (`authToken`)
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """};

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
