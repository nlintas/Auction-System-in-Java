//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis
package Client.Controllers;

import Client.ClientApp;
import Packets.MessageType;
import Packets.PacketMessage;
import Payload.CreateAuctionPayload;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Window;

public class CreateAuctionController implements Initializable {
    @FXML
    private TextField itemName;
    @FXML
    private TextArea description;
    @FXML
    private RadioButton timeFixed;
    @FXML
    private RadioButton timeReset;

    @FXML
    TextField price;

    @FXML
    TextField time;

    @FXML
    private TextArea console;

    @FXML
    private Text invalidInputTime;

    @FXML
    private Text invalidInputPrice;


    @Override
    /**
     * <p>This method is used for loading the Create auction view that is used for creating and auction.
     * Validation is done at this point where the user fills the textfields
     * </p>
     *
     * @param      url   the view loaded
     * @param      resourceBundle     resourceBundle
     * @see        java.lang.System#getProperty(java.lang.String)
     * @see        java.net.URL#setURLStreamHandlerFactory(
     *java.net.URLStreamHandlerFactory)
     * @see        java.net.URLStreamHandler
     * @see        java.net.URLStreamHandlerFactory#createURLStreamHandler(
     *java.lang.String)
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //binds the console to update everytime the memory reference is changed
        console.textProperty().bind(ClientApp.getServerConnection().consoleMessageProperty());

        // force the field to be numeric only
        itemName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue.length() >= 25) {
                    Platform.runLater(() -> {
                        itemName.clear();
                    });
                }
            }
        });

        // force the field to be numeric only except for Floats
        price.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    price.setText(newValue.replaceAll("[^\\d.]", ""));
                }
            }
        });

        // force the field to be numeric only
        time.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    time.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        //select only one at a time
        ToggleGroup toggleGroup = new ToggleGroup();
        timeFixed.setToggleGroup(toggleGroup);
        timeFixed.setSelected(true);
        timeReset.setToggleGroup(toggleGroup);
    }

    /**
     * <p> This method will validate the user data that is inputed in the field to create an auction
     * and send the data to the server. The method allows the price to be a float by checking the . in the
     * text provided.
     * </p>
     * @see
     *
     * @see        PacketMessage
     * @see         NumberFormatException  NumberFormatExeption <p>Invalid Text is displayed if the error occurs</p>
     */
    @FXML
    protected void validate() throws IOException, ClassNotFoundException {

        if (price.getText().trim().isEmpty() || description.getText().trim().isEmpty() || itemName
            .getText().trim().isEmpty() || time.getText().trim().isEmpty()) {
            //Stop up here
        }
        else {
            Float priceCheck = new Float(0);
            try {
                priceCheck = Float.parseFloat(price.getText().trim());
            } catch (NumberFormatException e) {
                invalidInputPrice.setVisible(true);
            }
            //Check if the price has an invalid value
            if (priceCheck < 0.1111111111111) {
                invalidInputPrice.setVisible(true);
                price.setText("");
            }
            //Check if the time has an invalid value
            if (Float.parseFloat(time.getText().trim()) < 0.1111111111111) {
                invalidInputTime.setVisible(true);
                time.setText("");
            } else {
                invalidInputTime.setVisible(false);
                String auctionType = "Time_With_Reset";
                if (timeFixed.isSelected()) {
                    auctionType = "Time_Fixed";
                }
                try {
                    //Send Packet Message for Auction creation
                    PacketMessage packetMessage = new PacketMessage(MessageType.CREATE_AUCTION,
                        new CreateAuctionPayload(auctionType, Float.parseFloat(price.getText()),
                            itemName.getText(), description.getText(),
                            Integer.parseInt(time.getText())));
                    ClientApp.getServerConnection().sendPacket(packetMessage);
                    invalidInputPrice.setVisible(false);
                    back();
                } catch (NumberFormatException e) {//Display Invalid input error
                    invalidInputPrice.setVisible(true);
                    //Reset the text to empty
                    price.setText("");
                }
            }
        }
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
        AlertHelper.showAlert(AlertType.WARNING, owner, "Disconnect Warning",
            "Are you sure you want to disconnect");
    }

}
