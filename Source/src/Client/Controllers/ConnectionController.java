//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis
package Client.Controllers;


import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import Client.ClientApp;

public class ConnectionController implements Initializable {

    /**
     * <p> This method will call the start method from the client class to start the ServerConnection that is used
     * to connect to the server. After the successful connection it will load the initializer method of the WelcomeController
     * that will switch the views and display the Welcome Message sent by the server.
     * </p>
     * <p>
     * Load the View
     * <p>
     * The view is loaded with the help of the FXMLLoader that will search for the resources/welcome file that contains the configurations needed for the
     * view optimisation
     * </p>
     *
     *
     * @see FXMLLoader#load()
     * @see ClientApp#getStage()
     */
    @FXML
    protected void connectToServer() throws IOException {
        ClientApp.start();
        Parent root = FXMLLoader.load(ConnectionController.class.getResource("resources/welcome.fxml"));
        Scene scene = new Scene(root);
        ClientApp.getStage().setScene(scene);
        ClientApp.getStage().show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
