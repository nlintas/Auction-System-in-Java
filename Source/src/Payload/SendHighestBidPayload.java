//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class SendHighestBidPayload implements Serializable {

    //Attributes
    private Date bidCreationDate;
    private float highestBid;
    private String highestBidderIP;
    private int auctionID;

    //Constructors
    public SendHighestBidPayload(Date bidCreationDate, float highestBid, String highestBidderIP, int auctionID) {
        this.bidCreationDate = bidCreationDate;
        this.highestBid = highestBid;
        this.highestBidderIP = highestBidderIP;
        this.auctionID = auctionID;
    }

    //Setters and Getters
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

    public String getHighestBidderIP() {
        return highestBidderIP;
    }

    public void setHighestBidderIP(String highestBidderIP) {
        this.highestBidderIP = highestBidderIP;
    }

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
        SendHighestBidPayload that = (SendHighestBidPayload) o;
        return Float.compare(that.highestBid, highestBid) == 0 &&
                auctionID == that.auctionID &&
                Objects.equals(bidCreationDate, that.bidCreationDate) &&
                Objects.equals(highestBidderIP, that.highestBidderIP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bidCreationDate, highestBid, highestBidderIP, auctionID);
    }

    @Override
    public String toString() {
        return "SendHighestBidPayload{" +
                "bidCreationDate=" + bidCreationDate +
                ", bid=" + highestBid +
                ", bidderIP='" + highestBidderIP + '\'' +
                ", auctionID=" + auctionID +
                '}';
    }
}

