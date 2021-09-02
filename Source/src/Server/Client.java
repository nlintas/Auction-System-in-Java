//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Objects;

public class Client {

    //Attributes
    private InetSocketAddress socketAddress;
    private LinkedList<Integer> registeredAuctions;
    private int numberOfHighBids;
    private Socket socket;


    //Constructors
    public Client(Socket socket) {
        this.socketAddress = new InetSocketAddress(socket.getInetAddress(), socket.getPort());
        this.socket = socket;
        this.registeredAuctions = new LinkedList<>();
        numberOfHighBids = 0;
    }

    //Setters and Getters
    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(InetSocketAddress address) {
        this.socketAddress = address;
    }

    public LinkedList<Integer> getRegisteredAuctions() {
        return registeredAuctions;
    }

    public void setRegisteredAuctions(LinkedList<Integer> registeredAuctions) {
        this.registeredAuctions = registeredAuctions;
    }

    public int getNumberOfHighBids() {
        return numberOfHighBids;
    }

    public void setNumberOfHighBids(int numberOfHighBids) {
        this.numberOfHighBids = numberOfHighBids;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    //Methods

    /*
        Precondition: None
        Postcondition: Method simply increments the numberOfHighBids variable by 1
        Method returns nothing
    */
    public void madeHighBid() {
        numberOfHighBids++;
    }

    /*
        Precondition: None
        Postcondition: Method simply decrements the numberOfHighBids variable by 1 only of the value is positive
        Method returns nothing
    */
    public void lostHighBid() {
        if (numberOfHighBids > 0) {
            numberOfHighBids--;
        }
    }

    @Override
    public String toString() {
        return "ServerClient{" +
                "socketAddress=" + socketAddress +
                ", registeredAuctions=" + registeredAuctions +
                ", numberOfHighBids=" + numberOfHighBids +
                ", socket=" + socket +
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
        Client that = (Client) o;
        return numberOfHighBids == that.numberOfHighBids &&
                Objects.equals(socketAddress, that.socketAddress) &&
                Objects.equals(registeredAuctions, that.registeredAuctions) &&
                Objects.equals(socket, that.socket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socketAddress, registeredAuctions, numberOfHighBids, socket);
    }
}
