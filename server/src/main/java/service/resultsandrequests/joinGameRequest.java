package service.resultsandrequests;

import chess.ChessGame;
import model.AuthData;

public record joinGameRequest(int gameID, ChessGame.TeamColor color, AuthData auth) implements Request {

    @Override
    public boolean existingFields() {
        return color != null && auth != null && !auth.username().isBlank() && !auth.authToken().isBlank();
    }
}
