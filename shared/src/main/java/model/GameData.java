package model;

import chess.ChessGame;
import chess.ChessGame.TeamColor;

import static chess.ChessGame.TeamColor.*;

public record GameData(int gameID, String whiteUsername, String blackUsername,
                       String gameName, ChessGame game) {
    public boolean verifyFields(){
        return gameName != null && !gameName.isBlank() && game != null;
    }

    public GameData setUser(String username, TeamColor color){
        if(color == WHITE){
            return new GameData(gameID, username, blackUsername, gameName, game);
        }
        return new GameData(gameID, whiteUsername, username, gameName, game);
    }

    public GameData setGameID(int ID){
        return new GameData(ID, whiteUsername, blackUsername, gameName, game);
    }
}
