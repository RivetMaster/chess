package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import resultsandrequests.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static client.ClientMain.State.*;
import static ui.EscapeSequences.*;

public class ClientUI {
    ServerFacade serverFacade;
    Map<Integer, Integer> gameIDs;

    public ClientUI(ServerFacade serverFacade){
        this.serverFacade = serverFacade;
        gameIDs = new HashMap<>();
    }


    public String helpMenu(ClientMain.State state){
        StringBuilder uiOutput = new StringBuilder();
        if(state == SIGNED_OUT){
            uiOutput.append(bold("REGISTER <Username> <Password> <Email> ")).append(": To create an account\n");
            uiOutput.append(bold("LOGIN <Username> <Password>            ")).append(": To log in to an existing account\n");
            uiOutput.append(bold("QUIT                                   ")).append(": Exit Program\n");
            uiOutput.append(bold("HELP                                   ")).append(": See List of Commands\n");
        } else if(state == SIGNED_IN){
            uiOutput.append(bold("CREATE <name>     ")).append(": Create a new chess game with a name\n");
            uiOutput.append(bold("LIST              ")).append(": See a list of all chess games\n");
            uiOutput.append(bold("JOIN <ID> <COLOR> ")).append(": Join a game as BLACK or WHITE\n");
            uiOutput.append(bold("OBSERVE <ID>      ")).append(": Choose a game to watch\n");
            uiOutput.append(bold("LOGOUT            ")).append(": Sign out of account\n");
            uiOutput.append(bold("HELP              ")).append(": See List of Commands\n");
        } else if(state == PLAYING_GAME){
            //TO DO
            uiOutput.append(bold("LEAVE ")).append(": Leave game\n");
            uiOutput.append(bold("HELP  ")).append(": See List of Commands\n");
        } else if(state == WATCHING_GAME){
            uiOutput.append(bold("LEAVE ")).append(": Leave game\n");
            uiOutput.append(bold("HELP  ")).append(": See List of Commands\n");
        }
        return uiOutput.toString();
    }

    public static String bold(String text){
        return SET_TEXT_BOLD + text + RESET_TEXT_BOLD_FAINT;
    }

    public static String red(String text){
        return SET_TEXT_COLOR_RED + text + RESET_TEXT_COLOR;
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

            StringBuilder output = new StringBuilder();
            output.append(bold("Game ID")).append(" | ").append(bold("Game Name")).append(" | <").append(bold("White Team"))
                    .append(", ").append(bold("Black Team")).append(">\n");
            for(int i = 0; i < gameIDs.size(); i++){ //for every key in gameIDs (1 to i)
                output.append(i+1).append("       | ");
                for(int k = 0; k < result.games().size(); k++){ //check every game returned by listGames
                    if(result.games().get(k).gameID() == gameIDs.get(i+1)){ //if the id of the game in list games corresponds to the key's value
                        output.append(result.games().get(k).gameName()).append("    | <");
                        output.append(Objects.requireNonNullElse(result.games().get(k).whiteUsername(), "AVAILABLE"));
                        output.append(", ");
                        output.append(Objects.requireNonNullElse(result.games().get(k).blackUsername(), "AVAILABLE"));
                        output.append(">\n");
                        break;
                    }
                }
            }
            return output.toString();
        } catch(ResponseException e){
           return handleError(e, null).message();
        }
    }

    public UIResponse join(int id, ChessGame.TeamColor color, String authToken) {
        if(!gameIDs.containsKey(id)){
            return new UIResponse(ClientUI.red("Error: Invalid Game ID"), null);
        }
        try{
            serverFacade.joinGame(new JoinGameRequest(gameIDs.get(id), color, new AuthData(authToken, null)));
            return new UIResponse("Successfully joined game " + id +" as team "
                    +color + ".\n" + printBoard(gameIDs.get(id), color, authToken), authToken);
        } catch(ResponseException e){
            return handleError(e, null);
        }
    }

    public UIResponse observe(int id, String authToken){
        if(!gameIDs.containsKey(id)){
            return new UIResponse(ClientUI.red("Error: Invalid Game ID"), null);
        }
        try {
            return new UIResponse("Now observing game " + id + "\n" + printBoard(gameIDs.get(id), WHITE, authToken), authToken);
        } catch(ResponseException e){
            return handleError(e, null);
        }
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

    private String printBoard(int gameID, ChessGame.TeamColor color, String authToken) throws ResponseException{
        StringBuilder board = new StringBuilder();
        ChessGame chessGame = getBoard(gameID, authToken);
        ChessBoard chessBoard = chessGame.getBoard();
        //Boarder Above
        if(color == WHITE){
            board.append(boarderColor("    a  b  c  d  e  f  g  h    ")).append("\n");
            board.append(drawBoard(chessBoard, WHITE));
            board.append(boarderColor("    a  b  c  d  e  f  g  h    ")).append("\n");
        } else if(color == BLACK){
            board.append(boarderColor("    h  g  f  e  d  c  b  a    ")).append("\n");
            board.append(drawBoard(chessBoard, BLACK));
            board.append(boarderColor("    h  g  f  e  d  c  b  a    ")).append("\n");

        }

        return board.toString();
    }

    private String boarderColor(String text){
        return SET_BG_COLOR_LIGHT_GREY +SET_TEXT_COLOR_BLUE + text + RESET_BG_COLOR +RESET_TEXT_COLOR;
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

    private String drawBoard(ChessBoard board, ChessGame.TeamColor color){
        StringBuilder boardRow = new StringBuilder();
        if(color == WHITE){
            for(int row = 8; row > 0; row--) {
                boardRow.append(boarderColor(" " + row +" "));
                for (int col = 1; col <= 8; col++) {
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    boardRow.append(drawPiece(row, col, piece));
                }
                boardRow.append(boarderColor(" " + row +" ")).append("\n");
            }
        } else if(color == BLACK){
            for(int row = 1; row <= 8; row++) {
                boardRow.append(boarderColor(" " + row +" "));
                for (int col = 8; col > 0 ; col--) {
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    boardRow.append(drawPiece(row, col, piece));
                }
                boardRow.append(boarderColor(" " + row +" ")).append("\n");
            }
        }

        return boardRow.toString();
    }

    private String drawPiece(int row, int col, ChessPiece chessPiece){
        StringBuilder piece = new StringBuilder();
        if (col % 2 != row % 2) {
            piece.append(SET_BG_COLOR_WHITE); //white square
        }
        if(chessPiece != null) {
            piece.append(chessPiece.toStringBoard());
        } else{
            piece.append(EMPTY);
        }
        piece.append(RESET_BG_COLOR);
        return piece.toString();
    }

    private UIResponse handleError(ResponseException e, String authToken){
        if(e.code() == ResponseException.Code.ServerError) {
            return new UIResponse(red("Error: Could not connect to the server."), authToken);
        }
        return new UIResponse(red(e.getMessage()), authToken);
    }
}
