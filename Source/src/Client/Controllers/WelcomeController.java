//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis
package Client.Controllers;

import Client.ClientApp;
import Client.SemaphoreTasks.SemaphoreTask;
import CommonClasses.Auction;
import Packets.MessageType;
import Packets.PacketMessage;
import Server.Client;
import java.awt.JobAttributes;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import java.util.Timer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javax.swing.JOptionPane;

public class WelcomeController implements Initializable {



  @FXML
  private Button newAuction;

  @FXML
  private Button myAuctions;

  @FXML
  private Button liveAuctions;

  @FXML
  private Text title1;

  @FXML
  private TextArea console;

  /**
   * <p>Try and send the message through the serverConnection for live auctions. The method will load the
   * initializer method of the AuctionController class that is used to load the view.
   * </p>
   *
   * @see       AuctionController#initialize(URL, ResourceBundle)
   * @see       FXMLLoader#load()
   * @see        ClientApp#getServerConnection() ServerConnection
   */
  @FXML
  protected void showAuctions(ActionEvent event)
      throws IOException, ClassNotFoundException, InterruptedException {
    ClientApp.getServerConnection()
        .sendPacket(new PacketMessage(MessageType.REQUEST_ACTIVE_AUCTION_LIST, ""));
    //Semaphore
    Timer timer = new Timer();
    timer.schedule(new SemaphoreTask(AuctionController.getLiveAuctionsSemaphore()), 6000);
    try {
      AuctionController.getLiveAuctionsSemaphore().acquire();
      timer.cancel();
      timer = null;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if(AuctionController.getAuctionReceived() != null) {
      Parent root = FXMLLoader.load(getClass().getResource("resources/auctions.fxml"));
      Scene scene = new Scene(root);
      ClientApp.getStage().setScene(scene);
      ClientApp.getStage().show();
    }
    else{
      JOptionPane.showMessageDialog(null,"Error Received");
    }
  }

  /**
   * <p>Try and send the message through the serverConnection to load the view of the auction creation. The
   * view will be loaded by the AuctionCreateController initialize method.
   * </p>
   *
   * @see       CreateAuctionController#initialize(URL, ResourceBundle)
   * @see       FXMLLoader#load()
   * @see        ClientApp#getServerConnection() ServerConnection
   */
  @FXML
  protected void newAuction(ActionEvent event) throws IOException, ClassNotFoundException {
    Parent root = FXMLLoader.load(getClass().getResource("resources/createAuction.fxml"));
    ClientApp.getStage().setTitle("Create Auction");
    Scene scene = new Scene(root);
    ClientApp.getStage().setScene(scene);
    ClientApp.getStage().show();
  }

  /**
   * <p>Try and send the message through the serverConnection to load the view of the my Auctions. This represents
   * a table of all the Auctions that are created by me. The view is loaded by the MyAuctionsController initialize method.
   * </p>
   *
   * @see       MyAuctionsController#initialize(URL, ResourceBundle)
   * @see       FXMLLoader#load()
   * @see        ClientApp#getServerConnection() ServerConnection
   */
  @FXML
  protected void myAuctions(ActionEvent event) throws IOException, InterruptedException {
    ClientApp.getServerConnection()
        .sendPacket(new PacketMessage(MessageType.REQUEST_MY_AUCTIONS, null));
    //Semaphore
    Timer timer = new Timer();
    timer.schedule(new SemaphoreTask(MyAuctionsController.getMyAuctionsSemaphore()), 6000);
    try {
      MyAuctionsController.getMyAuctionsSemaphore().acquire();
      timer.cancel();
      timer = null;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if(MyAuctionsController.getAuctionsRecieved() != null) {
      Parent root = FXMLLoader.load(getClass().getResource("resources/myAuctions.fxml"));
      ClientApp.getStage().setTitle("Create Auction");
      Scene scene = new Scene(root);
      ClientApp.getStage().setScene(scene);
      ClientApp.getStage().show();
    }
    else{
      JOptionPane.showMessageDialog(null,"Error");
    }

  }

  /**
   * <p>This method will try to disconnect the user by using a confirmation alert. The user should have
   * no active highest bids that will prevent him from disconnecting
   * </p>
   *
   * @see        AlertHelper#showAlert(AlertType, Window, String, String)
   * @see        Window
   * @see        ClientApp#getStage
   */
  @FXML
  protected void disconnect(ActionEvent event) throws IOException {
    Window owner = ClientApp.getStage().getScene().getWindow();
    AlertHelper.showAlert(AlertType.WARNING, owner, "Dissconnect Warning",
        "Are you sure you want to discconnect");

  }

  /**
   * <p> This method will redirect to the welcome page that will be loaded with the WelcomeController initializer. It changes the stage
   * of the main to the one loaded from the fxml Loader
   * </p>
   *
   * @see        FXMLLoader#load() FXMLLoader.load("resources/welcome.fxml")
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    console.textProperty().bind(ClientApp.getServerConnection().consoleMessageProperty());
    try {
      title1.setText("Welcome to the Server " + InetAddress.getLocalHost().getHostName());
      ClientApp.whatsMyIp();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
