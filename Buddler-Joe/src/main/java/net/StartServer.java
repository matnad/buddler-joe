package net;

import java.io.IOException;


/**
 * Server Interface
 *
 * Starts the Server Interface and creates the Server Logic object.
 * Port to listen on can be specified as a commandline option, otherwise the default port is used.
 *
 * If we ever need a server console, we will implement this here.
 */
public class StartServer {

    private static boolean created;
    private static ServerLogic serverLogic;

    /**
     * Start the Interface for the server, listening on a specific port.
     *
     * @param args port to start the {@link ServerLogic} with.
     *
     * @see ServerLogic
     */
    public static void main(String[] args) {

        //Set Port via commandline or use default port
        int serverPort;
        if (args.length == 1) {
            try {
                serverPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                serverPort = 11337;
            }
        } else {
            serverPort = 11337;
        }

        //Create and start server logic
        while (!created) {
            try {
                serverLogic = new ServerLogic(serverPort);
                serverLogic.waitForPlayers();
                created = true;
            } catch (IOException e) {
                System.out.println("Could not create Server - Use different Port - IO");
            } catch (NumberFormatException NFE) {
                System.out.println("Entered Server Port Incorrectly");
            } finally {
                if (serverLogic != null)
                    serverLogic.kill();
            }

        }


    }
}
