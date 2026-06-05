package service;


import dataaccess.AuthDAO;
import dataaccess.AuthMemoryDAO;
import dataaccess.DataAccessException;
import dataaccess.GameMemoryDAO;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exceptions.InvalidAuthTokenException;
import service.exceptions.UnavailableException;
import service.resultsandrequests.createGameRequest;
import service.resultsandrequests.createGameResult;
import service.resultsandrequests.joinGameRequest;
import service.resultsandrequests.listGamesRequest;

import java.util.ArrayList;

import static chess.ChessGame.TeamColor.*;
import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {
    static final AuthDAO authDAO = new AuthMemoryDAO();
    static final GameService service = new GameService(new GameMemoryDAO(), authDAO);

    @BeforeEach
    void clearGames() throws DataAccessException{
        service.clearGames();
        authDAO.clearAuth();
    }

    //test creating one game
    //positive test for createGame, and listGames
    @Test
    void createGame() throws DataAccessException, InvalidAuthTokenException {
        AuthData auth = authDAO.createAuth("June");
        service.createGame(new createGameRequest("Game 1", auth.authToken()));
        ArrayList<GameData> games = service.listGames(new listGamesRequest(auth.authToken())).games();
        assert(games.size() == 1);
        assert(games.getFirst().gameName().equals("Game 1"));
    }

    //test clears games positive
    @Test
    void clear() throws DataAccessException, InvalidAuthTokenException {
        AuthData auth = authDAO.createAuth("July");
        service.createGame(new createGameRequest("game 2", auth.authToken()));
        service.clearGames();
        assert(service.listGames(new listGamesRequest(auth.authToken())).games().isEmpty());
    }

    //test throws error when creating game without authorization
    //create game negative
    @Test
    void createGameUnauthorized() throws DataAccessException, InvalidAuthTokenException{
        assertThrows(InvalidAuthTokenException.class, () -> service.createGame(new createGameRequest("game 3", "token")));
        AuthData auth = authDAO.createAuth("Mary");
        assert(service.listGames(new listGamesRequest(auth.authToken())).games().isEmpty());
    }

    //tests list games negative
    @Test
    void listGamesUnauthorized() {
        assertThrows(InvalidAuthTokenException.class, () ->
                service.listGames(new listGamesRequest("3")));
    }

    //positive test for joining Game
    @Test
    void joinGame() throws DataAccessException, InvalidAuthTokenException, UnavailableException {
        AuthData auth = authDAO.createAuth("Kim");
        createGameResult result = service.createGame(new createGameRequest("Game 4", auth.authToken()));
        service.joinGame(new joinGameRequest(result.gameID(), WHITE, auth));
        GameData game = service.listGames(new listGamesRequest(auth.authToken())).games().getFirst();
        assert(game.whiteUsername().equals("Kim"));
        assert(game.blackUsername() == null);
    }

    //negative test for joining game
    @Test
    void joinGameFull() throws DataAccessException, InvalidAuthTokenException, UnavailableException {
        AuthData auth1 = authDAO.createAuth("Pam");
        AuthData auth2 = authDAO.createAuth("Ruby");
        createGameResult game = service.createGame(new createGameRequest("Game 5", auth1.authToken()));
        service.joinGame(new joinGameRequest(game.gameID(), WHITE, auth1));
        assertThrows(UnavailableException.class, () -> service.joinGame(new joinGameRequest(game.gameID(), WHITE, auth2)));
    }

    //positive test for leaveGame
    @Test
    void leaveGame() throws DataAccessException, InvalidAuthTokenException, UnavailableException {
        AuthData auth1 = authDAO.createAuth("Harry");
        createGameResult result = service.createGame(new createGameRequest("Game 6", auth1.authToken()));
        service.joinGame(new joinGameRequest(result.gameID(), WHITE, auth1));
        GameData game = service.listGames(new listGamesRequest(auth1.authToken())).games().getFirst();
        assert(game.whiteUsername().equals("Harry"));
        service.leaveGame(new joinGameRequest(game.gameID(), WHITE, auth1));
        game = service.listGames(new listGamesRequest(auth1.authToken())).games().getFirst();
        assert(game.whiteUsername() == null);
    }

    //negative test for leaveGame
    @Test
    void leaveGameNotIn() throws DataAccessException, InvalidAuthTokenException {
        AuthData auth = authDAO.createAuth("Gary");
        createGameResult game = service.createGame(new createGameRequest("Game 7", auth.authToken()));
        assertThrows(UnavailableException.class, () -> service.leaveGame(new joinGameRequest(game.gameID(), WHITE, auth)));
    }

}
