//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Objects;

public class AuctionListPayload<A> implements Serializable {

    //Attributes
    private LinkedList<AuctionListItem> auctionList;

    //Constructors
    public AuctionListPayload(LinkedList<AuctionListItem> auctionList) {
        this.auctionList = auctionList;
    }

    //Setters and Getters
    public LinkedList<AuctionListItem> getAuctionList() {
        return auctionList;
    }

    public void setAuctionList(LinkedList<AuctionListItem> auctionList) {
        this.auctionList = auctionList;
    }

    //Methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuctionListPayload<A> that = (AuctionListPayload<A>) o;
        return Objects.equals(auctionList, that.auctionList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionList);
    }

    @Override
    public String toString() {
        return "AuctionListPayload{" +
                "auctionList=" + auctionList +
                '}';
    }
}
