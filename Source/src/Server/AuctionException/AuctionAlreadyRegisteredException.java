//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server.AuctionException;

public class AuctionAlreadyRegisteredException extends AuctionException {

    public AuctionAlreadyRegisteredException(String msg) {
        super(msg);
    }

    public AuctionAlreadyRegisteredException() {
        super();
    }
}
