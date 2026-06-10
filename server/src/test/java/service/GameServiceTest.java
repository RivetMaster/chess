package service;


import dataaccess.*;

import exceptions.DataAccessException;
import model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import resultsandrequests.CreateGameRequest;
import resultsandrequests.CreateGameResult;
import resultsandrequests.JoinGameRequest;
import resultsandrequests.ListGamesRequest;
import exceptions.InvalidRequestException;
import exceptions.InvalidAuthTokenException;
import exceptions.UnavailableException;

import java.util.ArrayList;
import java.util.stream.Stream;

import static chess.ChessGame.TeamColor.*;
import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {

    private GameDAO gameGetDataAccess(Class<? extends GameDAO> databaseClass) throws DataAccessException {
        GameDAO db;
        if (databaseClass.equals(GameSQLDAO.class)) {
            db = new GameSQLDAO();
        } else {
            db = new GameMemoryDAO();
        }
        db.clearGames(); //before each clear
        return db;
    }

    private AuthDAO authGetDataAccess(Class<? extends AuthDAO> databaseClass) throws DataAccessException {
        AuthDAO db;
        if (databaseClass.equals(AuthSQLDAO.class)) {
            db = new AuthSQLDAO();
        } else {
            db = new AuthMemoryDAO();
        }
        db.clearAuth(); //before each clear
        return db;
    }

    private static Stream<Arguments> provideGameClasses() {
        return Stream.of(
                Arguments.of(AuthMemoryDAO.class, GameMemoryDAO.class),
                Arguments.of(AuthSQLDAO.class, GameMemoryDAO.class),
                Arguments.of(AuthMemoryDAO.class, GameSQLDAO.class),
                Arguments.of(AuthSQLDAO.class, GameSQLDAO.class)
        );
    }

    //test creating one game
    //positive test for createGame, and listGames
    @ParameterizedTest
    @MethodSource("provideGameClasses")
    void createGameClass(Class<? extends AuthDAO> adao, Class<? extends GameDAO> gdao)
            throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        AuthDAO authDAO = authGetDataAccess(adao);
        GameDAO gameDAO = gameGetDataAccess(gdao);
        GameService service = new GameService(gameDAO, authDAO);

        AuthData auth = authDAO.createAuth("June");
        service.createGame(new CreateGameRequest("Game 1", auth.authToken()));
        ArrayList<GameData> games = service.listGames(new ListGamesRequest(auth.authToken())).games();
        assert(games.size() == 1);
        assert(games.getFirst().gameName().equals("Game 1"));
    }

    //test clears games positive
    @ParameterizedTest
    @MethodSource("provideGameClasses")
    void clear(Class<? extends AuthDAO> adao, Class<? extends GameDAO> gdao)
            throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        AuthDAO authDAO = authGetDataAccess(adao);
        GameDAO gameDAO = gameGetDataAccess(gdao);
        GameService service = new GameService(gameDAO, authDAO);

        AuthData auth = authDAO.createAuth("July");
        service.createGame(new CreateGameRequest("game 2", auth.authToken()));
        service.clearGames();
        assert(service.listGames(new ListGamesRequest(auth.authToken())).games().isEmpty());
    }

    //test throws error when creating game without authorization
    //create game negative
    @ParameterizedTest
    @MethodSource("provideGameClasses")
    void createGameUnauthorized(Class<? extends AuthDAO> adao, Class<? extends GameDAO> gdao)
            throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        AuthDAO authDAO = authGetDataAccess(adao);
        GameDAO gameDAO = gameGetDataAccess(gdao);
        GameService service = new GameService(gameDAO, authDAO);

        assertThrows(InvalidAuthTokenException.class, () ->
                service.createGame(new CreateGameRequest("game 3", "token")));
        AuthData auth = authDAO.createAuth("Mary");
        assert(service.listGames(new ListGamesRequest(auth.authToken())).games().isEmpty());
    }

    //tests list games negative
    @ParameterizedTest
    @MethodSource("provideGameClasses")
    void listGamesUnauthorized(Class<? extends AuthDAO> adao, Class<? extends GameDAO> gdao)
            throws DataAccessException {
        AuthDAO authDAO = authGetDataAccess(adao);
        GameDAO gameDAO = gameGetDataAccess(gdao);
        GameService service = new GameService(gameDAO, authDAO);

        assertThrows(InvalidAuthTokenException.class, () ->
                service.listGames(new ListGamesRequest("3")));
    }

    //positive test for joining Game
    @ParameterizedTest
    @MethodSource("provideGameClasses")
    void joinGame(Class<? extends AuthDAO> adao, Class<? extends GameDAO> gdao)
            throws DataAccessException, InvalidAuthTokenException, UnavailableException, InvalidRequestException {
        AuthDAO authDAO = authGetDataAccess(adao);
        GameDAO gameDAO = gameGetDataAccess(gdao);
        GameService service = new GameService(gameDAO, authDAO);

        AuthData auth = authDAO.createAuth("Kim");
        CreateGameResult result = service.createGame(new CreateGameRequest("Game 4", auth.authToken()));
        service.joinGame(new JoinGameRequest(result.gameID(), WHITE, auth));
        GameData game = service.listGames(new ListGamesRequest(auth.authToken())).games().getFirst();
        assert(game.whiteUsername().equals("Kim"));
        assert(game.blackUsername() == null);
    }

    //negative test for joining game
    @ParameterizedTest
    @MethodSource("provideGameClasses")
    void joinGameFull(Class<? extends AuthDAO> adao, Class<? extends GameDAO> gdao)
            throws DataAccessException, InvalidAuthTokenException, UnavailableException, InvalidRequestException {
        AuthDAO authDAO = authGetDataAccess(adao);
        GameDAO gameDAO = gameGetDataAccess(gdao);
        GameService service = new GameService(gameDAO, authDAO);

        AuthData auth1 = authDAO.createAuth("Pam");
        AuthData auth2 = authDAO.createAuth("Ruby");
        CreateGameResult game = service.createGame(new CreateGameRequest("Game 5", auth1.authToken()));
        service.joinGame(new JoinGameRequest(game.gameID(), WHITE, auth1));
        assertThrows(UnavailableException.class, () ->
                service.joinGame(new JoinGameRequest(game.gameID(), WHITE, auth2)));
    }

    //positive test for leaveGame
    @ParameterizedTest
    @MethodSource("provideGameClasses")
    void leaveGame(Class<? extends AuthDAO> adao, Class<? extends GameDAO> gdao)
            throws DataAccessException, InvalidAuthTokenException, UnavailableException, InvalidRequestException {
        AuthDAO authDAO = authGetDataAccess(adao);
        GameDAO gameDAO = gameGetDataAccess(gdao);
        GameService service = new GameService(gameDAO, authDAO);

        AuthData auth1 = authDAO.createAuth("Harry");
        CreateGameResult result = service.createGame(new CreateGameRequest("Game 6", auth1.authToken()));
        service.joinGame(new JoinGameRequest(result.gameID(), WHITE, auth1));
        GameData game = service.listGames(new ListGamesRequest(auth1.authToken())).games().getFirst();
        assert(game.whiteUsername().equals("Harry"));
        service.leaveGame(new JoinGameRequest(game.gameID(), WHITE, auth1));
        game = service.listGames(new ListGamesRequest(auth1.authToken())).games().getFirst();
        assert(game.whiteUsername() == null);
    }

    //negative test for leaveGame
    @ParameterizedTest
    @MethodSource("provideGameClasses")
    void leaveGameNotIn(Class<? extends AuthDAO> adao, Class<? extends GameDAO> gdao)
            throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        AuthDAO authDAO = authGetDataAccess(adao);
        GameDAO gameDAO = gameGetDataAccess(gdao);
        GameService service = new GameService(gameDAO, authDAO);

        AuthData auth = authDAO.createAuth("Gary");
        CreateGameResult game = service.createGame(new CreateGameRequest("Game 7", auth.authToken()));
        assertThrows(InvalidRequestException.class, () ->
                service.leaveGame(new JoinGameRequest(game.gameID(), WHITE, auth)));
    }

}
