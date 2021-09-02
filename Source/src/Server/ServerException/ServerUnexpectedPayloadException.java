//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server.ServerException;

public class ServerUnexpectedPayloadException extends ServerException {

    public ServerUnexpectedPayloadException(String msg) {
        super(msg);
    }

    public ServerUnexpectedPayloadException() {
        super();
    }
}
