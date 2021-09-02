//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis
package Client.Controllers;


import Client.ClientApp;
import CommonClasses.Item;
import Payload.AuctionListItem;
import Payload.AuctionListPayload;
import Payload.RegisterClientPayload;
import Packets.MessageType;
import Packets.PacketMessage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Window;

public class AuctionController implements Initializable {


    public static AuctionListPayload<AuctionListItem> auctionReceived;
    public static Semaphore liveAuctionsSemaphore = new Semaphore(0);

    public AuctionController() throws IOException {

    }

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
     *auctionListItem Recieved it will add it inside the table to display its attributes accordingly. It contains semaphores
     *that are used for synchronisation between the packet recieved and the loading of the pages. If the content in the auciton have not arrived
     *the semaphore will block so that the content is not loaded.
     * </p>
     *
     * @see        AlertHelper#showAlert(AlertType, Window, String, String)
     * @see        Window
     * @see        ClientApp#getStage
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        console.textProperty().bind(ClientApp.getServerConnection().consoleMessageProperty());
        auction.setCellValueFactory(new PropertyValueFactory<AuctionListItem, String>("auctionID"));
        itemName.setCellValueFactory(new PropertyValueFactory<AuctionListItem, Item>("itemName"));
        description
                .setCellValueFactory(new PropertyValueFactory<AuctionListItem, String>("itemDescription"));
        price.setCellValueFactory(new PropertyValueFactory<AuctionListItem, Float>("itemStartingPrice"));
        highestBid.setCellValueFactory(new PropertyValueFactory<AuctionListItem, Float>("highestBid"));
        owner.setCellValueFactory(new PropertyValueFactory<AuctionListItem, String>("auctionOwnerIP"));

        try {
            tableView.setItems(getAuctions());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TableViewSelectionModel<AuctionListItem> selectionModel = tableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        tableView.setRowFactory(tv -> {
            TableRow<AuctionListItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    AuctionListItem rowData = row.getItem();
                    try {
                        register(rowData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });

    }

    /**
     * <p> This method will redirect to the welcome page that will be loaded with the WelcomeController initializer. It changes the stage
     * of the main to the one loaded from the fxml Loader
     * </p>
     *
     * @see        FXMLLoader#load() FXMLLoader.load("resources/welcome.fxml")
     */
    @FXML
    protected void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/welcome.fxml"));
        ClientApp.getStage().setTitle("Registration Form FXML Application");
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
    protected void disconnect(ActionEvent event) throws IOException {
        Window owner = ClientApp.getStage().getScene().getWindow();
        AlertHelper.showAlert(AlertType.WARNING, owner, "Dissconnect Warning",
                "Are you sure you want to discconnect");
    }

    /**
     * <p>The register method will get the value of the auction id and parse it into the PacketMessage that the user is going
     * to send to the client for registering in the auction. This method will redirect to the bid that loads the fxml file for
     * BidController.
     * </p>
     *
     */
    protected void register(AuctionListItem id) throws IOException {
        ClientApp.getServerConnection().sendPacket(new PacketMessage(MessageType.REGISTER_IN_AUCTION,
                new RegisterClientPayload(Integer.valueOf(id.getAuctionID()))));
        System.out.println("I have asked server to join an auction");
        bid();
    }

    /**
     * <p>The method will load the resource fxml file that is used for the bid view. For the loaded fxml file
     * the BidController will use the initialize method to set the necessary attributes.
     * </p>
     *
     * @see        FXMLLoader
     */
    public void bid() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Bid.fxml"));
        ClientApp.getStage().setTitle("Registration Form FXML Application");
        Scene scene = new Scene(root);
        ClientApp.getStage().setScene(scene);
        ClientApp.getStage().show();
    }

    public ObservableList<AuctionListItem> getAuctions() throws InterruptedException {
        ObservableList<AuctionListItem> auctions = FXCollections.observableArrayList();
        Thread.sleep(10);
        for (AuctionListItem auctionListItem : auctionReceived.getAuctionList()
        ) {
            auctions.add(auctionListItem);
        }
        return auctions;
    }

    public static AuctionListPayload<AuctionListItem> getAuctionReceived() {
        return auctionReceived;
    }

    public static void setAuctionsReceived(AuctionListPayload<AuctionListItem> auctionsReceived) {
        AuctionController.auctionReceived = auctionsReceived;
    }

    public static Semaphore getLiveAuctionsSemaphore() {
        return liveAuctionsSemaphore;
    }

    public static void setLiveAuctionsSemaphore(Semaphore liveAuctionsSemaphore) {
        AuctionController.liveAuctionsSemaphore = liveAuctionsSemaphore;
    }
}
