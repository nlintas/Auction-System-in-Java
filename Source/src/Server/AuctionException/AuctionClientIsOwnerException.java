//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server.AuctionException;

public class AuctionClientIsOwnerException extends AuctionException {

    public AuctionClientIsOwnerException(String msg) {
        super(msg);
    }

    public AuctionClientIsOwnerException() {
        super();
    }
}
