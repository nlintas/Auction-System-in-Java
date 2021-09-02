//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server.ServerException;

public class ServerShutdownAlreadyInitiatedException extends ServerException {

    public ServerShutdownAlreadyInitiatedException(String msg) {
        super(msg);
    }

    public ServerShutdownAlreadyInitiatedException() {
        super();
    }
}
