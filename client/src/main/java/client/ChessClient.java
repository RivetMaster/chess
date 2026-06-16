package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import resultsandrequests.*;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameMove;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static chess.ChessGame.TeamColor.*;
import static ui.ClientUI.*;

public class ChessClient implements ServerMessageHandler{
    ServerFacade serverFacade;
    Map<Integer, Integer> gameIDs;
    ChessGame.TeamColor color;
    WebSocketFacade ws;

    public ChessClient(ServerFacade serverFacade) throws ResponseException {
        this.serverFacade = serverFacade;
        gameIDs = new HashMap<>();
        ws = new WebSocketFacade(serverFacade.getServerUrl(), this);
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
        try{
            updateGameIDList(authToken);
            if(!gameIDs.containsKey(id)){
                return new UIResponse(red("Error: Invalid Game ID"), null);
            }
            serverFacade.joinGame(new JoinGameRequest(gameIDs.get(id), color, new AuthData(authToken, null)));
            this.color = color;
            return new UIResponse("Successfully joined game " + id +" as team "
                    +color, authToken);
        } catch(ResponseException e){
            return handleError(e, null);
        }
    }

    public UIResponse observe(int id, String authToken){
        try {
            updateGameIDList(authToken);
            if (!gameIDs.containsKey(id)) {
                return new UIResponse(red("Error: Invalid Game ID"), null);
            }
            color = WHITE;
            return new UIResponse("Now observing game " + id, authToken);
        } catch(ResponseException e){
            return handleError(e, null);
        }
    }

    public void connect(String authToken, int gameID){
        try {
            ws.connect(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
        } catch(ResponseException e) {
            System.out.println(handleError(e, null).message());
        }
    }

    public void makeMove(String authToken, int id, String start, String end){
        ChessMove move = new ChessMove(toPos(start), toPos(end));
        try {
            ws.makeMove(new UserGameMove(authToken, id, move));
        } catch(ResponseException e){
            System.out.println(red(handleError(e, null).message()));
        }
    }

    public ChessPosition toPos(String letterNotation){
        String letterCol = letterNotation.substring(0, 1).toLowerCase();
        int row = Integer.parseInt(letterNotation.substring(1));
        int col = 0;
        switch(letterCol){
            case("a") -> col = 1;
            case("b") -> col = 2;
            case("c") -> col = 3;
            case("d") -> col = 4;
            case("e") -> col = 5;
            case("f") -> col = 6;
            case("g") -> col = 7;
            case("h") -> col = 8;
        }
        return new ChessPosition(row, col);
    }

    public UIResponse leaveGame(String authToken, int id){
        try {
            ws.leaveGame(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, id));
            return new UIResponse("Left game", authToken);
        } catch(ResponseException e) {
            return handleError(e, null);
        }
    }

    public UIResponse resign(String authToken, int id){
        try {
            ws.resignGame(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, id));
            return new UIResponse("Resigned from game", authToken);
        } catch(ResponseException e) {
            return handleError(e, null);
        }
    }

    public String redrawBoard(int gameID, String authToken) {
        String response;
        try {
            response = printBoard(color, getBoard(gameID, authToken));
        } catch (ResponseException e){
            response = handleError(e, null).message();
        }
        return response;
    }

    public String highlightBoard(int gameID, String authToken, ChessPosition pos) {
        String response;
        try {
            if(getBoard(gameID, authToken).getBoard().getPiece(pos) != null) {
                response = printBoardHighlight(color, getBoard(gameID, authToken), pos);
            } else{
                response = "No piece found to highlight";
            }
        } catch (ResponseException e){
            response = handleError(e, null).message();
        }
        return response;
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

    @Override
    public void notify(Notification notification) {
        System.out.println();
        System.out.println(notification.getMessage());
        System.out.print(">>>> ");
    }

    @Override
    public void notifyError(ErrorMessage error) {
        System.out.println();
        System.out.println(red(error.getMessage()));
        System.out.print(">>>> ");
    }

    @Override
    public void loadGameNotify(LoadGame game) {
        System.out.println();
        System.out.println(printBoard(color, game.getGame()));
        System.out.print(">>>> ");
    }
}
