package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

public class UserGameMove extends UserGameCommand{

    private final ChessMove move;

    public UserGameMove(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove(){
        return move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameMove that)) {
            return false;
        }
        return Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID()) &&
                getMove().equals(that.getMove());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID(), getMove());
    }
}
