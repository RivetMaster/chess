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

    public VoidResult joinGame(JoinGameRequest req)
            throws DataAccessException, InvalidAuthTokenException, UnavailableException, InvalidRequestException {
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

    public void leaveGame(JoinGameRequest req) throws DataAccessException, InvalidAuthTokenException, InvalidRequestException {
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
