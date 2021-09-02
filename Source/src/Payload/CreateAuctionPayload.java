//Authors: Memli Restelica, Donat Salihu, Phillipos Kalatzis, Nikolaos Lintas
package Payload;

import java.io.Serializable;
import java.util.Objects;

public class CreateAuctionPayload implements Serializable {

    //Attributes
    private String auctionType;
    private float itemStartingPrice;
    private String itemName;
    private String itemDescription;
    private int auctionDuration;

    //Constructors
    public CreateAuctionPayload(String auctionType, float itemStartingPrice, String itemName,
                                String itemDescription, int auctionDuration) {
        this.auctionType = auctionType;
        this.itemStartingPrice = itemStartingPrice;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.auctionDuration = auctionDuration;
    }

    //Setters and Getter
    public String getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
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

    public long getAuctionDuration() {
        return auctionDuration;
    }

    public void setAuctionDuration(int auctionDuration) {
        this.auctionDuration = auctionDuration;
    }

    //Methods
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateAuctionPayload that = (CreateAuctionPayload) o;
        return Float.compare(that.itemStartingPrice, itemStartingPrice) == 0 &&
                Objects.equals(auctionType, that.auctionType) &&
                Objects.equals(itemName, that.itemName) &&
                Objects.equals(itemDescription, that.itemDescription) &&
                Objects.equals(auctionDuration, that.auctionDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionType, itemStartingPrice, itemName, itemDescription, auctionDuration);
    }

    @Override
    public String toString() {
        return "AuctionCreatePayload{" +
                "type='" + auctionType + '\'' +
                ", startingPrice=" + itemStartingPrice +
                ", itemName='" + itemName + '\'' +
                ", description='" + itemDescription + '\'' +
                ", endAt=" + auctionDuration +
                '}';
    }
}
