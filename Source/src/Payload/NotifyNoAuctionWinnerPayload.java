//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.Objects;

public class NotifyNoAuctionWinnerPayload implements Serializable {

    //Attributes
    private int auctionID;
    private String itemName;
    private float itemStartingPrice;

    public NotifyNoAuctionWinnerPayload(int auctionID, String itemName, float itemStartingPrice) {
        this.auctionID = auctionID;
        this.itemName = itemName;
        this.itemStartingPrice = itemStartingPrice;
    }

    //Getters and Setters
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

    public float getItemStartingPrice() {
        return itemStartingPrice;
    }

    public void setItemStartingPrice(float itemStartingPrice) {
        this.itemStartingPrice = itemStartingPrice;
    }

    //Methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotifyNoAuctionWinnerPayload that = (NotifyNoAuctionWinnerPayload) o;
        return auctionID == that.auctionID &&
                Float.compare(that.itemStartingPrice, itemStartingPrice) == 0 &&
                Objects.equals(itemName, that.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID, itemName, itemStartingPrice);
    }

    @Override
    public String toString() {
        return "NoAuctionWinnerPayload{" +
                "auctionID=" + auctionID +
                ", itemName='" + itemName + '\'' +
                ", itemStartingPrice=" + itemStartingPrice +
                '}';
    }
}
