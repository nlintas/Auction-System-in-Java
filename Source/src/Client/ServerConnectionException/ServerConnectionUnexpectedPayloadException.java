package Client.ServerConnectionException;

public class ServerConnectionUnexpectedPayloadException extends ServerConnectionException{

    public ServerConnectionUnexpectedPayloadException() {
    }

    public ServerConnectionUnexpectedPayloadException(String message) {
        super(message);
    }
}
