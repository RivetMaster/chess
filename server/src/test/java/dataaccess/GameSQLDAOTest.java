package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import exceptions.DataAccessException;
import model.GameData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import exceptions.InvalidRequestException;

import static chess.ChessGame.TeamColor.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameSQLDAOTest {

    private GameDAO getDataAccess(Class<? extends GameDAO> databaseClass) throws DataAccessException {
        GameDAO db;
        if (databaseClass.equals(GameSQLDAO.class)) {
            db = new GameSQLDAO();
        } else {
            db = new GameMemoryDAO();
        }
        db.clearGames(); //before each clear
        return db;
    }

    //positive test for create game, list games, getGame
    @ParameterizedTest
    @ValueSource(classes = {GameSQLDAO.class, GameMemoryDAO.class})
    void createGame(Class<? extends GameDAO> dao) throws DataAccessException, InvalidRequestException {
        GameDAO gameDAO = getDataAccess(dao);

        GameData game = new GameData(0, null, null, "game1", new ChessGame());
        game = gameDAO.createGame(game);
        assert(gameDAO.listGames().size() == 1);
        GameData game2 = gameDAO.getGame(game.gameID());
        assert(game2.equals(game));
    }

    //negative test for create game
    @ParameterizedTest
    @ValueSource(classes = {GameSQLDAO.class, GameMemoryDAO.class})
    void createGameNull(Class<? extends GameDAO> dao) throws DataAccessException {
        GameDAO gameDAO = getDataAccess(dao);

        assertThrows(InvalidRequestException.class, () -> gameDAO.createGame(new GameData(0, null, null, null, null)));
    }

    //positive test for create game, list games, addPlayer (which calls update game), getGame
    @ParameterizedTest
    @ValueSource(classes = {GameSQLDAO.class, GameMemoryDAO.class})
    void addPlayerToGame(Class<? extends GameDAO> dao) throws DataAccessException, InvalidRequestException {
        GameDAO gameDAO = getDataAccess(dao);

        GameData game = new GameData(0, null, null, "game2", new ChessGame());
        game = gameDAO.createGame(game);
        int gameID = game.gameID();

        assert(gameDAO.listGames().size() == 1);
        GameData game2 = gameDAO.getGame(gameID);
        assert(game2.equals(game));

        gameDAO.addPlayer(gameID, WHITE, "Amy");
        game = new GameData(gameID, "Amy", null, "game2", new ChessGame());
        game2 = gameDAO.getGame(gameID);
        assert(game.equals(game2));
    }

    //positive test for creating multiple games, getGame
    @ParameterizedTest
    @ValueSource(classes = {GameSQLDAO.class, GameMemoryDAO.class})
    void createGames(Class<? extends GameDAO> dao) throws DataAccessException, InvalidRequestException {
        GameDAO gameDAO = getDataAccess(dao);

        GameData game = new GameData(0, "Sue", "merry", "kip", new ChessGame());
        game = gameDAO.createGame(game);
        GameData game1 = new GameData(0, "Henry", "Cami", "Whales", new ChessGame());
        game1 = gameDAO.createGame(game1);

        assert(gameDAO.listGames().size() == 2);
        GameData game2 = gameDAO.getGame(game.gameID());
        assert(game2.equals(game));
        GameData game3 = gameDAO.getGame(game1.gameID());
        assert(game3.equals(game1));
    }

    //positive test for clear games, listGames
    @ParameterizedTest
    @ValueSource(classes = {GameSQLDAO.class, GameMemoryDAO.class})
    void clearGames(Class<? extends GameDAO> dao) throws DataAccessException, InvalidRequestException {
        GameDAO gameDAO = getDataAccess(dao);

        GameData game = new GameData(0, null, null, "Sharks", new ChessGame());
        gameDAO.createGame(game);
        GameData game1 = new GameData(0, "Henry", "Cami", "Whales", new ChessGame());
        gameDAO.createGame(game1);

        assert(gameDAO.listGames().size() == 2);
        gameDAO.clearGames();
        assert(gameDAO.listGames().isEmpty());
    }

    //negative test for getGame
    @ParameterizedTest
    @ValueSource(classes = {GameSQLDAO.class, GameMemoryDAO.class})
    void getGameNotExist(Class<? extends GameDAO> dao) throws DataAccessException {
        GameDAO gameDAO = getDataAccess(dao);

        assertThrows(InvalidRequestException.class, () -> gameDAO.getGame(6));
        assertThrows(InvalidRequestException.class, () -> gameDAO.getGame(0));
    }

    //positive test for update game
    @ParameterizedTest
    @ValueSource(classes = {GameSQLDAO.class, GameMemoryDAO.class})
    void updateGame(Class<? extends GameDAO> dao) throws DataAccessException, InvalidRequestException, InvalidMoveException {
        GameDAO gameDAO = getDataAccess(dao);

        GameData originalGame = new GameData(0, null, null, "game2", new ChessGame());
        originalGame = gameDAO.createGame(originalGame);
        int gameID = originalGame.gameID();

        GameData storedGame = gameDAO.getGame(gameID);
        assert(storedGame.equals(originalGame));

        ChessMove move = new ChessMove(new ChessPosition(2, 2), new ChessPosition(3, 2), null);
        ChessGame newGame = new ChessGame();
        newGame.makeMove(move);
        GameData newGameData = new GameData(gameID, null, null, "game2", newGame);
        gameDAO.updateGame(gameID, newGameData);

        storedGame = gameDAO.getGame(gameID);
        assert(newGame.equals(storedGame.game()));

        assert(newGameData.equals(storedGame));
    }

    //negative test for update game
    @ParameterizedTest
    @ValueSource(classes = {GameSQLDAO.class, GameMemoryDAO.class})
    void updateGameNotExist(Class<? extends GameDAO> dao) throws DataAccessException {
        GameDAO gameDAO = getDataAccess(dao);

        assertThrows(InvalidRequestException.class, () -> gameDAO.updateGame(6, new GameData(190, null, null, "Tigers", new ChessGame())));
    }

    //negative test for addPlayer
    @ParameterizedTest
    @ValueSource(classes = {GameSQLDAO.class, GameMemoryDAO.class})
    void joinGameNotExist(Class<? extends GameDAO> dao) throws DataAccessException {
        GameDAO gameDAO = getDataAccess(dao);

        assertThrows(InvalidRequestException.class, () -> gameDAO.addPlayer(6, BLACK, "Kimmy"));
    }
}
