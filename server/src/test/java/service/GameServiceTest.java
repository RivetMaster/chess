package service;


import dataaccess.AuthDAO;
import dataaccess.AuthMemoryDAO;
import dataaccess.DataAccessException;
import dataaccess.GameMemoryDAO;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {
    static final AuthDAO authDAO = new AuthMemoryDAO();
    static final GameService service = new GameService(new GameMemoryDAO(), authDAO);

    @BeforeEach
    void clearGames() throws DataAccessException{
        service.clearGames();
    }

    //test creating one game
    //positive test for createGame, and listGames
    @Test
    void createGame() throws DataAccessException, InvalidAuthTokenException {
        AuthData auth = authDAO.createAuth("June");
        service.createGame("Game 1", auth.authToken());
        List<GameData> games = service.listGames(auth.authToken());
        assert(games.size() == 1);
        assert(games.getFirst().gameName().equals("Game 1"));
    }

    //test clears games positive
    @Test
    void testClear() throws DataAccessException, InvalidAuthTokenException {
        AuthData auth = authDAO.createAuth("July");
        assert(service.listGames(auth.authToken()).isEmpty());
    }

    //tests list games negative
    @Test
    void testClearUnauthorized() {
        assertThrows(InvalidAuthTokenException.class, () -> service.listGames("3"));
    }


}
