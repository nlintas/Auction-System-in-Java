//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.Objects;

public class FirstCountdownPayload implements Serializable {

    //Attributes
    private int auctionID;
    private String itemName;
    private float highestBid;

    //Constructors
    public FirstCountdownPayload(int auctionID, String itemName, float highestBid) {
        this.auctionID = auctionID;
        this.itemName = itemName;
        this.highestBid = highestBid;
    }

    //Setters and Getters
    public int getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(int auctionID) {
        this.auctionID = auctionID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(float highestBid) {
        this.highestBid = highestBid;
    }

    //Methods
    @Override
    public String toString() {
        return "FirstCountdownPayload{" +
                "auctionID=" + auctionID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirstCountdownPayload that = (FirstCountdownPayload) o;
        return auctionID == that.auctionID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID);
    }
}
