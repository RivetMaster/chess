package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import exceptions.DataAccessException;
import dataaccess.GameDAO;
import exceptions.InvalidAuthTokenException;
import exceptions.UnavailableException;
import model.GameData;
import resultsandrequests.*;
import exceptions.InvalidRequestException;

import static chess.ChessGame.TeamColor.*;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthService authServ;

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        authServ = new AuthService(authDAO);
    }

    public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        authServ.verifyAuth(req.authToken());
        GameData game = new GameData(0, null, null, req.gameName(), new ChessGame());
        game = gameDAO.createGame(game);
        return new CreateGameResult(game.gameID());
    }

    public void clearGames() throws DataAccessException{
        gameDAO.clearGames();
    }

    public ListGamesResult listGames(ListGamesRequest req) throws DataAccessException, InvalidAuthTokenException{
        authServ.verifyAuth(req.authToken());
        return new ListGamesResult(gameDAO.listGames());
    }

    public void joinGame(JoinGameRequest req)
            throws DataAccessException, InvalidAuthTokenException, UnavailableException, InvalidRequestException {
        authServ.verifyAuth(req.auth().authToken());
        GameData game = gameDAO.getGame(req.gameID());
        if(req.playerColor() == WHITE){
            if(game.whiteUsername() == null){
                gameDAO.addPlayer(req.gameID(), req.playerColor(), req.auth().username());
                game.updateGameStatus();
                return;
            }
        } else {
            if(game.blackUsername() == null){
                gameDAO.addPlayer(req.gameID(), req.playerColor(), req.auth().username());
                game.updateGameStatus();
                return;
            }
        }
        throw new UnavailableException("Team Not Available");
    }

    public void leaveGame(JoinGameRequest req) throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
        authServ.verifyAuth(req.auth().authToken());
        GameData game = gameDAO.getGame(req.gameID());
        if(req.playerColor() == WHITE && game.whiteUsername() != null && game.whiteUsername().equals(req.auth().username())){
            gameDAO.addPlayer(req.gameID(), req.playerColor(), null);
            game.updateGameStatus();
        }
        else if(req.playerColor() == BLACK && game.blackUsername() != null && game.blackUsername().equals(req.auth().username())){
            gameDAO.addPlayer(req.gameID(), req.playerColor(), null);
            game.updateGameStatus();
        } else {
            throw new InvalidRequestException("Couldn't Leave Game");
        }
    }
}
