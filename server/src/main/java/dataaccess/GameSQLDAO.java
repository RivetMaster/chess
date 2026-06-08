package dataaccess;

import java.sql.Connection;

import chess.ChessGame;
import model.*;
import server.InvalidRequestException;
import service.exceptions.InvalidAuthTokenException;

import java.sql.*;
import java.util.ArrayList;

public class GameSQLDAO implements GameDAO{
    public GameSQLDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException, InvalidRequestException {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData game) throws DataAccessException, InvalidRequestException {

    }

    @Override
    public void addPlayer(int gameID, ChessGame.TeamColor color, String username) throws DataAccessException, InvalidRequestException {

    }

    @Override
    public void clearGames() throws DataAccessException {

    }

    @Override
    public int newGameID() {
        return 0;
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
