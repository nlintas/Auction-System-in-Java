//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server.AuctionException;

public class AuctionLowBidException extends AuctionException {

    public AuctionLowBidException(String msg) {
        super(msg);
    }

    public AuctionLowBidException() {
        super();
    }
}
