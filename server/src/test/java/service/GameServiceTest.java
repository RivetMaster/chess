package service;


import dataaccess.AuthDAO;
import dataaccess.AuthMemoryDAO;
import dataaccess.DataAccessException;
import dataaccess.GameMemoryDAO;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.InvalidRequestException;
import service.exceptions.InvalidAuthTokenException;
import service.exceptions.UnavailableException;
import service.resultsandrequests.CreateGameRequest;
import service.resultsandrequests.CreateGameResult;
import service.resultsandrequests.JoinGameRequest;
import service.resultsandrequests.ListGamesRequest;

import java.util.ArrayList;

import static chess.ChessGame.TeamColor.*;
import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {
    static final AuthDAO AUTH_DAO = new AuthMemoryDAO();
    static final GameService SERVICE = new GameService(new GameMemoryDAO(), AUTH_DAO);

    @BeforeEach
    void clearGames() throws DataAccessException{
        SERVICE.clearGames();
        AUTH_DAO.clearAuth();
    }

    //test creating one game
    //positive test for createGame, and listGames
    @Test
    void createGame() throws DataAccessException, InvalidAuthTokenException {
        AuthData auth = AUTH_DAO.createAuth("June");
        SERVICE.createGame(new CreateGameRequest("Game 1", auth.authToken()));
        ArrayList<GameData> games = SERVICE.listGames(new ListGamesRequest(auth.authToken())).games();
        assert(games.size() == 1);
        assert(games.getFirst().gameName().equals("Game 1"));
    }

    //test clears games positive
    @Test
    void clear() throws DataAccessException, InvalidAuthTokenException {
        AuthData auth = AUTH_DAO.createAuth("July");
        SERVICE.createGame(new CreateGameRequest("game 2", auth.authToken()));
        SERVICE.clearGames();
        assert(SERVICE.listGames(new ListGamesRequest(auth.authToken())).games().isEmpty());
    }

    //test throws error when creating game without authorization
    //create game negative
    @Test
    void createGameUnauthorized() throws DataAccessException, InvalidAuthTokenException{
        assertThrows(InvalidAuthTokenException.class, () -> SERVICE.createGame(new CreateGameRequest("game 3", "token")));
        AuthData auth = AUTH_DAO.createAuth("Mary");
        assert(SERVICE.listGames(new ListGamesRequest(auth.authToken())).games().isEmpty());
    }

    //tests list games negative
    @Test
    void listGamesUnauthorized() {
        assertThrows(InvalidAuthTokenException.class, () ->
                SERVICE.listGames(new ListGamesRequest("3")));
    }

    //positive test for joining Game
    @Test
    void joinGame() throws DataAccessException, InvalidAuthTokenException, UnavailableException, InvalidRequestException {
        AuthData auth = AUTH_DAO.createAuth("Kim");
        CreateGameResult result = SERVICE.createGame(new CreateGameRequest("Game 4", auth.authToken()));
        SERVICE.joinGame(new JoinGameRequest(result.gameID(), WHITE, auth));
        GameData game = SERVICE.listGames(new ListGamesRequest(auth.authToken())).games().getFirst();
        assert(game.whiteUsername().equals("Kim"));
        assert(game.blackUsername() == null);
    }

    //negative test for joining game
    @Test
    void joinGameFull() throws DataAccessException, InvalidAuthTokenException, UnavailableException, InvalidRequestException {
        AuthData auth1 = AUTH_DAO.createAuth("Pam");
        AuthData auth2 = AUTH_DAO.createAuth("Ruby");
        CreateGameResult game = SERVICE.createGame(new CreateGameRequest("Game 5", auth1.authToken()));
        SERVICE.joinGame(new JoinGameRequest(game.gameID(), WHITE, auth1));
        assertThrows(UnavailableException.class, () -> SERVICE.joinGame(new JoinGameRequest(game.gameID(), WHITE, auth2)));
    }

    //positive test for leaveGame
    @Test
    void leaveGame() throws DataAccessException, InvalidAuthTokenException, UnavailableException, InvalidRequestException {
        AuthData auth1 = AUTH_DAO.createAuth("Harry");
        CreateGameResult result = SERVICE.createGame(new CreateGameRequest("Game 6", auth1.authToken()));
        SERVICE.joinGame(new JoinGameRequest(result.gameID(), WHITE, auth1));
        GameData game = SERVICE.listGames(new ListGamesRequest(auth1.authToken())).games().getFirst();
        assert(game.whiteUsername().equals("Harry"));
        SERVICE.leaveGame(new JoinGameRequest(game.gameID(), WHITE, auth1));
        game = SERVICE.listGames(new ListGamesRequest(auth1.authToken())).games().getFirst();
        assert(game.whiteUsername() == null);
    }

    //negative test for leaveGame
    @Test
    void leaveGameNotIn() throws DataAccessException, InvalidAuthTokenException {
        AuthData auth = AUTH_DAO.createAuth("Gary");
        CreateGameResult game = SERVICE.createGame(new CreateGameRequest("Game 7", auth.authToken()));
        assertThrows(InvalidRequestException.class, () -> SERVICE.leaveGame(new JoinGameRequest(game.gameID(), WHITE, auth)));
    }

}
