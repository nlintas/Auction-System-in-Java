//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server.ServerException;

public class ServerNotClientOwnerException extends ServerException {

    public ServerNotClientOwnerException(String msg) {
        super(msg);
    }

    public ServerNotClientOwnerException() {
        super();
    }
}
