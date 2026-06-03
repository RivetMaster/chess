package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthService authServ;

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        authServ = new AuthService(authDAO);
    }

    public void createGame(String gameName, String authToken) throws DataAccessException, InvalidAuthTokenException {
        authServ.verifyAuth(authToken);
        GameData game = new GameData(gameDAO.newGameID(), null, null, gameName, new ChessGame());
        gameDAO.createGame(game);
    }

    public void clearGames() throws DataAccessException{
        gameDAO.clearGames();
    }

    public ArrayList<GameData> listGames(String authToken) throws DataAccessException, InvalidAuthTokenException{
        authServ.verifyAuth(authToken);
        return gameDAO.listGames();
    }
}
