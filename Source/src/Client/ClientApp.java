//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    //Attributes
    private static ExecutorService pool;
    private static Stage stage;
    private static ServerConnection serverConnection;
    private static IntegerProperty value;
    private static FXMLLoader fxmlLoader;
    private static String clientIp;
    private static String ip;
    private static int port;


    /**
     * The main method is only needed for the IDE with limited JavaFX support. Not needed for running
     * from the command line.
     */
    public static void main(String[] args){
        //Check for arguments
        if (args.length > 0) {
            try {
                ip = String.valueOf(args[0]);
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be a String.");
                System.exit(1);
            }
        }
        //Check if user inputs more than two arguments
         if(args.length > 2){
            System.err.println("There need to be only two arguments");
             System.exit(1);
        }
        launch(args);
    }

    /**
     * The start method is used for starting the GUI and loading the first page
     * that appears for server connection. It uses FXML loader for loading the file connection.fxml
     * under the package resources.
     */
    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) throws IOException {
        pool = Executors.newFixedThreadPool(4);
        value = new SimpleIntegerProperty(0);
        fxmlLoader = new FXMLLoader();
        //load the first window for connection
        Parent root = fxmlLoader.load(getClass().getResource("Controllers/resources/connection.fxml"));
        stage = new Stage();
        stage.setTitle("Main Page");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

        /**
         *  This start method is used to intialize the ServerConnection that connects to the Server. It adds it into the
         * pool excectioner for threading.
         * @see ServerConnection
         * @see ExecutorService
     */
    public static void start() {
        serverConnection = new ServerConnection(ip,port);
        pool.execute(serverConnection);
    }

    /**
     *  This method is used to find my ip from another server. Ths is used in the beggining of the application so that
     *  the ip is known to the user and used in the BidConroller for setting the delete button based on the ip of the auction owner.
     * @see Client.Controllers.BidController#initialize(URL, ResourceBundle)
     * @see BufferedReader
     */
    public static String whatsMyIp() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        clientIp = in.readLine(); //you get the IP as a String
        return clientIp;
    }

    public static String getClientIp() {
        return clientIp;
    }

    public static void setClientIp(String clientIp) {
        ClientApp.clientIp = clientIp;
    }

    //Getters and Setters

    public static ExecutorService getPool() {
        return pool;
    }

    public static void setPool(ExecutorService pool) {
        ClientApp.pool = pool;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        ClientApp.stage = stage;
    }

    public static ServerConnection getServerConnection() {
        return serverConnection;
    }

    public static void setServerConnection(ServerConnection serverConnection) {
        ClientApp.serverConnection = serverConnection;
    }


    public static void setValue(int value) {
        ClientApp.value.set(value);
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        ClientApp.ip = ip;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        ClientApp.port = port;
    }
}