//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server;

import CommonClasses.Auction;

import java.util.TimerTask;

public class AuctionTerminateTask extends TimerTask {

    //Attributes
    private Auction auction;

    //Constructors
    public AuctionTerminateTask(Auction auction) {
        this.auction = auction;
    }

    //Methods
    @Override
    public void run() {
        //Conclude the auction
        auction.conclude();
    }
}
