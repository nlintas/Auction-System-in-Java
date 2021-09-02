//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.Objects;

public class CancelAuctionPayload implements Serializable {

    //Attributes
    private int auctionID;

    //Constructors
    public CancelAuctionPayload(int auctionID){
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
        if (!(o instanceof CancelAuctionPayload)) return false;
        CancelAuctionPayload that = (CancelAuctionPayload) o;
        return Objects.equals(auctionID, that.auctionID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID);
    }

    @Override
    public String toString() {
        return "CancelAuctionPayload{" +
                "auctionID=" + auctionID +
                '}';
    }
}
