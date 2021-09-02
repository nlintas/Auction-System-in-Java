//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis
package Client.Controllers;

import Client.SemaphoreTasks.SemaphoreTask;
import CommonClasses.Item;
import Client.ClientApp;
import Payload.AuctionListItem;
import Payload.AuctionListPayload;
import Server.AuctionTerminateTask;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.concurrent.Semaphore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;
import javax.swing.JOptionPane;

public class MyAuctionsController implements Initializable {
  public static AuctionListPayload<AuctionListItem> auctionsRecieved;
  public static Semaphore myAuctionsSemaphore = new Semaphore(0);

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
  private TextArea console;

  /**
   * <p>This method is used to initialize the table content that are used to be put in the table view. For every
   * auctionListItem recieved it will add it inside the table to display its attributes accordingly. It contains semaphores
   * that are used for synchronisation between the packet recieved and the loading of the pages. If the content in the auction have not arrived
   * the semaphore will block so that the content is not loaded.
   * </p>
   *
   *
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

      //set the properties to be extracted from the AuctionListItem
      console.textProperty().bind(ClientApp.getServerConnection().consoleMessageProperty());
      auction.setCellValueFactory(new PropertyValueFactory<AuctionListItem, String>("auctionID"));
      itemName.setCellValueFactory(new PropertyValueFactory<AuctionListItem, Item>("itemName"));
      description
          .setCellValueFactory(
              new PropertyValueFactory<AuctionListItem, String>("itemDescription"));
      price.setCellValueFactory(
          new PropertyValueFactory<AuctionListItem, Float>("itemStartingPrice"));
      highestBid
          .setCellValueFactory(new PropertyValueFactory<AuctionListItem, Float>("highestBid"));
      owner
          .setCellValueFactory(new PropertyValueFactory<AuctionListItem, String>("auctionOwnerIP"));
      //Try to acquire the semaphore that is released when the content arrives from the server,
      if (auctionsRecieved != null) {
        try {
          tableView.setItems(getAuctions());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        //get the seletcion models that allows only one selection per table row.
        TableViewSelectionModel<AuctionListItem> selectionModel = tableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        ObservableList<AuctionListItem> selectedItems = selectionModel.getSelectedItems();

        //make the click property for double clicking
        tableView.setRowFactory(tv -> {
          TableRow<AuctionListItem> row = new TableRow<>();
          row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (!row.isEmpty())) {
              AuctionListItem rowData = row.getItem();
            }
          });
          return row;
        });
      }
    }



  /**
   * <p>This method will make sure that the auctions are returned accoring to the content reieved from the server. The variable auctions
   * contains a list of auctionListItems that will be extracted into rows for Data Display
   * </p>
   *
   * @see        AuctionListItem
   * @see         ObservableList
   * @see         TableView
   * @see         TableColumn
   *
   *java.lang.String)
   */

  public ObservableList<AuctionListItem> getAuctions() throws InterruptedException {
    ObservableList<AuctionListItem> auctions = FXCollections.observableArrayList();
    //Make sure the loaded content is not null
    if(!auctionsRecieved.getAuctionList().isEmpty()){
      for (AuctionListItem auctionListItem: auctionsRecieved.getAuctionList()
      ) {
        auctions.add(auctionListItem);
      }
    }
    return auctions;
  }

  /**
   * <p> This method will redirect to the welcome page that will be loaded with the WelcomeController initializer. It changes the stage
   * of the main to the one loaded from the fxml Loader
   * </p>
   *
   * @see        FXMLLoader#load() FXMLLoader.load("resources/welcome.fxml")
   */
  @FXML
  protected void back() throws IOException {
    auctionsRecieved = null;
    Parent root = FXMLLoader.load(getClass().getResource("resources/welcome.fxml"));
    Scene scene = new Scene(root);
    ClientApp.getStage().setScene(scene);
    ClientApp.getStage().show();
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
  protected void disconnect() {
    Window owner = ClientApp.getStage().getScene().getWindow();
    AlertHelper.showAlert(AlertType.WARNING, owner, "Dissconnect Warning",
        "Are you sure you want to discconnect");
  }

  public static AuctionListPayload<AuctionListItem> getAuctionsRecieved() {
    return auctionsRecieved;
  }

  public static void setAuctionsReceived(
      AuctionListPayload<AuctionListItem> auctionsReceived) {
    MyAuctionsController.auctionsRecieved = auctionsReceived;
  }

  public static Semaphore getMyAuctionsSemaphore() {
    return myAuctionsSemaphore;
  }

  public static void setMyAuctionsSemaphore(Semaphore myAuctionsSemaphore) {
    MyAuctionsController.myAuctionsSemaphore = myAuctionsSemaphore;
  }
}
