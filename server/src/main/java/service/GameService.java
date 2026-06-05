package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
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

    public VoidResult joinGame(joinGameRequest req) throws DataAccessException, InvalidAuthTokenException, UnavailableException {
        //want to add so that same person can't join same game as both teams, but still allow two people with same
        //username?
        authServ.verifyAuth(req.auth().authToken());
        GameData game = gameDAO.getGame(req.gameID());
        if(req.color() == WHITE){
            if(game.whiteUsername() == null){
                gameDAO.addPlayer(req.gameID(), req.color(), req.auth().username());
                return new VoidResult();
            }
        } else {
            if(game.blackUsername() == null){
                gameDAO.addPlayer(req.gameID(), req.color(), req.auth().username());
                return new VoidResult();
            }
        }
        throw new UnavailableException("Team Not Available");
    }

    public void leaveGame(joinGameRequest req) throws DataAccessException, InvalidAuthTokenException, UnavailableException {
        authServ.verifyAuth(req.auth().authToken());
        GameData game = gameDAO.getGame(req.gameID());
        if(req.color() == WHITE && game.whiteUsername() != null && game.whiteUsername().equals(req.auth().username())){
            gameDAO.addPlayer(req.gameID(), req.color(), null);
        }
        else if(req.color() == BLACK && game.blackUsername() != null && game.blackUsername().equals(req.auth().username())){
            gameDAO.addPlayer(req.gameID(), req.color(), null);
        } else {
            throw new UnavailableException("Couldn't Leave Game");
        }
    }
}
