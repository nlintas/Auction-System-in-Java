
Aims:
  - Design an application layer network protocol
  - Design and implement a client-server program
  - Have practical insight in the implementation of networking protocols
  - Research and independently learn concurrency issues for client/server programs 
  - Identify appropriate communication protocol at the transport level
  - Break down a large problem into simple manageable modules
  - Work within a small development team effectively.

Requirements:
* Client *
  - connect to the server: this functionality just allows a client to connect to the auction system server. Upon successful connection, the client receives a welcome message from the server. The client’s IP address is used for client identification.
  - advertise an item for auction: the client can place an item for sale by specifying the item name, item description, item starting price and type of closing the auction (explained later). An auction id is generated for the item in sale. Note that a client may register more than one item for sale.
  - list active auctions: a client can query the auction server in order to determine which are the auctions that are currently active. The server responds with a list of items for each auction: the auction id, the item’s name, the item’s description, the starting price, the highest bid, and the IP address of the person who is selling the item.
  - register in an auction: a client can register to participate in an active auction, by specifying the auction id. Note, that one client can register and participate in more than one auctions.
  - place a bid: a client can place a bid for an item of a particular auction. The client needs to specify the auction id and the bid which of course has to be higher than the current highest bid for the item. The (server) time the bid was placed is also saved. The client cannot place a bid for an item unless he has registered in the specific auction.
  - check highest bid: a client may query the auction server to determine the highest bid and its server time for a specific auction by specifying the auction id.
  - withdraw from an auction: a client may withdraw from an auction that he has registered as long as he is not the highest bidder.
  - disconnect from server: a client can disconnect from the auction server as long as he is not the highest bidder of any registered auction. He receives a ‘goodbye’ message from the server, if the disconnection is successful.

* Auctions *
  Closing the auction:

    1. the auction will run for a specified amount of time set by the client. The participant, who has the highest bid when this time expires, gets the item.

    2. the auction runs as long as bids are placed. When a bid is placed, the server starts a timer. If the timer expires and nobody made a new bid, then the server sends a message to the participants “Last bid for item X was price Y: going once”. Then, after 5 seconds, it does the same again with a “... going twice” message. Finally, after 5 seconds it sends a message “Item X Sold for price Y to participant Z (IP address)” to all registered clients of the specific auction (including the client who initiated the auction). Of course, the timer resets, if someone during this period makes a bid.

    Notice that both ways, require from the client to set a timer.

* Server *
  - accept a connection: upon request for a connection by a user, it creates the connection and sends a welcome message to the client.
  - respond to bids: when a client places a bid for an item of a particular auction, the server notifies all the auction participants about the new bid by sending the auction id, item name, the placed bid and the IP address of the bidder.
  - close auction (item sold): when an auction finishes (in either of the two ways), the server sends to all registered participants the auction id, item name, the price that the item was sold, and the IP address of the client that ‘bought’ the item. Then it closes the current auction; no more bids can be placed.
  - disconnect a client: the server disconnects a client from the auction system, if the client is not the highest bidder in any active auction. If he is, an appropriate error message is sent to the client. However, If the client is not the highest bidder in any auction, the server removes him from any auctions that he has registered, sends a ‘goodbye’ message and closes the connection.

* Error Control *
Error checking is important for the system. There are actually quite a few things that can go wrong. The server must send appropriate error messages to client(s) if anything goes wrong.

* Protocol Design *
You should design and document an application protocol that meets the specifications described in the previous section. Of course, there is room for assumptions and creativity. Part of this design includes the syntax and semantics of the data that will be exchanged which means that you have to efficiently construct the format of the packets.

Furthermore, you will have to compare, contrast, choose and thoroughly justify the transport layer communication mechanism under which the system will operate. The choice of the connection-oriented protocol (TCP) or the connectionless protocol (UDP) is based on the nature of the application and the specification of the protocol.

## Steps to get the project working
1. Install IntelliJ Idea Community Version
2. Use the **openJDK 13** from Idea
3. File->New->Project->From Version Control. Add the link to this repo and clone
4. Edit Configurations and add **--module-path "C:\Users\reste\Downloads\openjfx-11.0.2_windows-x64_bin-sdk\javafx-sdk-11.0.2\lib" --add-modules javafx.controls,javafx.fxml**
  The path should be the path to your javafx lib folder
5. The javaFx library is inside the project and can be used to run the program. 
6. Everything should compile and run the main class
7. Use VSC for any rebasing and new commits

# Additional Requirements
* JUnit5
* Maven
* JDK >8 openjfx 13 is preferred
* Java SE 8
* Dagger 1 - 2 (optional)


# Running the ClientApp
* The client app requires 2 parameters that are the **Server IP** and the **Port**

 **Note**
(The port on the client side and server side should be the same.)

# Running the ServerApp
* The serverApp requires 1 parameter that needs to be a number. The parameter represents the **PORT**
