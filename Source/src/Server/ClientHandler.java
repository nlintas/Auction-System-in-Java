//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server;

import CommonClasses.Auction;
import CommonClasses.Bid;
import CommonClasses.Item;
import Packets.MessageType;
import Packets.PacketMessage;

import Payload.*;
import Server.AuctionException.*;
import Server.ServerException.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.LinkedList;

import static Packets.MessageType.*;

@SuppressWarnings("unused")
public class ClientHandler extends Thread {

    //Attributes
    private Client client;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private boolean isRunning;

    //Constructor
    public ClientHandler(Client serverClient) {
        this.client = serverClient;
        this.isRunning = true;
    }

    //Getters and Setters
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void run() {
        //Initialise variables and get server instance
        Server server = Server.getInstance();
        String packetMessageJson = null;
        MessageType packetType = null;
        PacketMessage inputPacketMessage = null;
        isRunning = true;

        try {
            objectInputStream = new ObjectInputStream(client.getSocket().getInputStream());
            objectOutputStream = new ObjectOutputStream(client.getSocket().getOutputStream());
            sendPacket((new PacketMessage(WELCOME_MESSAGE, null)));
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }

        while (isRunning) {
            try {
                PacketMessage packetMessage = (PacketMessage) objectInputStream.readObject();

                switch (packetMessage.getType()) {

                    case REGISTER_IN_AUCTION:
                        //Server received packet indicating the client wishes to register into an auction
                        try {
                            joinAuction(packetMessage);
                        } catch (AuctionAlreadyRegisteredException e) {
                            e.printStackTrace();
                        } catch (ServerNoAuctionException | ServerUnexpectedPayloadException  e) {
                            sendPacket(new PacketMessage(ERROR, new ErrorMessagePayload(e.getMessage())));
                        }
                        break;

                    case CANCEL_AUCTION:
                        try {
                            cancelAuction(packetMessage);
                        } catch (ServerNoAuctionException | ServerNotClientOwnerException | AuctionException | ServerUnexpectedPayloadException e) {

                            sendPacket(new PacketMessage(ERROR, new ErrorMessagePayload(e.getMessage())));
                        }
                        break;

                    case REQUEST_ACTIVE_AUCTION_LIST:
                        //Server received packet indicating the client wishes to receive a list of active auctions
                        sendAllAuctions();
                        break;

                    case UNREGISTER_FROM_AUCTION:
                        //Server received a packet indicating the client wishes to unregister from a specific auction
                        try {
                            leaveAuction(packetMessage);
                        } catch (ServerUnexpectedPayloadException | AuctionHighBidException | AuctionNotRegisteredException | ServerNoAuctionException | AuctionClientIsOwnerException e) {
                            sendPacket(new PacketMessage(ERROR, new ErrorMessagePayload(e.getMessage())));
                        }
                        break;

                    case DISCONNECT:
                        //Server received a packet indicating the client wishes to disconnect from the server
                        try {
                            disconnectFromServer();
                        } catch (ServerHasHighBidException | AuctionHighBidException e) {
                            sendPacket(new PacketMessage(ERROR, new ErrorMessagePayload(e.getMessage())));
                        }
                        break;

                    case REQUEST_HIGHEST_BID:
                        //Server received a packet indicating the client requested the highest bid in an auction
                        try {
                            requestHighestBid(packetMessage);
                        } catch (ServerNoAuctionException | ServerUnexpectedPayloadException e) {
                            sendPacket(new PacketMessage(ERROR, new ErrorMessagePayload(e.getMessage())));
                        }
                        break;

                    case CREATE_AUCTION:
                        //Server received a packet indicating the client wishes to create a new auction
                        //Check if the server wishes to accept the creation of new auctions
                        if(Server.getInstance().isAcceptingAuctions()){
                            try {
                                createAuction(packetMessage);
                            } catch (ServerUnexpectedPayloadException e) {
                                sendPacket(new PacketMessage(ERROR, new ErrorMessagePayload(e.getMessage())));
                            }
                        }else{
                            sendPacket(new PacketMessage(ERROR, new ErrorMessagePayload("Server is not accepting auctions at this time")));
                        }
                        break;

                    case MAKE_BID:
                        //Server received a packet indicating the client wishes to make a bid in an auction
                        try {
                            makeBid(packetMessage);
                        } catch (ServerUnexpectedPayloadException | AuctionLowBidException | AuctionClientIsOwnerException | AuctionNotRegisteredException | ServerNoAuctionException e) {
                            sendPacket(new PacketMessage(ERROR, new ErrorMessagePayload(e.getMessage())));
                        }
                        break;

                    case REQUEST_MY_AUCTIONS:
                        //Client requested their auctions list
                        sendMyAuctions(packetMessage);
                        break;

                    default:
                        break;
                }

            } catch (IOException e) {
                try {
                    client.getSocket().close();
                    isRunning = false;
                    server.getClientHandlers().remove(client.getSocket().getInetAddress().getHostAddress());
                    for(int auctionID: client.getRegisteredAuctions()){
                        if(server.getAuctions().containsKey(auctionID)){
                            try {
                                server.getAuctions().get(auctionID).forcefullyRemoveClient(client);
                            } catch (AuctionNotRegisteredException auctionNotRegisteredException) {
                                auctionNotRegisteredException.printStackTrace();
                            }
                        }
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //Preconditions: The method receives a packet message and then attempts to send a packet
    //Postconditions: The packet is sent successfully to its intended destination
    public void sendPacket(PacketMessage packetMessage) throws IOException {
        objectOutputStream.writeObject(packetMessage);
    }

    //Preconditions: The packet has been received indicating the request of a client to join an auction
    //Postconditions: The auction is joined by the client
    //NOTE:The ServerUnexpectedPayloadException is thrown when the packet received contains the wrong type of payload
    //AuctionAlreadyRegisteredException is thrown when the client attempts to register in the given auction after already being registered
    //ServerNoAuctionException is thrown when the auction no longer exists and an attempt to register in it is made
    //IOException is thrown when the send packet method fails in the auction method
    public void joinAuction(PacketMessage packetMessage)
        throws AuctionAlreadyRegisteredException, ServerNoAuctionException, ServerUnexpectedPayloadException, IOException {
        //Check if the received packet contains the correct payload
        if (packetMessage.getPayload() instanceof RegisterClientPayload) {
            //Create a temporary server instance and packet store
            Server server = Server.getInstance();
            RegisterClientPayload clientRegisterPayload = (RegisterClientPayload) packetMessage
                    .getPayload();
            //Have the server add this clientHandler "client" object to the specific provided auction ID
            server.joinAuction(clientRegisterPayload.getAuctionID(), client);
        } else {
            throw new ServerUnexpectedPayloadException("Packet provided the wrong payload");
        }
    }

    //Preconditions: The packet has been received indicating the request of a client to view all active auctions
    //Postconditions: Auction list is successfully displayed the client
    public void sendAllAuctions() {
        //Create a temporary server instance
        Server server = Server.getInstance();
        try {
            //Get and send the client all of the currently active auctions
            LinkedList<AuctionListItem> auctionListItemAuctionListPayload = server.getAllAuctions();
            server.sendPacket(client, new PacketMessage(SEND_ACTIVE_AUCTION_LIST, new AuctionListPayload(auctionListItemAuctionListPayload)));
        } catch (IOException e) {
            try {
                server.sendPacket(client, new PacketMessage(ERROR, new ErrorMessagePayload("Unable to send all active auctions")));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    //Preconditions: Client needs to be removed
    //Postconditions: Client is successfully removed from the system
    //NOTE:The AuctionHighBidException is thrown when the user attempted to be removed has the highest bid in any auction
    //The IOException is thrown if the socket method fails to close
    public void stopRunning() throws ServerHasHighBidException, IOException {
        //Check if client has made highest bid in an auction/s
        if (client.getNumberOfHighBids() <= 0) {
            //Stop listening for client packet messages
            isRunning = false;
            //close the client socket
            client.getSocket().close();
            //Get the server
            Server server = Server.getInstance();
            //Remove the client handler self instance
            server.getClientHandlers().remove(this);
        } else {
            throw new ServerHasHighBidException("Connection can't be stopped client is highest bid in at least 1 auction. Action not permitted.");
        }
    }

    //Preconditions: The packet has been received indicating the request of a client to unregister from an auction
    //Postconditions: The client is successfully removed from the auction
    //NOTE:The AuctionHighBidException is thrown when the user attempted to be removed has the highest bid in any auction
    //The ServerUnexpectedPayloadException is thrown when the packet received contains the wrong type of payload
    //AuctionNotRegisteredException is thrown when the client is not registered in the specific auction
    //ServerNoAuctionException is thrown when the auction that the client is attempting leave no longer exists
    //AuctionClientIsOwnerException is thrown when the client that asked to unregister from the specific auction is also the owner of the auction
    public void leaveAuction(PacketMessage packetMessage) throws ServerUnexpectedPayloadException, AuctionHighBidException, AuctionNotRegisteredException, ServerNoAuctionException, AuctionClientIsOwnerException {
        //Check if the received packet contains the correct payload
        if (packetMessage.getPayload() instanceof UnregisterClientPayload) {
            //Temporarily store the payload and server instance
            UnregisterClientPayload unregisterPayload = (UnregisterClientPayload) packetMessage.getPayload();
            Server server = Server.getInstance();
            //Call the auction to handle the rest of the removal operation
            server.leaveAuction(unregisterPayload.getAuctionID(), client);
        } else {
            throw new ServerUnexpectedPayloadException("Packet provided the wrong payload");
        }
    }

    //Preconditions: Client needs to be disconnected from the server
    //Postconditions: Client is successfully removed from the server
    //NOTE:The AuctionHighBidException is thrown when the user attempted to be removed has the highest bid in any auction by the leave auction method
    //The ServerHasHighBidException is thrown when the server finds at least one auction where the client has the highest bid and an attempt to disconnect is made
    public void disconnectFromServer() throws ServerHasHighBidException, AuctionHighBidException {
        //Temporarily grab the server
        Server server = Server.getInstance();
        //Check if the client has 1 or more highest bids in any auctions
        if (client.getNumberOfHighBids() > 0) {
            String errorMessage;
            if (client.getNumberOfHighBids() > 1) {
                errorMessage = "Client has high bids in " + client.getNumberOfHighBids() + " live auctions";
            } else {
                errorMessage = "Client has high bids in one live auction";
            }
            throw new ServerHasHighBidException(errorMessage);
        } else {
            //Unregister the client from all auctions they are participating in
            for (int auction : client.getRegisteredAuctions()) {
                try {
                    server.leaveAuction(auction, client);
                } catch (ServerNoAuctionException | AuctionNotRegisteredException e) {
                    e.printStackTrace();
                }
            }
            //Attempt to remove the client and close their socket connection
            try {
                server.removeClient(client);
                client.getSocket().close();
            } catch (IOException | ServerClientHandlerDoesNotExistException | ServerHasHighBidException e) {
                e.printStackTrace();
            }
        }
    }

    //Preconditions: The packet has been received indicating the request of a client to find the highest bid in an auction
    //Postconditions: The highest bid of an auction is found and used for the client 'respond to bids' refresh button
    //NOTE: The ServerNoAuctionException is thrown when the auction trying to be accessed for its highest bid no longer exists
    //The ServerUnexpectedPayloadException is thrown when the packet received contains the wrong type of payload
    public void requestHighestBid(PacketMessage packetMessage) throws ServerNoAuctionException, ServerUnexpectedPayloadException {
        //Check if the appropriate message is received
        if (packetMessage.getPayload() instanceof RequestHighestBidPayload) {
            //Temporarily store server, highest bid, auctionID and payload
            Server server = Server.getInstance();
            RequestHighestBidPayload sendHighestBidPayload = (RequestHighestBidPayload) packetMessage.getPayload();
            int auctionID = sendHighestBidPayload.getAuctionID();
            Bid highestBid = server.getHighestBid(auctionID);
            //Attempt tp send the packet to client
            PacketMessage outputPacketMessage = new PacketMessage(SEND_HIGHEST_BID,
                    new SendHighestBidPayload(highestBid.getCreatedAt(), highestBid.getBid(), highestBid.getBidderIP(), auctionID));
            try {
                sendPacket(outputPacketMessage);
            } catch (IOException e) {
                try {
                    server.sendPacket(client, new PacketMessage(ERROR, new ErrorMessagePayload("Highest bid request could not be performed.")));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } else {
            throw new ServerUnexpectedPayloadException("Packet provided the wrong payload");
        }
    }

    //Preconditions: The packet has been received indicating the request of a client to create an auction
    //Postconditions: The client's auction has been created with the details provided in the packet message
    //NOTE: The ServerUnexpectedPayloadException is thrown when the packet received contains the wrong type of payload
    public void createAuction(PacketMessage packetMessage) throws ServerUnexpectedPayloadException {
        //Check if the received packet contains the correct payload
        if (packetMessage.getPayload() instanceof CreateAuctionPayload) {
            //Temporarily store the server instance, item, current date, new auction created and the received message
            Server server = Server.getInstance();
            CreateAuctionPayload createAuctionPayload = (CreateAuctionPayload) packetMessage.getPayload();
            //Assemble the new auction, Item and date (timestamp)
            Item item = new Item(createAuctionPayload.getItemStartingPrice(), createAuctionPayload.getItemName(), createAuctionPayload.getItemDescription());
            Date currentDate = new Date();
            Auction newAuction = new Auction(client.getSocketAddress().getAddress().getHostAddress(), new Date(currentDate.getTime() + (createAuctionPayload.getAuctionDuration() * (60 * 1000))), createAuctionPayload.getAuctionType(), item);
            //Contact the server to process the add auction request
            server.addAuction(newAuction);
            //Attempt to send the packet to the client
            try {
                sendPacket(new PacketMessage(SEND_AUCTION_ID, new SendAuctionIDPayload(newAuction.getId())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new ServerUnexpectedPayloadException("Packet provided the wrong payload");
        }
    }

    //Preconditions: The packet has been received indicating the request of a client to make a bid on an auction
    //Postconditions: The client successfully makes a bid on the appointed auction
    //NOTE: The ServerUnexpectedPayloadException is thrown when the packet received contains the wrong type of payload
    //AuctionLowBidException is thrown when the bid is lower than the current current highest price in the auction
    //AuctionClientIsOwnerException is thrown when the owner of the auction tries to bid in their own auction
    //AuctionNotRegisteredException is thrown when the client attempting to make a bid is not registered to the given auction
    //IOException is thrown if the nested send packet method (from auctionBid) fails to work
    public void makeBid(PacketMessage packetMessage) throws ServerUnexpectedPayloadException, AuctionLowBidException, AuctionClientIsOwnerException, AuctionNotRegisteredException, ServerNoAuctionException {
        //Check if the received packet contains the correct payload
        if(packetMessage.getPayload() instanceof MakeBidPayload){
            //Temporarily store the server instance and the message received
            Server server = Server.getInstance();
            MakeBidPayload newBidPayload = (MakeBidPayload) packetMessage.getPayload();
            //Assemble the new bid
            Bid newBid = new Bid(new Date(), newBidPayload.getHighestBid(), client.getSocketAddress().getAddress().getHostAddress());
            //Ask the server to do the rest of the operations required
            server.auctionBid(newBidPayload.getAuctionID(), newBid, client);
        } else{
            throw new ServerUnexpectedPayloadException("Packet provided the wrong payload");
        }
    }

    //Preconditions: The packet has been received indicating the request of a client to cancel a created auction
    //Postconditions: The client successfully cancels the specific auction created
    //NOTE: The ServerUnexpectedPayloadException is thrown when the packet received contains the wrong type of payload
    //ServerNoAuctionException is thrown when the auction attempted to be cancelled no longer exists
    //ServerNotClientOwnerException is thrown when the client that tries to cancel the auction is not the owner of the specified auction
    //AuctionActiveException is thrown in order to prevent an auction from being cancelled if any bids have been made already
    public void cancelAuction(PacketMessage packetMessage)
            throws ServerNoAuctionException, ServerNotClientOwnerException, ServerUnexpectedPayloadException, AuctionActiveException {
        //Check if the payload is the expected one
        if (packetMessage.getPayload() instanceof ConfirmAuctionCancellationPayload) {
            //Store the payload and server temporarily
            ConfirmAuctionCancellationPayload cancelAuctionPayload = (ConfirmAuctionCancellationPayload) packetMessage.getPayload();
            Server server = Server.getInstance();
            //Call the server to process the cancel auction request
            server.cancelAuction(cancelAuctionPayload.getAuctionID(), client);
        } else {
            throw new ServerUnexpectedPayloadException("Packet provided the wrong payload");
        }
    }


    //Preconditions: The packet has been received indicating the request of a client to view their own created auctions list
    //Postconditions: The list is sent and successfully displays for the specified client
    public void sendMyAuctions(PacketMessage packetMessage) {
            //Temporarily store the server instance
            Server server = Server.getInstance();
            //Call the server to process the get my auctions request and send the packet to the client
            PacketMessage outputPacketMessage = new PacketMessage(SEND_MY_AUCTIONS,
                    new AuctionListPayload(server.getMyAuctions(client)));
            try {
                sendPacket(outputPacketMessage);
            } catch (IOException e) {
                try {
                    server.sendPacket(client, new PacketMessage(ERROR, new ErrorMessagePayload("My auction list could not be fetched.")));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
    }
}
