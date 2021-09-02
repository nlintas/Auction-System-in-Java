//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.Objects;

public class RegisterClientPayload implements Serializable {

    //Attributes
    private int auctionID;

    //Constructors
    public RegisterClientPayload(int auctionID) {
        this.auctionID = auctionID;
    }

    //Setters and Getters
    public void setAuctionID(int auctionID) {
        this.auctionID = auctionID;
    }

    public int getAuctionID() {
        return auctionID;
    }

    //Methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterClientPayload that = (RegisterClientPayload) o;
        return auctionID == that.auctionID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID);
    }

    @Override
    public String toString() {
        return "ClientRegisterPayload{" +
                "auctionID=" + auctionID +
                '}';
    }
}
