package service.resultsandrequests;

import chess.ChessGame;
import model.AuthData;

public record joinGameRequest(int gameID, ChessGame.TeamColor playerColor, AuthData auth) implements Request {

    @Override
    public boolean existingFields() {
        return playerColor != null && auth != null && !auth.username().isBlank() && !auth.authToken().isBlank();
    }

    public joinGameRequest setAuth(AuthData auth){
        return new joinGameRequest(gameID, playerColor, auth);
    }
}
