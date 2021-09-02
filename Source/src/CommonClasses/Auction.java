//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package CommonClasses;

import Packets.PacketMessage;
import Payload.*;
import Server.AuctionCountdownTask;
import Server.AuctionException.*;
import Server.AuctionTerminateTask;
import Server.Client;
import Server.Server;
import Server.ServerException.ServerNotClientOwnerException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import static Packets.MessageType.*;

public class Auction implements Serializable {

    // Attributes
    //This attribute is used to increment the auction id on creation
    private final static AtomicInteger incrementer = new AtomicInteger();
    private int id;
    private String clientOwner;
    private Date createdAt;
    private Date terminateAt;
    private boolean isInCountDown;
    // Assumption: the words for the 2 types of auctions are "Time_Fixed" and "Time_With_Reset"
    private String type;
    private  Timer timer;
    private LinkedList<Client> clientList;
    private LinkedList<Bid> bidList;
    private Item item;
    private AuctionCountdownTask countdownTask;

    // Constructors
    public Auction(String clientOwner, Date terminateAt, String type,
                   LinkedList<Client> clientList, LinkedList<Bid> bidList, Item item) {
        id = incrementer.incrementAndGet();
        this.clientOwner = clientOwner;
        this.createdAt = new Date();
        this.terminateAt = terminateAt;
        this.type = type;
        this.timer = new Timer();
        countdownTask = new AuctionCountdownTask(this);

        //Determine the type of the auction and set the appropriate timer task
        if (type.equals("Time_Fixed")) {
            timer.schedule(new AuctionTerminateTask(this), terminateAt);
        } else {
            timer.schedule(countdownTask, terminateAt);
        }

        this.clientList = clientList;
        this.bidList = bidList;
        this.item = item;
        isInCountDown = false;
    }

    public Auction(String clientOwner, Date terminateAt, String type, Item item) {
        id = incrementer.incrementAndGet();
        this.clientOwner = clientOwner;
        createdAt = new Date();
        this.terminateAt = terminateAt;
        this.type = type;
        timer = new Timer();
        bidList = new LinkedList<>();
        clientList = new LinkedList<>();
        this.item = item;
        isInCountDown = false;
        countdownTask = new AuctionCountdownTask(this);

        //Determine the type of the auction and set the appropriate timer task
        if (type.equals("Time_Fixed")) {
            timer.schedule(new AuctionTerminateTask(this), terminateAt);
        } else {
            timer.schedule(countdownTask, terminateAt);
        }

    }

    // Getters and Setters
    public static AtomicInteger getIncrementer() {
        return incrementer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientOwner() {
        return clientOwner;
    }

    public void setClientOwner(String clientOwner) {
        this.clientOwner = clientOwner;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getTerminateAt() {
        return terminateAt;
    }

    public void setTerminateAt(Date terminateAt) {
        this.terminateAt = terminateAt;
    }

    public boolean isInCountDown() {
        return isInCountDown;
    }

    public void setInCountDown(boolean inCountDown) {
        isInCountDown = inCountDown;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public LinkedList<Client> getClientList() {
        return clientList;
    }

    public void setClientList(LinkedList<Client> clientList) {
        this.clientList = clientList;
    }

    public LinkedList<Bid> getBidList() {
        return bidList;
    }

    public void setBidList(LinkedList<Bid> bidList) {
        this.bidList = bidList;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public AuctionCountdownTask getCountdownTask() {
        return countdownTask;
    }

    public void setCountdownTask(AuctionCountdownTask countdownTask) {
        this.countdownTask = countdownTask;
    }

    //Methods
    /*
        Precondition: Method expects to receive a Client object that is not registered in the auction
        Postcondition: Method adds the received Client object into the clientList variable. The Client object has its
        list of registered auctions updated to include this auction ID.
        This only occurs if the Client has not already been registered and is not the owner.
        Method returns nothing.
        NOTE:
        If the client is the owner of the auction an AuctionClientIsOwnerException is thrown.
        If the client is all ready registered a AuctionAlreadyRegisteredException is thrown.
     */
    public void addClient(Client client) throws AuctionAlreadyRegisteredException, AuctionClientIsOwnerException {

        //Check if the client is the owner of the auction
        if (!clientOwner.equals(client.getSocket().getInetAddress().getHostAddress())) {
            // Check if the client is registered in the auction
            if (!clientList.contains(client)) {

                //Add this auction as one of the clients registered auctions
                client.getRegisteredAuctions().addFirst(id);

                //Register the client as a participant of the auction
                clientList.add(client);

            } else {
                throw new AuctionAlreadyRegisteredException("Client is already registered");
            }
        } else {
            throw new AuctionClientIsOwnerException("The onwer of the auction can not register to their own auction");
        }
    }


    /*
        Precondition: Method expects to receive a Client object that is already registered in the auction
        Postcondition: Method removes the provided Client object from the list of registered clients in the clientList variable.
        At the same time the client has the auction removed from their list of registered auctions.
        These operations only occur if the Client has been already registered and does not posses the highest bid in the auction.
        Method returns nothing.
        NOTE:
        If the provided Client possesses the highest bif in the auction a AuctionHighBidException is thrown.
        If the provided Client is not registered in the auction a AuctionNotRegisteredException is thrown.
    */
    public void removeClient(Client client)
            throws AuctionHighBidException, AuctionNotRegisteredException {

        // Check if the client is registered in the auction
        if (clientList.contains(client)) {

            //Check if the client has the highest bid
            if (!bidList.isEmpty() && bidList.getFirst().getBidderIP()
                    .equals(client.getSocketAddress().getAddress().getHostAddress())) {
                throw new AuctionHighBidException("User has the highest bid");
            }

            //Remove this auction from the clients list of registered auctions
            int auctionIndex = client.getRegisteredAuctions().indexOf(id);
            if (auctionIndex != -1) {
                client.getRegisteredAuctions().remove(auctionIndex);
            }

            //Unregister client from auction
            clientList.remove(client);
        } else {
            throw new AuctionNotRegisteredException("The client is not registered in the auction");
        }
    }

    /*
        Preconditions: This method expects to receive a Client object that is registered in the auction.
        Postconditions: The method removes the provided Client object from the auctions clientList variable.
        This is done regardless of if the client has the largest auction. The method appropriately removes the highest bid
        with the next biggest one if it was made by the client desired to be removed. In this event all participants
        are sent a packet message to update them on the new highest bid owner.
        Method returns nothing.
        NOTE:
        If the client is not registered in the auction a AuctionNotRegisteredException is thrown.
     */

    public void forcefullyRemoveClient(Client client) throws AuctionNotRegisteredException {

        Server server = Server.getInstance();

        // Check if the client is registered in the auction
        if (clientList.contains(client)) {
            //Check if the client has the highest bid
            if (bidList.getFirst().getBidderIP().equals(client.getSocket().getInetAddress().getHostAddress()) && !bidList.isEmpty()) {
                bidList.remove(0);

                float highestBid = item.getStartingPrice();

                if (!bidList.isEmpty()) {
                    highestBid = bidList.getFirst().getBid();
                }

                // Update participants for the new highest bid and owner
                AuctionUpdatePayload auctionUpdate = new AuctionUpdatePayload(id, createdAt, highestBid,
                        item.getName(), client.getSocketAddress().getAddress().getHostAddress(),
                        item.getDescription());
                server.sendPackets(clientList, new PacketMessage(HIGHEST_BID_OWNER_LOST, auctionUpdate));
            }

            //Unregister client from auction
            clientList.remove(client);

        } else {
            throw new AuctionNotRegisteredException("Not registered in auction");
        }
    }


    /*
        Precondition: Method expects to receive a Client object that is already registered in the auction and a Bid object that
        represents a bid made by the Client object.
        Postcondition: Method adds the received Bid object as a valid bid into the bidList variable,
        the provided Client also has its numberOfHighBids variable increment by 1 upon success.
        These operations can only occur if the Bid object has a larger bid than the current highest bid (or starting price) and
        the Client object is already registered in the auction and not the owner.
        Method returns nothing.
        NOTE:
        If the provided Client is not registered a AuctionNotRegisteredException is thrown.
        If the Bid provided is lower than then the current bid a AuctionLowBidException is thrown.
        If the Client provided is the owner of the auction a AuctionClientIsOwnerException is thrown
    */

    public void addBid(Bid bid, Client client)
            throws AuctionNotRegisteredException, AuctionLowBidException, AuctionClientIsOwnerException {

        //Check if the bidder is the auction owner
        if (!clientOwner.equals(client.getSocket().getInetAddress().getHostAddress())) {
            //Check if the client is registered
            if (clientList.contains(client)) {

                //Check if the new bid is higher than the starting price or the latest bid
                if ((!bidList.isEmpty() && bidList.getFirst().getBid() < bid.getBid())
                        || (bidList.isEmpty() && bid.getBid() > item.getStartingPrice())) {

                    Server server = Server.getInstance();

                    //Find the largest bid made so far, if none have been made the starting price is provided.
                    float highestBid = findHighestBid().getBid();

                    //Reduce the previous highest Bid owners number of high bids by 1
                    if(!bidList.isEmpty() && server.getClientHandlers().containsKey(bidList.getFirst().getBidderIP())){
                        server.getClientHandlers().get(bidList.getFirst().getBidderIP()).getClient().lostHighBid();
                    }

                    //Add new bid as highest
                    bidList.addFirst(bid);
                    //increment client for the number of high bids they have made
                    client.madeHighBid();

                    //Check if the auction timer is resettable and in the final countdown
                    if (type.equals("Time_With_Reset") && isInCountDown) {

                        countdownTask.setCanConclude(false);
                        countdownTask.cancel();
                        timer.cancel();
                        timer.purge();
                        timer = null;
                        countdownTask = null;
                        timer = new Timer();
                        countdownTask = new AuctionCountdownTask(this);
                        timer.schedule(countdownTask, 0);
                    }

                    // Update participants for the new highest bid
                    AuctionUpdatePayload auctionUpdate = new AuctionUpdatePayload(id, createdAt, highestBid,
                            item.getName(), client.getSocketAddress().getAddress().getHostAddress(),
                            item.getDescription());
                    PacketMessage auctionUpdatePacket = new PacketMessage(AUCTION_UPDATE, auctionUpdate);
                    server.sendPackets(clientList, auctionUpdatePacket);

                    //Check if a packet can be sent to the auction owner
                    if (server.getClientHandlers().containsKey(clientOwner)) {
                        try {
                            server.getClientHandlers().get(clientOwner).sendPacket(auctionUpdatePacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    throw new AuctionLowBidException("Bid is lower than highest price");
                }
            } else {
                throw new AuctionNotRegisteredException("Not registered in auction");
            }
        } else {
            throw new AuctionClientIsOwnerException("The client is the owner of the auction");
        }
    }

    /*
        Precondition: Method expects to receive a Client object that is the owner of the auction
        Postcondition: Method cancels the auction and all clients are unregistered from the auction.
        The owner and participants are send a packet informing the auctions cancellation
        The auction is removed from the server. This can only occur if the auction does not have any existing bids.
        Method returns nothing.
        NOTE:
        If any bids have been made a AuctionActiveException is thrown.
        If the provided Client is not the owner of the auction a ServerNotClientOwnerException.
    */
    public void cancel(Client client) throws AuctionActiveException, ServerNotClientOwnerException {

        if (clientOwner.equals(client.getSocketAddress().getAddress().getHostAddress())) {
            //Check if a bid has been made
            if (bidList.isEmpty()) {
                //Get Server Instance
                Server server = Server.getInstance();
                //Destroy the Timer
                timer.cancel();
                timer = null;
                //Inform all registered users of the auctions cancellation
                PacketMessage auctionCanceledPacket = new PacketMessage(AUCTION_CANCELLED, new ConfirmAuctionCancellationPayload(id));
                server.sendPackets(clientList, auctionCanceledPacket);

                //Check if a packet can be sent to the auction owner
                if (server.getClientHandlers().containsKey(clientOwner)) {
                    try {
                        server.getClientHandlers().get(clientOwner).sendPacket(auctionCanceledPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //Remove this auction from all clients registered in this auction
                for (int i = 0; i < clientList.size(); i++) {
                    if (clientList.get(i).getRegisteredAuctions().contains(id)) {
                        clientList.remove(i);
                    }
                }

                //remove the auction from the server
                server.getAuctions().remove(id);
            } else {
                throw new AuctionActiveException("Bid has already been made in this auction, action not permitted.");
            }
        } else {
            throw new ServerNotClientOwnerException("The client is not the owner of the auction");
        }
    }


    /*
        Precondition: None
        Postcondition: Method concludes the auction and notifies the winner of the auction on their success
        through a sent packet.
        All of the registered clients in the clientList variable are informed on the auctions conclusion
        through a sent packet, this applies also for if there are no winners.
        All clients are unregistered from the auction and the auction removes itself from the server.
        Method returns nothing.
    */
    public void conclude() {

        Server server = Server.getInstance();
        PacketMessage noAuctionWinnerPacket;
        boolean foundWinner = false;

        //Check if any bids have been made
        if (!bidList.isEmpty()) {
            //Iterate through all bids made
            for (int i = 0; i < bidList.size(); i++) {
                //Check if the client who made the bid still has a connection to the server
                if (server.getClientHandlers().containsKey(bidList.get(i).getBidderIP())) {

                    foundWinner = true;
                    //Create payloads and packets to inform the winner and participants of the auction for the conclusion
                    ConcludeAuctionPayload concludePayload = new ConcludeAuctionPayload(id, bidList.get(i).getBid(), item.getName(), bidList.get(i).getBidderIP());
                    PacketMessage concludeAuctionPacket = new PacketMessage(AUCTION_CONCLUDED, concludePayload);
                    NotifyAuctionWinnerPayload notifyWinnerPayload = new NotifyAuctionWinnerPayload(id, bidList.get(i).getBid(), item.getName());
                    PacketMessage notifyWinnerPacket = new PacketMessage(NOTIFY_AUCTION_WINNER, notifyWinnerPayload);

                    //Check if the auction owner is still connected and send a packet
                    if (server.getClientHandlers().containsKey(clientOwner)) {
                        try {
                            server.getClientHandlers().get(clientOwner).sendPacket(concludeAuctionPacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //Reduce the auctions winners number of high bids by one
                    server.getClientHandlers().get(bidList.get(i).getBidderIP()).getClient().lostHighBid();

                    //Send a packet to the winner indicating they have won
                    try {
                        server.getClientHandlers().get(bidList.get(i).getBidderIP()).sendPacket(notifyWinnerPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Send packet message to all participants of the winner and conclusion of the auction
                    server.sendPackets(clientList, concludeAuctionPacket);
                    break;
                }
            }
            if (!foundWinner) {
                // None of the clients can be contacted that made bids,
                // all registered participants are notified of the lack of a winner
                noAuctionWinnerPacket = new PacketMessage(NOTIFY_NO_AUCTION_WINNER, new NotifyNoAuctionWinnerPayload(id, item.getName(), item.getStartingPrice()));

                //Check if the auction owner is still connected and send a packet
                if (server.getClientHandlers().containsKey(clientOwner)) {
                    try {
                        server.getClientHandlers().get(clientOwner).sendPacket(noAuctionWinnerPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //Send a packet to participants of the auction for the lack of a winner of the auction
                server.sendPackets(clientList, noAuctionWinnerPacket);
            }
        } else {

            noAuctionWinnerPacket = new PacketMessage(NOTIFY_NO_AUCTION_WINNER, new NotifyNoAuctionWinnerPayload(id, item.getName(), item.getStartingPrice()));

            //Check if the auction owner is still connected and send a packet
            if (server.getClientHandlers().containsKey(clientOwner)) {
                try {
                    server.getClientHandlers().get(clientOwner).sendPacket(noAuctionWinnerPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Send a packet to participants of the auction for the lack of a winner of the auction
            server.sendPackets(clientList, noAuctionWinnerPacket);
        }

        //Remove this auction from all clients registered auctions list
        for (int i = 0; i < clientList.size(); i++) {
            //Remove this auction from the clients list of registered auctions
            int auctionIndex = clientList.get(i).getRegisteredAuctions().indexOf(id);
            if (auctionIndex != -1) {
                clientList.get(i).getRegisteredAuctions().remove(auctionIndex);
            }
        }

        //Remove the auction from the server
        server.getAuctions().remove(id);
    }

    /*
       Precondition: None
       Postcondition: Method returns a Bid object that has the highest bid placed in the auction.
       If no bids are found a generic empty and null valued Bid object is returned: Bid(null, 0, null).
   */
    public Bid findHighestBid() {
        //Check if there are any bids in the auction
        if (!bidList.isEmpty()) {
            return this.getBidList().getFirst();
        } else {
            return new Bid(null, 0, null);
        }
    }

    /*
       Precondition: None
       Postcondition: Method returns a float variable that is either that starting price of the item object of this class
       or the highest bid value if one exists.
   */
    public float findHighestItemPrice() {

        float highestBid = 0;

        if (!bidList.isEmpty()) {
            highestBid = bidList.getFirst().getBid();
        }

        return highestBid;
    }

    @Override
    public String toString() {
        return "ServerAuction{" +
                "id=" + id +
                ", clientOwner=" + clientOwner +
                ", createdAt=" + createdAt +
                ", terminateAt=" + terminateAt +
                ", isInCountDown=" + isInCountDown +
                ", type='" + type + '\'' +
                ", timer=" + timer +
                ", clientList=" + clientList +
                ", bidList=" + bidList +
                ", item=" + item +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Auction auction = (Auction) o;
        return isInCountDown == auction.isInCountDown &&
                Objects.equals(id, auction.id) &&
                Objects.equals(clientOwner, auction.clientOwner) &&
                Objects.equals(createdAt, auction.createdAt) &&
                Objects.equals(terminateAt, auction.terminateAt) &&
                Objects.equals(type, auction.type) &&
                Objects.equals(timer, auction.timer) &&
                Objects.equals(clientList, auction.clientList) &&
                Objects.equals(bidList, auction.bidList) &&
                Objects.equals(item, auction.item);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(id, clientOwner, createdAt, terminateAt, isInCountDown, type, timer, clientList,
                        bidList, item);
    }
}