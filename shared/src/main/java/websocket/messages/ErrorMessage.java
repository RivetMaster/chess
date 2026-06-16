package websocket.messages;

import java.util.Objects;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(String message) {
        super(ServerMessage.ServerMessageType.ERROR);
        this.errorMessage = message;
    }

    public String getMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ErrorMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType() &&
                errorMessage.equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getMessage());
    }

}
