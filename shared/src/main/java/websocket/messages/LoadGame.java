package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

public class LoadGame extends ServerMessage{
    private final ChessGame game;

    public LoadGame(ChessGame game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoadGame that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType() &&
                getGame().equals(that.getGame());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getGame());
    }
}
