//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Server;

import java.io.IOException;

public class ServerApp {

    public static void main(String[] args) throws IOException {
      
        int port = 0;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be a String.");
                System.exit(1);
            }
        }
        if (args.length > 1) {
            System.err.println("There need to be only two arguments");
            System.exit(1);
        }

        //Create and get a server instance
        Server server = Server.getInstance(port);
        //Start the server and make it accept connection requests
        server.listen();
    }
}
