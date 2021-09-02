//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server;

import CommonClasses.Auction;
import CommonClasses.Bid;
import CommonClasses.Item;
import Packets.PacketMessage;
import Payload.AuctionListItem;
import Payload.ErrorMessagePayload;
import Payload.SendAuctionPayload;
import Server.AuctionException.*;
import Server.ServerException.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Packets.MessageType.*;

public class Server {

    // Attributes
    private static ExecutorService pool = Executors.newFixedThreadPool(4);
    // Server Socket instance that will listen for TCP connection requests
    private static ServerSocket serverSocket;
    // HashMap of running ClientHandler threads identified by String host address as the key
    private static Map<String, ClientHandler> clientHandlers;
    // HashMap of Auctions identified by an Integer object which is the auctions id variable as the key
    private static Map<Integer, Auction> auctions;
    // Server Singleton instance
    private static Server server;
    // Boolean that dictates if new created auctions are being accepted or not from clients. Used by the ClientHandler class
    private static boolean isAcceptingAuctions;
    // Boolean that will control the listening loop of the server for new connections
    private static boolean isListening;

    // Constructors
    private Server() {
        super();
        try {
            serverSocket = new ServerSocket(9090);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientHandlers = new HashMap<String, ClientHandler>();
        auctions = new HashMap<Integer, Auction>();
        isListening = false;
        isAcceptingAuctions = false;
    }

    private Server(int port) {
        super();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientHandlers = new HashMap<String, ClientHandler>();
        auctions = new HashMap<Integer, Auction>();
        isListening = false;
        isAcceptingAuctions = false;
    }

    //Getters and Setters
    public ExecutorService getPool() {
        return pool;
    }

    public void setPool(ExecutorService pool) {
        Server.pool = pool;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        Server.serverSocket = serverSocket;
    }

    public boolean isListening() {
        return isListening;
    }

    public void setListening(boolean listening) {
        isListening = listening;
    }

    public Map<String, ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public void setClientHandlers(Map<String, ClientHandler> clientHandlers) {
        clientHandlers = clientHandlers;
    }

    public Map<Integer, Auction> getAuctions() {
        return auctions;
    }

    private static Server getServer() {
        return server;
    }

    private static void setServer(Server server) {
        server = server;
    }

    public void setAuctions(Map<Integer, Auction> auctions) {
        this.auctions = auctions;
    }

    public boolean isAcceptingAuctions() {
        return isAcceptingAuctions;
    }

    public void setAcceptingAuctions(boolean acceptingAuctions) {
        this.isAcceptingAuctions = acceptingAuctions;
    }

    //Methods

    /*
        Precondition: None
        Postcondition: Method returns a singleton server object. If the server object does not exist
        a new instance is created with the default constructor.
    */
    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    /*
        Precondition: Method receives a primitive int value named port.
        Postcondition: Method returns a singleton server object. If the server object does not exist
        a new instance is created with the constructor that accepts the provided "port" parameter.
    */

    public static Server getInstance(int port) {
        if (server == null) {
            server = new Server(port);
        }
        return server;
    }

    /*
        Precondition: None
        Postcondition: Method causes the singleton server instance to run in an infinite loop listening
        for TCP connection requests to establish. Each new active socket generated is placed in a new Client object
        constructed which is then placed in a ClientHandler thread instance. This new ClientHandler instance is then
        placed into the clientHandlers variable with the key being the prior made active socket InetAddress hostAddress value
        (as a String).
        Method returns nothing.
    */
    public void listen() throws IOException {

        isAcceptingAuctions = true;
        isListening = true;

        //Have the server listen for packets indefinitely until the isListening variable is false
        while (isListening) {
            //Accept new client connections
            Socket clientSocket = serverSocket.accept();
            //Create a new client instance
            Client client = new Client(clientSocket);
            //Create a new ClientHandler thread with the recently created Client object
            ClientHandler clientThread = new ClientHandler(client);
            //Place the clientThread variable into the clientHandlers hashmap and execute it
            clientHandlers.put(client.getSocket().getInetAddress().getHostAddress(), clientThread);
            pool.execute(clientThread);
        }

    }

    /*
        Precondition: Method expects to receive a Client object that is participating in an active connection
        (a ClientHandler thread in the clientHandlers variable).
        Postcondition: Method will remove the the provided Client object from the clientHandlers data structure.
        NOTE: An exception is thrown if the client to be removed has the highest bid in 1 or more auctions.
        In this event the client is not removed.
        Method returns nothing.
        NOTE:
        If the provided Client has the highest bid in an auction ServerHasHighBidException is thrown.
        If the socket connection can not be closed a IOException is thrown.
        If the provided Client does not currently have an active connection (which is identifiable
        though the clientHandlers data structure) a ServerClientHandlerDoesNotExistException is thrown.
    */
    public void removeClient(Client client)
            throws IOException, ServerClientHandlerDoesNotExistException, ServerHasHighBidException {

        String clientIP = client.getSocketAddress().getAddress().getHostAddress();

        //Check if the client exists
        if (clientHandlers.containsKey(clientIP)) {
            //Stop the execution of the client handler thread
            clientHandlers.get(clientIP).stopRunning();
        } else {
            throw new ServerClientHandlerDoesNotExistException("The client handler does not exists");
        }
    }

    /*
        Precondition: Method expects to receive an int parameter as identification
        for an existing Auction in the auctions data structure. A Client instance is also expected to be received
        that should be registered to the auction.
        Postcondition: The provided Client object is registered into the specific auction based on the auctionID parameter.
        In the event the Client is already registered nothing happens. In both cases a packet is sent to the Client
        with information about the specific auction.
        Method returns nothing.
        NOTE:
        If the auction requested based on the provided auctionID parameter is missing
        a ServerNoAuctionException is thrown.
    */

    public void joinAuction(int auctionID, Client client)
            throws ServerNoAuctionException {

        //Check if auction exists in the hashmap
        if (auctions.containsKey(auctionID)) {

            //Get auction from auctions variable
            Auction auction = auctions.get(auctionID);

            //register the client into the specific auction if possible
            try {
                auction.addClient(client);
            } catch (AuctionClientIsOwnerException | AuctionAlreadyRegisteredException e) {

            }

            //Get the latest bid if any
            Bid latestBid = auction.findHighestBid();

            //Prepare the payload to be sent
            SendAuctionPayload auctionPayload = new SendAuctionPayload(auctionID, auction.getType(),
                    auction.getCreatedAt(), auction.getTerminateAt(), latestBid.getCreatedAt(), latestBid.getBid(),
                    auction.getItem().
                            getStartingPrice(), auction.getItem().getName(), auction.getItem().getDescription(),
                    auction.getClientOwner(), latestBid.getBidderIP());

            //Send the client the specific auction details
            try {
                sendPacket(client, new PacketMessage(SEND_AUCTION, auctionPayload));
            } catch (IOException e) {
                try {
                    sendPacket(client,
                            new PacketMessage(ERROR, new ErrorMessagePayload("Unable to send auction details")));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } else {
            throw new ServerNoAuctionException("Auction doesn't exist. Action not permitted.");
        }
    }

    /*
        Precondition: Method expects to receive a primitive integer parameter as identification for an
        existing Auction in the auctions data structure. A Client instance is also expected to be received
        that is already registered into the auction.
        Postcondition: Method causes a specific Auction instance based on the provided primitive integer
        to remove a participating Client based on the provided Client parameter.
        Method returns nothing.
        NOTE:
        If the the auction could not be identified based on the provided int auctionID a ServerNoAuctionException is thrown.
        If the provided Client possesses the highest bid in the requested auction a AuctionHighBidException is thrown.
        If the provided Client is not registered in the specified auction instance a AuctionNotRegisteredException is thrown.
    */

    public void leaveAuction(int auctionID, Client client)
            throws ServerNoAuctionException, AuctionHighBidException, AuctionNotRegisteredException {

        //Check if the auction exists
        if (auctions.containsKey(auctionID)) {
            //Unregister the provided client from the auction
            auctions.get(auctionID).removeClient(client);
        } else {
            throw new ServerNoAuctionException("Auction doesn't exist. Action not permitted.");
        }
    }

    /*
        Precondition: Method expects to receive an Auction object.
        Postcondition: Method inserts the provided Auction parameter into the auctions data structure.
        Method returns nothing
    */
    public void addAuction(Auction auction) {
        //Insert new auction into auctions HashMap
        auctions.put(auction.getId(), auction);
    }

    /*
        Precondition: Method expects to receive a int parameter to be used as an identifier for an auction in the auctions variable.
        The last parameter expected to be received is a Client object that wishes to cancel the auction.
        Postcondition: Method will cancel an existing auction only if the provided Client object is the owner of the auction
        and no bids have ever been made.
        Method returns nothing.
        NOTE:
        If the provided Client is not the owner of the specified auction a ServerNotClientOwnerException is thrown.
        If the specified auction could not be found a ServerNoAuctionException is thrown
        If the specified auction is active (possesses bids) a AuctionActiveException is thrown
    */

    public void cancelAuction(int auctionID, Client client)
            throws ServerNotClientOwnerException, ServerNoAuctionException, AuctionActiveException {
        Auction auction;
        //Check if auction exists in the hashmap
        if (auctions.containsKey(auctionID)) {
            //get specific auction
            auction = auctions.get(auctionID);
            //Cancel the auction
            auction.cancel(client);
        } else {
            throw new ServerNoAuctionException("Auction doesn't exist. Action not permitted.");
        }
    }

    /*
        Precondition: Method expects to receive a int that identifies an existing auction from the auctions variable,
        a Bid object for a bid to be made and a Client object to indicate the client making the bid to the specified auction.
        Postcondition: The method will invoke the specified auction to add a bid with the provided Bid object
        and Client object.
        Method returns nothing
        NOTE:
        If the provided Bid object is lower than the auctions highest bid a AuctionLowBidException is thrown.
        If the provided Client parameter is not registered is the specified auction a AuctionNotRegisteredException is thrown.
        If the provided Client parameter is the owner of the auction a AuctionClientIsOwnerException is thrown.
    */

    public void auctionBid(int auctionID, Bid bid, Client client)
            throws AuctionLowBidException, AuctionNotRegisteredException, AuctionClientIsOwnerException, ServerNoAuctionException {
        if(auctions.containsKey(auctionID)){
            auctions.get(auctionID).addBid(bid, client);
        }
        else{
            throw new ServerNoAuctionException("Auction doesn't exist. Action not permitted.");
        }

    }

    /*
        Precondition: Method expects to receive a int that identifies an existing auction in the auctions variable.
        Postcondition: Method will return a Bid object from the auction specified that is currently the highest bid.
        NOTE:
        If the specified auction does not exist a ServerNoAuctionException is thrown
    */

    public Bid getHighestBid(int auctionID) throws ServerNoAuctionException {
        //Attribute initialisation
        Auction auction;
        //Check if the auction exists
        if (auctions.containsKey(auctionID)) {
            //get specific auction
            auction = auctions.get(auctionID);
            //Ask the auction for the highest bid
            return auction.findHighestBid();
        } else {
            throw new ServerNoAuctionException("Auction doesn't exist. Action not permitted.");
        }
    }

    /*
        Precondition: None
        Postcondition: Method returns a LinkedList of AuctionListItem. The LinkedList is filled with new AuctionListItems
        based on all existing auctions that exist in the auctions variable.
    */
    public LinkedList<AuctionListItem> getAllAuctions() {

        LinkedList<AuctionListItem> auctionsItemsPayload = new LinkedList<>();

        //Iterate through all of the auctions currently active
        for (Auction auction : auctions.values()) {

            Item auctionItem = auction.getItem();
            //Find the auctions current highest bid if any
            float highestBidPrice = auction.findHighestItemPrice();

            auctionsItemsPayload.add(new AuctionListItem(auction.getId(), auctionItem.getStartingPrice(),
                    auctionItem.getName(), auctionItem.getDescription(), auction.getClientOwner(),
                    highestBidPrice));
        }

        return auctionsItemsPayload;
    }

    /*
        Precondition: Method expects to receive a Client Object and PacketMessage object.
        Postcondition: Method sends the provided PacketMessage packet to the appropriate client based
        on the Client object. The packet is sent through the appropriate ClientHandler instance with
        the provided Client object as a key to the clientHandlers data structure.
        Method returns nothing.
        NOTE:
        If the packet is failed to be sent through the socket a IOException is thrown.
    */

    public void sendPacket(Client client, PacketMessage packet)
            throws IOException {
        //Check if the client connection exists
        if (clientHandlers.containsKey(client.getSocket().getInetAddress().getHostAddress())) {
            clientHandlers.get(client.getSocketAddress().getAddress().getHostAddress()).sendPacket(packet);
        }
    }

    /*
        Precondition: Method expects to receive a LinkedList of type Client and a PacketMessage object.
        Postcondition: Method sends the provided PacketMessage packet to all Client objects provided in the
        LinkedList clients parameter. Packets are sent to each Client by invoking the sendPackets method
        Method returns nothing.
    */

    public void sendPackets(LinkedList<Client> clients, PacketMessage packet) {
        //Loop through all clients
        for (Client client : clients) {
            try {
                //Send client the provided packet parameter
                sendPacket(client, packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
        Precondition: Method expects to receive a Client object.
        Postcondition: Method will return a LinkedList of AuctionListItem. The LinkedList is filled with new AuctionListItems
        based on all existing auctions that exist in the auctions variable that were created by the provided Client object parameter.
    */

    public LinkedList<AuctionListItem> getMyAuctions(Client client) {

        LinkedList<AuctionListItem> auctionListItems = new LinkedList<>();
        //Traverse the hashmap to get the auctions
        for (Auction auction : auctions.values()) {

            //Check for if the auction exists in the map and has the specific client as the owner
            if (auction != null && auction.getClientOwner()
                    .equals(client.getSocketAddress().getAddress().getHostAddress())) {

                float highestBidPrice = auction.findHighestItemPrice();
                Item auctionItem = auction.getItem();

                //Assemble the auction item
                AuctionListItem listItem = new AuctionListItem(auction.getId(),
                        auctionItem.getStartingPrice(),
                        auctionItem.getName(), auctionItem.getDescription(), auction.getClientOwner(),
                        highestBidPrice);
                //Add the created AuctionListItem to the temporary auctionListItem data structure
                auctionListItems.add(listItem);
            }
        }
        return auctionListItems;
    }
}