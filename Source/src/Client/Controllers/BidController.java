//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis
package Client.Controllers;

import static Packets.MessageType.MAKE_BID;
import static Packets.MessageType.UNREGISTER_FROM_AUCTION;

import Client.ClientApp;
import Client.SemaphoreTasks.SemaphoreTask;
import CommonClasses.Item;
import Packets.MessageType;
import Packets.PacketMessage;
import Payload.AuctionListItem;
import Payload.MakeBidPayload;
import Payload.RequestHighestBidPayload;
import Payload.SendAuctionPayload;
import Payload.UnregisterClientPayload;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.concurrent.Semaphore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javax.swing.JOptionPane;

public class BidController implements Initializable {
  private static SendAuctionPayload auctionBid;
  private static Semaphore bidLoadSemaphore = new Semaphore(0);
  private static Semaphore bidRefreshSemaphore = new Semaphore(0);
  private static Semaphore bidSubmitSemaphore =  new Semaphore(0);
  private static float highestBidRefresh;
  private static String bidderIp;


  @FXML
  private TextField textField;

  @FXML
  private Button cancel;

  @FXML
  private TextArea console;

  @FXML
  private TreeTableView table;

  @FXML
  private TableView<AuctionListItem> tableView;

  @FXML
  private TableColumn<AuctionListItem, String> auction;

  @FXML
  private TableColumn<AuctionListItem, Item> itemName;

  @FXML
  private TableColumn<AuctionListItem, String> description;

  @FXML
  private TableColumn<AuctionListItem, Float> price;

  @FXML
  private TableColumn<AuctionListItem, Float> highestBid;

  @FXML
  private TableColumn<AuctionListItem, String> owner;

  @FXML
  private Button submit;

  @FXML
  private Text invalidInput;

  /**
   * <p>This method is used to initialize the table content that are used to be put in the table view. For every
   * auctionListItem Received it will add it inisde the table to display its attributes accordingly. It contains semaphores
   * that are used for synchronisation between the packet received and the loading of the pages. If the content in the auction have not arrived
   * the semaphore will block so that the content is not loaded.
   * </p>
   *
   * @func  Load Delete button <p>Load the deletion button if the user matches the auction owner.</p>
   *
   * @param      url   the view loaded
   * @param      resourceBundle     resourceBundle
   * @see        java.lang.System#getProperty(java.lang.String)
   * @see         Semaphore
   * @see         TableView
   * @see         TableColumn
   *
   *java.lang.String)
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    auction.setCellValueFactory(new PropertyValueFactory<AuctionListItem, String>("auctionID"));
    itemName.setCellValueFactory(new PropertyValueFactory<AuctionListItem, Item>("itemName"));
    description
        .setCellValueFactory(new PropertyValueFactory<AuctionListItem, String>("itemDescription"));
    price.setCellValueFactory(new PropertyValueFactory<AuctionListItem, Float>("itemStartingPrice"));
    highestBid.setCellValueFactory(new PropertyValueFactory<AuctionListItem, Float>("highestBid"));
    owner.setCellValueFactory(new PropertyValueFactory<AuctionListItem, String>("auctionOwnerIP"));
    console.textProperty().bind(ClientApp.getServerConnection().consoleMessageProperty());

    try {
      bidLoadSemaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    try {
      tableView.setItems(getAuctions());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // force the field to be numeric only
    textField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("\\d*")) {
        textField.setText(newValue.replaceAll("[^\\d.]", ""));
      }
    });

    //should be InetAddress.getLocalHost().getHostAddress();

    if (auctionBid.getAuctionOwnerIP().equals(ClientApp.getClientIp())) {
      cancel.setVisible(true);
      cancel.setOnAction(event -> {
        Window owner = ClientApp.getStage().getScene().getWindow();
        try {
          AlertHelper.showCancelationConfirmation(AlertType.CONFIRMATION, owner, "Auction Deletion",
              "Are you sure you want to Delete the Auction", auctionBid.getAuctionID());
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {
          back();
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }
  }

  /**
   * <p> This method will send a PacketMessage of type REQUEST_ACTIVE_AUCTION_LIST so that the auction list is updated on the AuctionController.
   * It uses a timerTask to handle the block on the AuctionController that might occur if the Server never replies.
   *
   * </p>
   * @see        java.sql.Time
   * @see         MessageType#REQUEST_ACTIVE_AUCTION_LIST
   * @see         FXMLLoader
   * @see        FXMLLoader#load() FXMLLoader.load("resources/welcome.fxml")
   */
  @FXML
  protected void back() throws IOException {
    setAuctionBid(null);
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
    if (AuctionController.getAuctionReceived() != null) {
      Parent root = FXMLLoader.load(getClass().getResource("resources/auctions.fxml"));
      Scene scene = new Scene(root);
      ClientApp.getStage().setScene(scene);
      ClientApp.getStage().show();
    } else {
      JOptionPane.showMessageDialog(null, "Error Received");
    }
  }

  /**
   * <p> This method will validate the user data that is inputed in the field to create a bid
   * and send the data to the server. The method allows the price to be a float by checking the . in the
   * text provided. The invalid input label will be set to visible if a mistake is submitted.
   * </p>
   * @see
   *
   * @see        PacketMessage
   * @see         NumberFormatException  NumberFormatExeption <p>Invalid Text is displayed if the error occurs</p>
   */
  @FXML
  protected void validate() throws IOException, ClassNotFoundException, InterruptedException {
    try {
      if(!textField.getText().trim().isEmpty()){
        ClientApp.getServerConnection().sendPacket(new PacketMessage(MAKE_BID,new MakeBidPayload(
            auctionBid.getAuctionID(), Float.parseFloat(textField.getText()))));
        textField.setText("");
        invalidInput.setVisible(false);
      }
    }catch (NumberFormatException e){
      textField.setText("");
      invalidInput.setVisible(true);
    }
  }

  /**
   * <p> The refresh button is used to request the highest bid from the actual auction that the user is looking at the moment.
   * The method will send a PacketMessage
   * </p>
   * @see
   *
   * @see        PacketMessage
   * @see         NumberFormatException  NumberFormatExeption <p>Invalid Text is displayed if the error occurs</p>
   */
  @FXML
  protected void refresh() throws IOException, InterruptedException {
    ClientApp.getServerConnection().sendPacket(new PacketMessage(MessageType.REQUEST_HIGHEST_BID,
        new RequestHighestBidPayload(auctionBid.getAuctionID())));

    Timer timer = new Timer();
    timer.schedule(new SemaphoreTask(bidRefreshSemaphore), 6000);
    try {
      bidRefreshSemaphore.acquire();
      timer.cancel();
      timer = null;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    if (bidderIp != null) {
      auctionBid.setHighestBid(highestBidRefresh);
      try {
        tableView.setItems(getAuctions());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } else { ;
      auctionBid.setHighestBid(highestBidRefresh);
      try {
        tableView.setItems(getAuctions());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }
    bidderIp = null;
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
    AlertHelper.showAlert(AlertType.WARNING,owner,"Dissconnect Warning","Are you sure you want to discconnect");
  }

  /**
   * <p> This method will set the auctionBid that the client recieved to register to. The method returns
   * an ObservableList that contains all the information for the tableView.
   * </p>
   *
   * @see        ObservableList
   * @see        AuctionListItem
   * @see        ClientApp#getStage
   */
  public ObservableList<AuctionListItem> getAuctions() throws InterruptedException {
    ObservableList<AuctionListItem> auctions = FXCollections.observableArrayList();

    AuctionListItem auctionListItem = new AuctionListItem(auctionBid.getAuctionID(),
        auctionBid.getItemStartingPrice(), auctionBid.getItemName(),
        auctionBid.getItemDescription(), auctionBid.getAuctionOwnerIP(),
        auctionBid.getHighestBid());
    auctions.add(auctionListItem);
    return auctions;
  }

  /**
   * <p> This method will send a packet to te server that will contain the PacketMessage to unregister from
   * the current auction he is looking at. The method will redirect the user to the live auctions method.
   * </p>
   *
   * @see        MessageType#UNREGISTER_FROM_AUCTION
   * @see        UnregisterClientPayload
   * @see        ClientApp#getStage
   */
  @FXML
  protected void unRegister() throws IOException {
    ClientApp.getServerConnection()
        .sendPacket(new PacketMessage(UNREGISTER_FROM_AUCTION, new UnregisterClientPayload(
            auctionBid.getAuctionID())));
    back();
  }


  /**
   * This Section Contains the Getters and Setters of the BidController
   * @return
   */

  public static SendAuctionPayload getAuctionBid() {
    return auctionBid;
  }

  public static void setAuctionBid(SendAuctionPayload auctionBid) {
    BidController.auctionBid = auctionBid;
  }


  public static Semaphore getBidLoadSemaphore() {
    return bidLoadSemaphore;
  }

  public static void setBidLoadSemaphore(Semaphore bidLoadSemaphore) {
    BidController.bidLoadSemaphore = bidLoadSemaphore;
  }

  public static Semaphore getBidRefreshSemaphore() {
    return bidRefreshSemaphore;
  }

  public static void setBidRefreshSemaphore(Semaphore bidRefreshSemaphore) {
    BidController.bidRefreshSemaphore = bidRefreshSemaphore;
  }

  public static Semaphore getBidSubmitSemaphore() {
    return bidSubmitSemaphore;
  }

  public static void setBidSubmitSemaphore(Semaphore bidSubmitSemaphore) {
    BidController.bidSubmitSemaphore = bidSubmitSemaphore;
  }

  public static float getHighestBidRefresh() {
    return highestBidRefresh;
  }

  public static void setHighestBidRefresh(float highestBidRefresh) {
    BidController.highestBidRefresh = highestBidRefresh;
  }

  public static String getBidderIp() {
    return bidderIp;
  }

  public static void setBidderIp(String bidderIp) {
    BidController.bidderIp = bidderIp;
  }
}
