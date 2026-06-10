package resultsandrequests;

import chess.ChessGame;
import model.AuthData;

public record JoinGameRequest(int gameID, ChessGame.TeamColor playerColor, AuthData auth) implements Request {

    @Override
    public boolean existingFields() {
        return playerColor != null && auth != null && !auth.username().isBlank() && !auth.authToken().isBlank();
    }

    public JoinGameRequest setAuth(AuthData auth){
        return new JoinGameRequest(gameID, playerColor, auth);
    }
}
