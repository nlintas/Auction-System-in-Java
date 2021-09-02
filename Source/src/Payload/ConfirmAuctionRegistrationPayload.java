//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.util.Objects;

public class ConfirmAuctionRegistrationPayload {
    //Attributes
    private int auctionID;

    //Constructors
    public ConfirmAuctionRegistrationPayload(int auctionID) {
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
        if (!(o instanceof ConfirmAuctionRegistrationPayload)) return false;
        ConfirmAuctionRegistrationPayload that = (ConfirmAuctionRegistrationPayload) o;
        return Objects.equals(getAuctionID(), that.getAuctionID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAuctionID());
    }

    @Override
    public String toString() {
        return "ConfirmCancelingAuctionPayload{" +
                "auctionID=" + auctionID +
                '}';
    }
}
