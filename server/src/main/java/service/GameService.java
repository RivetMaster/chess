package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.InvalidRequestException;
import service.exceptions.*;
import service.resultsandrequests.*;

import static chess.ChessGame.TeamColor.*;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthService authServ;

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        authServ = new AuthService(authDAO);
    }

    public createGameResult createGame(createGameRequest req) throws DataAccessException, InvalidAuthTokenException {
        authServ.verifyAuth(req.authToken());
        GameData game = new GameData(gameDAO.newGameID(), null, null, req.gameName(), new ChessGame());
        gameDAO.createGame(game);
        return new createGameResult(game.gameID());
    }

    public void clearGames() throws DataAccessException{
        gameDAO.clearGames();
    }

    public listGamesResult listGames(listGamesRequest req) throws DataAccessException, InvalidAuthTokenException{
        authServ.verifyAuth(req.authToken());
        return new listGamesResult(gameDAO.listGames());
    }

    public VoidResult joinGame(joinGameRequest req) throws DataAccessException, InvalidAuthTokenException, UnavailableException, InvalidRequestException {
        authServ.verifyAuth(req.auth().authToken());
        GameData game = gameDAO.getGame(req.gameID());
        if(req.playerColor() == WHITE){
            if(game.whiteUsername() == null){
                gameDAO.addPlayer(req.gameID(), req.playerColor(), req.auth().username());
                return new VoidResult();
            }
        } else {
            if(game.blackUsername() == null){
                gameDAO.addPlayer(req.gameID(), req.playerColor(), req.auth().username());
                return new VoidResult();
            }
        }
        throw new UnavailableException("Team Not Available");
    }

    public void leaveGame(joinGameRequest req) throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        authServ.verifyAuth(req.auth().authToken());
        GameData game = gameDAO.getGame(req.gameID());
        if(req.playerColor() == WHITE && game.whiteUsername() != null && game.whiteUsername().equals(req.auth().username())){
            gameDAO.addPlayer(req.gameID(), req.playerColor(), null);
        }
        else if(req.playerColor() == BLACK && game.blackUsername() != null && game.blackUsername().equals(req.auth().username())){
            gameDAO.addPlayer(req.gameID(), req.playerColor(), null);
        } else {
            throw new InvalidRequestException("Couldn't Leave Game");
        }
    }
}
