//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;

public class ConcludeAuctionPayload implements Serializable {
    //Attributes
    private int auctionID;
    private float highestBid;
    private String itemName;
    private String highestBidderIP;

    //Constructors
    public ConcludeAuctionPayload(int auctionID, float highestBid, String itemName, String highestBidderIP) {
        this.auctionID = auctionID;
        this.highestBid = highestBid;
        this.itemName = itemName;
        this.highestBidderIP = highestBidderIP;
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

    public String getHighestBidderIP() {
        return highestBidderIP;
    }

    public void setHighestBidderIP(String highestBidderIP) {
        this.highestBidderIP = highestBidderIP;
    }

    //Methods
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        ConcludeAuctionPayload that = (ConcludeAuctionPayload) object;
        return Float.compare(that.highestBid, highestBid) == 0 && java.util.Objects.equals(auctionID, that.auctionID) && java.util.Objects.equals(itemName, that.itemName) && java.util.Objects.equals(highestBidderIP, that.highestBidderIP);
    }

    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), auctionID, highestBid, itemName, highestBidderIP);
    }

    @Override
    public String toString() {
        return "ConcludeAuctionPayload{" +
                "auctionID=" + auctionID +
                ", biggestBid=" + highestBid +
                ", itemName='" + itemName + '\'' +
                ", WinnerIpAddress='" + highestBidderIP + '\'' +
                '}';
    }
}
