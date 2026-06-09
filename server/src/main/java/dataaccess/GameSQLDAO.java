package dataaccess;

import java.sql.Connection;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import server.InvalidRequestException;

import java.sql.*;
import java.util.ArrayList;

import static java.sql.Types.NULL;

public class GameSQLDAO implements GameDAO{
    public GameSQLDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException, InvalidRequestException {
        if(!game.verifyFields()){
            throw new InvalidRequestException("Expected game name and game.");
        }
        var statement = "INSERT INTO Games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        int gameID = executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), new Gson().toJson(game.game()));
        return new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException, InvalidRequestException {
        if(gameID<=0 || gameID > getNumGames()){
            throw new InvalidRequestException("Invalid Game ID");
        }
        var statement = "SELECT * FROM Games WHERE gameID=?";
        String whiteUser, blackUser, gameName;
        ChessGame game;
        try(Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    whiteUser = rs.getString(2);
                    blackUser = rs.getString(3);
                    gameName = rs.getString(4);
                    game = new Gson().fromJson(rs.getString(5), ChessGame.class);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to get game: %s", e.getMessage()));
        }
        return new GameData(gameID, whiteUser, blackUser, gameName, game);
    }

    private int getNumGames() throws DataAccessException {
        int num;
        var statement = "SELECT COUNT(0) FROM Games";
        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    num = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to count games: %s", e.getMessage()));
        }
        return num;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        var statement = "SELECT * FROM Games";
        String whiteUser, blackUser, gameName;
        ChessGame game;
        int gameID;
        ArrayList<GameData> games = new ArrayList<>();
        try(Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        gameID = rs.getInt(1);
                        whiteUser = rs.getString(2);
                        blackUser = rs.getString(3);
                        gameName = rs.getString(4);
                        game = new Gson().fromJson(rs.getString(5), ChessGame.class);
                        games.add(new GameData(gameID, whiteUser, blackUser, gameName, game));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to get games: %s", e.getMessage()));
        }
        return games;
    }

    @Override
    public void updateGame(int gameID, GameData game) throws DataAccessException, InvalidRequestException {
        if(gameID<=0 || gameID > getNumGames()){
            throw new InvalidRequestException("Invalid Game ID");
        }
        var statement = "UPDATE Games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?";
        executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), new Gson().toJson(game.game()), gameID);
    }

    @Override
    public void addPlayer(int gameID, ChessGame.TeamColor color, String username) throws DataAccessException, InvalidRequestException {
        updateGame(gameID, getGame(gameID).setUser(username, color));
    }

    @Override
    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE Games";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()){
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {"""
            CREATE TABLE IF NOT EXISTS `Games` (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(256) NULL,
              `blackUsername` VARCHAR(256) NULL,
              `gameName` VARCHAR(256) NULL,
              `game` JSON NULL,
              PRIMARY KEY (`gameID`),
              UNIQUE INDEX `gameID_UNIQUE` (`gameID` ASC) VISIBLE);
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
