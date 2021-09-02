//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.Objects;

public class AuctionListItem implements Serializable {

    //Attributes
    private int auctionID;
    private float itemStartingPrice;
    private String itemName;
    private String itemDescription;
    private String auctionOwnerIP;
    private float highestBid;

    //Constructors
    public AuctionListItem(int auctionID, float itemStartingPrice, String itemName, String itemDescription, String auctionOwnerIP, float highestBid) {
        this.auctionID = auctionID;
        this.itemStartingPrice = itemStartingPrice;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.auctionOwnerIP = auctionOwnerIP;
        this.highestBid = highestBid;
    }

    //Setters and Getters
    public int getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(int auctionID) {
        this.auctionID = auctionID;
    }

    public float getItemStartingPrice() {
        return itemStartingPrice;
    }

    public void setItemStartingPrice(float itemStartingPrice) {
        this.itemStartingPrice = itemStartingPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getAuctionOwnerIP() {
        return auctionOwnerIP;
    }

    public void setAuctionOwnerIP(String auctionOwnerIP) {
        this.auctionOwnerIP = auctionOwnerIP;
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
        AuctionListItem that = (AuctionListItem) o;
        return auctionID == that.auctionID &&
                Float.compare(that.itemStartingPrice, itemStartingPrice) == 0 &&
                Float.compare(that.highestBid, highestBid) == 0 &&
                Objects.equals(itemName, that.itemName) &&
                Objects.equals(itemDescription, that.itemDescription) &&
                Objects.equals(auctionOwnerIP, that.auctionOwnerIP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID, itemStartingPrice, itemName, itemDescription, auctionOwnerIP, highestBid);
    }

    @Override
    public String toString() {
        return "AuctionListItem{" +
            "auctionID=" + auctionID +
            ", itemStartingPrice=" + itemStartingPrice +
            ", itemName='" + itemName + '\'' +
            ", itemDescription='" + itemDescription + '\'' +
            ", auctionOwnerIP='" + auctionOwnerIP + '\'' +
            ", highestBid=" + highestBid +
            '}';
    }
}
