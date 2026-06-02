package model;

import chess.ChessGame;
import chess.ChessGame.TeamColor;

import static chess.ChessGame.TeamColor.*;

public record GameData(int gameID, String whiteUsername, String blackUsername,
                       String gameName, ChessGame game) {
    public GameData setUser(String username, TeamColor color){
        if(color == WHITE){
            return new GameData(gameID, username, blackUsername, gameName, game);
        }
        return new GameData(gameID, whiteUsername, username, gameName, game);
    }

    public GameData setGame(ChessGame newGame){
        return new GameData(gameID, whiteUsername, blackUsername, gameName, newGame);
    }
}
