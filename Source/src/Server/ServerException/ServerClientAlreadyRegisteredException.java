//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server.ServerException;

public class ServerClientAlreadyRegisteredException extends ServerException {

    public ServerClientAlreadyRegisteredException(String msg) {
        super(msg);
    }

    public ServerClientAlreadyRegisteredException() {
        super();
    }
}
