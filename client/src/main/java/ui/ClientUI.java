package ui;

import chess.*;
import client.Client;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static chess.ChessGame.TeamColor.*;
import static client.Client.State.*;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class ClientUI {


    public static String helpMenu(Client.State state){
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
            uiOutput.append(bold("MOVE <Start Position> <End Position> "))
                    .append(": Move the piece at Start Position to End Position (ex. MOVE a2 a3)\n");
            uiOutput.append(bold("HIGHLIGHT <Piece Position>           ")).append(": Highlight all possible moves for the piece at Piece Position\n");
            uiOutput.append(bold("REDRAW                               ")).append(": Redraw the chess board\n");
            uiOutput.append(bold("LEAVE                                ")).append(": Leave game\n");
            uiOutput.append(bold("RESIGN                               ")).append(": Forfeit the game\n");
            uiOutput.append(bold("HELP                                 ")).append(": See List of Commands\n");
        } else if(state == WATCHING_GAME){
            uiOutput.append(bold("REDRAW                     ")).append(": Redraw the chess board\n");
            uiOutput.append(bold("HIGHLIGHT <Piece Position> ")).append(": Highlight all possible moves for the piece at Piece Position.\n");
            uiOutput.append(bold("LEAVE                      ")).append(": Leave game\n");
            uiOutput.append(bold("HELP                       ")).append(": See List of Commands\n");
        }
        return uiOutput.toString();
    }

    public static String bold(String text){
        return SET_TEXT_BOLD + text + RESET_TEXT_BOLD_FAINT;
    }

    public static String red(String text){
        return SET_TEXT_COLOR_RED + text + RESET_TEXT_COLOR;
    }

    public static String listGames(Map<Integer, Integer> gameIDs, ArrayList<GameData> games) {
        StringBuilder output = new StringBuilder();

        output.append(bold("Game ID")).append(" | ").append(bold("Game Name")).append(" | <").append(bold("White Team"))
                .append(", ").append(bold("Black Team")).append(">\n");

        //for every key in gameIDs (1 to i)
        for(int i = 0; i < gameIDs.size(); i++){
            output.append(i+1).append("       | ");
            //check every game returned by listGames
            for (GameData game : games) {
                //if the id of the game in list games corresponds to the key's value
                if (game.gameID() == gameIDs.get(i + 1)) {
                    output.append(game.gameName()).append("    | <");
                    output.append(Objects.requireNonNullElse(game.whiteUsername(), "AVAILABLE"));
                    output.append(", ");
                    output.append(Objects.requireNonNullElse(game.blackUsername(), "AVAILABLE"));
                    output.append(">\n");
                    break;
                }
            }
        }
        return output.toString();
    }

    public static String printBoard(ChessGame.TeamColor color, ChessGame chessGame) {
        StringBuilder board = new StringBuilder();
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

    private static String boarderColor(String text){
        return SET_BG_COLOR_LIGHT_GREY +SET_TEXT_COLOR_BLUE + text + RESET_BG_COLOR +RESET_TEXT_COLOR;
    }

    private static String drawBoard(ChessBoard board, ChessGame.TeamColor color){
        StringBuilder boardRow = new StringBuilder();
        switch(color) {
            case WHITE -> {
                for (int row = 8; row > 0; row--) {
                    boardRow.append(boarderColor(" " + row + " "));
                    for (int col = 1; col <= 8; col++) {
                        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                        boardRow.append(drawPiece(row, col, piece));
                    }
                    boardRow.append(boarderColor(" " + row + " ")).append("\n");
                }
            }
            case BLACK -> {
                for (int row = 1; row <= 8; row++) {
                    boardRow.append(boarderColor(" " + row + " "));
                    for (int col = 8; col > 0; col--) {
                        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                        boardRow.append(drawPiece(row, col, piece));
                    }
                    boardRow.append(boarderColor(" " + row + " ")).append("\n");
                }
            }
        }
        return boardRow.toString();
    }

    public static String printBoardHighlight(ChessGame.TeamColor color, ChessGame chessGame, ChessPosition spot) {
        StringBuilder board = new StringBuilder();
        ChessBoard chessBoard = chessGame.getBoard();
        Collection<ChessMove> highlight = chessGame.validMoves(spot);
        ArrayList<ChessPosition> highlights = new ArrayList<>();
        if(highlight != null) {
            for (var h : highlight) {
                highlights.add(h.getEndPosition());
            }
        }
        //Boarder Above
        if(color == WHITE){
            board.append(boarderColor("    a  b  c  d  e  f  g  h    ")).append("\n");
            board.append(drawBoardHighlighted(chessBoard, WHITE, highlights, spot));
            board.append(boarderColor("    a  b  c  d  e  f  g  h    ")).append("\n");
        } else if(color == BLACK){
            board.append(boarderColor("    h  g  f  e  d  c  b  a    ")).append("\n");
            board.append(drawBoardHighlighted(chessBoard, BLACK, highlights, spot));
            board.append(boarderColor("    h  g  f  e  d  c  b  a    ")).append("\n");
        }

        return board.toString();
    }

    private static String drawBoardHighlighted(ChessBoard board, ChessGame.TeamColor color, ArrayList<ChessPosition> highlights, ChessPosition spot){
        StringBuilder boardRow = new StringBuilder();
        switch(color) {
            case WHITE -> {
                for (int row = 8; row > 0; row--) {
                    boardRow.append(boarderColor(" " + row + " "));
                    for (int col = 1; col <= 8; col++) {
                        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                        if(!highlights.isEmpty() && highlights.contains(new ChessPosition(row, col))){
                            boardRow.append(drawPieceHighlight(row, col, piece));
                        } else if(spot != null && spot.equals(new ChessPosition(row, col))){
                            boardRow.append(SET_BG_COLOR_YELLOW);
                            if(piece != null) {
                                boardRow.append(piece.toStringBoard());
                            } else{
                                boardRow.append(EMPTY);
                            }
                            boardRow.append(RESET_BG_COLOR);
                        } else {
                            boardRow.append(drawPiece(row, col, piece));
                        }
                    }
                    boardRow.append(boarderColor(" " + row + " ")).append("\n");
                }
            }
            case BLACK -> {
                for (int row = 1; row <= 8; row++) {
                    boardRow.append(boarderColor(" " + row + " "));
                    for (int col = 8; col > 0; col--) {
                        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                        if(!highlights.isEmpty() && highlights.contains(new ChessPosition(row, col))){
                            boardRow.append(drawPieceHighlight(row, col, piece));
                        } else if(spot != null && spot.equals(new ChessPosition(row, col))){
                            boardRow.append(SET_BG_COLOR_YELLOW);
                            if(piece != null) {
                                boardRow.append(piece.toStringBoard());
                            } else{
                                boardRow.append(EMPTY);
                            }
                            boardRow.append(RESET_BG_COLOR);
                        } else {
                            boardRow.append(drawPiece(row, col, piece));
                        }
                    }
                    boardRow.append(boarderColor(" " + row + " ")).append("\n");
                }
            }
        }
        return boardRow.toString();
    }

    public static String drawPieceHighlight(int row, int col, ChessPiece chessPiece){
        StringBuilder piece = new StringBuilder();
        if (col % 2 != row % 2) {
            piece.append(SET_BG_COLOR_GREEN); //white square
        } else {
            piece.append(SET_BG_COLOR_DARK_GREEN); //black square
        }
        if(chessPiece != null) {
            piece.append(chessPiece.toStringBoard());
        } else{
            piece.append(EMPTY);
        }
        piece.append(RESET_BG_COLOR);
        return piece.toString();
    }

    public static String drawPiece(int row, int col, ChessPiece chessPiece){
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

}
