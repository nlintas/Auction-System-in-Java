//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package CommonClasses;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Bid implements Serializable {
    //Attributes
    private Date createdAt;
    private float bid;
    private String bidderIP;

    //Constructors
    public Bid(Date createdAt, float bid, String bidderIP) {
        this.createdAt = createdAt;
        this.bid = bid;
        this.bidderIP = bidderIP;
    }

    //Setters and Getters
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public float getBid() {
        return bid;
    }

    public void setBid(float bid) {
        this.bid = bid;
    }

    public String getBidderIP() {
        return bidderIP;
    }

    public void setBidderIP(String bidderIP) {
        this.bidderIP = bidderIP;
    }

    //Methods

    @Override
    public String toString() {
        return "Bid{" +
                "createdAt=" + createdAt +
                ", bid=" + bid +
                ", bidderIP='" + bidderIP + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bid bid1 = (Bid) o;
        return Float.compare(bid1.bid, bid) == 0 &&
                Objects.equals(createdAt, bid1.createdAt) &&
                Objects.equals(bidderIP, bid1.bidderIP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, bid, bidderIP);
    }
}
