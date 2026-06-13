package client;

import chess.ChessGame;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import resultsandrequests.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static chess.ChessGame.TeamColor.*;
import static ui.ClientUI.*;

public class ChessClient {
    ServerFacade serverFacade;
    Map<Integer, Integer> gameIDs;

    public ChessClient(ServerFacade serverFacade){
        this.serverFacade = serverFacade;
        gameIDs = new HashMap<>();
    }

    public UIResponse register(String username, String password, String email){
        RegisterUserRequest request = new RegisterUserRequest(username, password, email);
        try {
            RegisterUserResult result = serverFacade.register(request);
            return new UIResponse("Account successfully made and signed in as " + result.username(), result.authToken());
        } catch (ResponseException e){
            return handleError(e, null);
        }
    }

    public UIResponse login(String username, String password){
        LogInRequest request = new LogInRequest(username, password);
        try{
            LogInResult result = serverFacade.logIn(request);
            return new UIResponse("Successfully signed in as " + result.username(), result.authToken());
        } catch (ResponseException e){
            return handleError(e, null);
        }
    }

    public String clear(){
        try{
            serverFacade.clear();
            return "Successfully cleared database.";
        } catch (ResponseException e){
            return handleError(e, null).message();
        }
    }

    public UIResponse logout(String authToken){
        try{
            serverFacade.logOut(new LogOutRequest(authToken));
            return new UIResponse("Successfully logged out.", null);
        } catch( ResponseException e){
            return handleError(e, authToken);
        }
    }

    public String create(String gameName, String authToken){
        try{
           CreateGameResult result = serverFacade.createGame(new CreateGameRequest(gameName, authToken));
           updateGameIDList(authToken);
           return "Successfully created a new chess game with ID " + getKeyFromID(result.gameID());
        } catch(ResponseException e){
            return handleError(e, null).message();
        }
    }

    public String list(String authToken){
        try{
            ListGamesResult result = serverFacade.listGames(new ListGamesRequest(authToken));
            updateGameIDList(authToken);
            return listGames(gameIDs, result.games());
        } catch(ResponseException e){
           return handleError(e, null).message();
        }
    }

    public UIResponse join(int id, ChessGame.TeamColor color, String authToken) {
        if(!gameIDs.containsKey(id)){
            return new UIResponse(red("Error: Invalid Game ID"), null);
        }
        try{
            serverFacade.joinGame(new JoinGameRequest(gameIDs.get(id), color, new AuthData(authToken, null)));
            ChessGame chessGame = getBoard(gameIDs.get(id), authToken);
            return new UIResponse("Successfully joined game " + id +" as team "
                    +color + ".\n" + printBoard(color, chessGame), authToken);
        } catch(ResponseException e){
            return handleError(e, null);
        }
    }

    public UIResponse observe(int id, String authToken){
        if(!gameIDs.containsKey(id)){
            return new UIResponse(red("Error: Invalid Game ID"), null);
        }
        try {
            ChessGame chessGame = getBoard(gameIDs.get(id), authToken);
            return new UIResponse("Now observing game " + id + "\n" + printBoard(WHITE, chessGame), authToken);
        } catch(ResponseException e){
            return handleError(e, null);
        }
    }

    public UIResponse connect(){
        return null;
    }

    public UIResponse makeMove(){
        return null;
    }

    public UIResponse leaveGame(){
        return null;
    }

    public UIResponse resign(){
        return null;
    }

    private void updateGameIDList(String authToken) throws ResponseException{
        ListGamesResult result = serverFacade.listGames(new ListGamesRequest(authToken));
        gameIDs.clear();
        for(int i = 0; i < result.games().size(); i++){
            gameIDs.put(i+1, result.games().get(i).gameID());
        }
    }

    private int getKeyFromID(int gameID) throws ResponseException{
        for(Map.Entry<Integer, Integer> game : gameIDs.entrySet()) {
            // Find the value that matches gameID, return the key
            if(game.getValue() == gameID){
               return game.getKey();
            }
        }
        throw new ResponseException(ResponseException.Code.ServerError, "Error: Could not create game.");
    }

    private ChessGame getBoard(int gameID, String authToken) throws ResponseException {
        ArrayList<GameData> games = serverFacade.listGames(new ListGamesRequest(authToken)).games();
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game.game();
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Error: Game Does Not Exist");
    }


    private UIResponse handleError(ResponseException e, String authToken){
        if(e.code() == ResponseException.Code.ServerError) {
            return new UIResponse(red("Error: Could not connect to the server."), authToken);
        }
        return new UIResponse(red(e.getMessage()), authToken);
    }
}
