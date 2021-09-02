//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class AuctionUpdatePayload implements Serializable {
    //Attributes
    private int auctionID;
    private Date bidCreationDate;
    private float highestBid;
    private String itemName;
    private String highestBidderIP;
    private String itemDescription;

    //Constructors
    public AuctionUpdatePayload(int auctionID, Date bidCreationDate, float highestBid, String itemName, String highestBidderIP, String itemDescription) {
        this.auctionID = auctionID;
        this.bidCreationDate = bidCreationDate;
        this.highestBid = highestBid;
        this.itemName = itemName;
        this.highestBidderIP = highestBidderIP;
        this.itemDescription = itemDescription;
    }

    //Setters and Getters
    public int getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(int auctionID) {
        this.auctionID = auctionID;
    }

    public Date getBidCreationDate() {
        return bidCreationDate;
    }

    public void setBidCreationDate(Date bidCreationDate) {
        this.bidCreationDate = bidCreationDate;
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

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    //Methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuctionUpdatePayload that = (AuctionUpdatePayload) o;
        return auctionID == that.auctionID &&
                Float.compare(that.highestBid, highestBid) == 0 &&
                Objects.equals(bidCreationDate, that.bidCreationDate) &&
                Objects.equals(itemName, that.itemName) &&
                Objects.equals(highestBidderIP, that.highestBidderIP) &&
                Objects.equals(itemDescription, that.itemDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID, bidCreationDate, highestBid, itemName, highestBidderIP, itemDescription);
    }

    @Override
    public String toString() {
        return "AuctionUpdatePayload{" +
                "auctionID=" + auctionID +
                ", createdAt=" + bidCreationDate +
                ", bidPrice=" + highestBid +
                ", itemName='" + itemName + '\'' +
                ", highestBidderIP='" + highestBidderIP + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                '}';
    }
}
