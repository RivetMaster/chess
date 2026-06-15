package model;

import chess.ChessGame;
import chess.ChessGame.TeamColor;

import static chess.ChessGame.GameStatus.PLAYING;
import static chess.ChessGame.GameStatus.WAITING;
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

    public GameData setGameID(int newGameID){
        return new GameData(newGameID, whiteUsername, blackUsername, gameName, game);
    }

    public void updateGameStatus() {
        if(whiteUsername != null && blackUsername != null && game.getStatus().equals(WAITING)){
            game.setGameStatus(PLAYING);
        } else if(game.getStatus().equals(PLAYING) && (whiteUsername == null || blackUsername == null)){
            game.setGameStatus(WAITING);
        }
    }
}
