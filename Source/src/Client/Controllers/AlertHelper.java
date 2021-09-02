package Client.Controllers;

import static Packets.MessageType.CANCEL_AUCTION;
import static Packets.MessageType.DISCONNECT;

import Client.ClientApp;
import Packets.PacketMessage;
import Payload.CancelAuctionPayload;
import Payload.ConfirmAuctionCancellationPayload;
import java.io.IOException;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

public class AlertHelper {

  public static void showAlert(Alert.AlertType alertType, Window owner, String title,
      String message) {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.initOwner(owner);
    Optional<ButtonType> result = alert.showAndWait();
    ButtonType button = result.orElse(ButtonType.CANCEL);
    if (!result.isPresent()) {
    } else if (result.get() == ButtonType.OK) {
      try {
        ClientApp.getServerConnection().sendPacket(new PacketMessage(DISCONNECT, null));
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    //oke button is pressed
    else if (result.get() == ButtonType.CANCEL) {
    }
  }


  public static boolean showRegisterAlert(Alert.AlertType alertType, Window owner, String title,
      String message){

    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.initOwner(owner);
    Optional<ButtonType> result = alert.showAndWait();
    ButtonType button = result.orElse(ButtonType.CANCEL);
    if (!result.isPresent()) {
    } else if (result.get() == ButtonType.OK) {
      // Main.sendRequest();
      return true;
    }
    //oke button is pressed
    else if (result.get() == ButtonType.CANCEL) {
      return false;
    }
    return false;
  }


  public static boolean showCancelationConfirmation(Alert.AlertType alertType, Window owner,
      String title,
      String message, int auction) throws IOException {

    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.initOwner(owner);
    Optional<ButtonType> result = alert.showAndWait();
    ButtonType button = result.orElse(ButtonType.CANCEL);
    if (!result.isPresent()) {
    } else if (result.get() == ButtonType.OK) {
     ClientApp.getServerConnection().sendPacket(new PacketMessage(CANCEL_AUCTION, new ConfirmAuctionCancellationPayload(
              auction)));
      return true;
    }
    //oke button is pressed
    else if (result.get() == ButtonType.CANCEL) {
      return false;
    }
    return false;
  }
}