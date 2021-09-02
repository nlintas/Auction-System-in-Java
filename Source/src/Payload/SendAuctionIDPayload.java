//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.Objects;

public class SendAuctionIDPayload implements Serializable {

    //Attributes
    private int auctionID;

    //Constructors
    public SendAuctionIDPayload(int auctionID) {
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
        if (o == null || getClass() != o.getClass()) return false;
        SendAuctionIDPayload that = (SendAuctionIDPayload) o;
        return auctionID == that.auctionID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID);
    }

    @Override
    public String toString() {
        return "SendAuctionIDPayload{" +
                "auctionID=" + auctionID +
                '}';
    }
}
