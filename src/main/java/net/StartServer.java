package net;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server Interface
 *
 * <p>Starts the Server Interface and creates the Server Logic object. Port to listen on can be
 * specified as a commandline option, otherwise the default port is used.
 *
 * <p>If we ever need a server console, we will implement this here.
 */
public class StartServer {

  public static final Logger log = LoggerFactory.getLogger(StartServer.class);

  private static boolean created;
  private static ServerLogic serverLogic;

  /**
   * Start the Interface for the server, listening on a specific port.
   *
   * @param args port to start the {@link ServerLogic} with.
   * @see ServerLogic
   */
  public static void main(String[] args) {

    //log.info("info message");
    //log.debug("debugging");

    // Set Port via commandline or use default port
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

    // Create and start server logic
    while (!created) {
      try {
        serverLogic = new ServerLogic(serverPort);
        serverLogic.waitForPlayers();
        created = true;
      } catch (IOException e) {
        System.out.println("Could not create Server - Use different Port - IO");
      } catch (NumberFormatException nfe) {
        System.out.println("Entered Server Port Incorrectly");
      } finally {
        if (serverLogic != null) {
          serverLogic.kill();
        }
      }
    }
  }
}
