//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.Objects;

public class UnregisterClientPayload implements Serializable {

    //Attributes
    private int auctionID;

    //Constructors
    public UnregisterClientPayload(int auctionID) {
        this.auctionID = auctionID;
    }

    //Setters and Getters
    public int getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(int auctionID) {
        this.auctionID = auctionID;
    }

    //Methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnregisterClientPayload)) return false;
        UnregisterClientPayload that = (UnregisterClientPayload) o;
        return Objects.equals(getAuctionID(), that.getAuctionID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAuctionID());
    }

    @Override
    public String toString() {
        return "ClientLeavesAuctionPayload{" +
                "auctionID=" + auctionID +
                '}';
    }
}
