//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.Objects;

public class SecondCountdownPayload implements Serializable {


    //Attributes
    private int auctionID;

    //Constructors
    public SecondCountdownPayload(int auctionID) {
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
    public String toString() {
        return "FirstCountdownPayload{" +
                "auctionID=" + auctionID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecondCountdownPayload that = (SecondCountdownPayload) o;
        return auctionID == that.auctionID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID);
    }
}
