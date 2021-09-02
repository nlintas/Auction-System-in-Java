//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import CommonClasses.Bid;

import java.io.Serializable;
import java.util.Objects;

public class MakeBidPayload implements Serializable {
    //Attributes
    private int auctionID;
    private float highestBid;

    //Constructors
    public MakeBidPayload(int auctionID, float highestBid) {
        this.auctionID = auctionID;
        this.highestBid = highestBid;
    }

    //Setters and Getters
    public int getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(int auctionID) {
        this.auctionID = auctionID;
    }

    public float getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(float highestBid) {
        this.highestBid = highestBid;
    }

    //Methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MakeBidPayload that = (MakeBidPayload) o;
        return auctionID == that.auctionID &&
                Float.compare(that.highestBid, highestBid) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID, highestBid);
    }

    @Override
    public String toString() {
        return "BidCreatePayload{" +
                "auctionID=" + auctionID +
                ", bidPrice=" + highestBid +
                '}';
    }
}
