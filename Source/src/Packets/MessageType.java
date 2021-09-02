//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Packets;

//Enumeration Values
public enum MessageType {
    SEND_AUCTION("Send Auction"),
    SEND_AUCTION_ID("Send New Auction ID"),
    SEND_ACTIVE_AUCTION_LIST("Send Live Auctions List"),
    REQUEST_ACTIVE_AUCTION_LIST("Request Live Auctions List"),
    ERROR("Error Message"),
    COUNTDOWN_ONCE("Counting Once..."),
    COUNTDOWN_TWICE("Counting Twice..."),
    REGISTER_IN_AUCTION("Register Client in Auction"),
    MAKE_BID("Make Auction Bid"),
    REQUEST_HIGHEST_BID("Request Highest Bid from Auction"),
    SEND_HIGHEST_BID("Send Highest Bid for an Auction"),
    UNREGISTER_FROM_AUCTION("Unregister Client from Auction"),
    CANCEL_AUCTION("Cancel Auction"),
    DISCONNECT("Disconnect"),
    AUCTION_UPDATE("Update Auction"),
    CREATE_AUCTION("Create Auction"),
    WELCOME_MESSAGE("Welcome to Server"),
    AUCTION_CONCLUDED("Conclude Auction"),
    NOTIFY_AUCTION_WINNER("Auction Winner"),
    NOTIFY_NO_AUCTION_WINNER("Auction Has No Winners"),
    AUCTION_CANCELLED("Confirm auction has been Canceled"),
    REQUEST_MY_AUCTIONS("Request the Clients Auctions"),
    SEND_MY_AUCTIONS("Send the Clients Auctions"),
    HIGHEST_BID_OWNER_LOST("The Bidder with the Highest Bid in an Auction was Lost in Communication"),
    CONFIRM_AUCTION_REGISTRATION("Client is Registered to Auction");
    private String type;

    MessageType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PacketMessage{" +
                "type='" + type + '\'' +
                '}';
    }
}
