//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server;

import CommonClasses.Auction;
import Packets.PacketMessage;
import Payload.FirstCountdownPayload;
import Payload.SecondCountdownPayload;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static Packets.MessageType.COUNTDOWN_ONCE;
import static Packets.MessageType.COUNTDOWN_TWICE;

public class AuctionCountdownTask extends TimerTask {

    //Attributes
    private Auction auction;
    private Boolean canConclude;

    //Constructors
    public AuctionCountdownTask(Auction auction) {
        this.auction = auction;
        canConclude = true;
    }

    //Setters and Getters

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Boolean getCanConclude() {
        return canConclude;
    }

    public void setCanConclude(Boolean canConclude) {
        this.canConclude = canConclude;
    }

    //Methods
    @Override
    public void run() {

        Server server = Server.getInstance();
        // Set the auction to be resettable by new bids
        auction.setInCountDown(true);

        //Send the first countdown message to participants of the auction
        server.sendPackets(auction.getClientList(), new PacketMessage(COUNTDOWN_ONCE, new FirstCountdownPayload(auction.getId(), auction.getItem().getName(), auction.findHighestItemPrice())));
        //Pause execution 5 seconds
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Send the second countdown message to participants of the auction
        server.sendPackets(auction.getClientList(), new PacketMessage(COUNTDOWN_TWICE, new SecondCountdownPayload(auction.getId())));
        //Pause execution 5 seconds
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Conclude the auction
        if(canConclude){
            auction.conclude();
        }
    }
}
