package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

public class GameService {

    GameDAO gameDAO;
    AuthDAO authDAO;
    AuthService authServ;

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        authServ = new AuthService(authDAO);
    }

    public void createGame(String gameName, String authToken) throws DataAccessException, InvalidAuthTokenException {
        authServ.verifyAuth(authToken);
        GameData game = new GameData(gameDAO.newGameID(), null, null, gameName, new ChessGame());
        gameDAO.createGame(game);
    }
}
