//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.Objects;

public class NotifyAuctionWinnerPayload implements Serializable {

    //Attributes
    private int auctionID;
    private float highestBid;
    private String itemName;

    //Constructors
    public NotifyAuctionWinnerPayload(int auctionID, float highestBid, String itemName) {
        this.auctionID = auctionID;
        this.highestBid = highestBid;
        this.itemName = itemName;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotifyAuctionWinnerPayload that = (NotifyAuctionWinnerPayload) o;
        return auctionID == that.auctionID &&
                Float.compare(that.highestBid, highestBid) == 0 &&
                Objects.equals(itemName, that.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID, highestBid, itemName);
    }

    @Override
    public String toString() {
        return "NotifyAuctionWinnerPayload{" +
                "auctionID=" + auctionID +
                ", highestBid=" + highestBid +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}
