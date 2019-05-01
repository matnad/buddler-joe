package net;

import java.io.IOException;
import net.lobbyhandling.ServerItemState;
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

  public static final Logger logger = LoggerFactory.getLogger(StartServer.class);

  private boolean created;
  private ServerLogic serverLogic;
  private int serverPort;
  private ServerItemState serverItemState = new ServerItemState();

  /**
   * Start the Interface for the server, listening on a specific port.
   *
   * @param serverPort Port to listen to
   * @see ServerLogic
   */
  public StartServer(int serverPort) {
    this.serverPort = serverPort;
  }

  /**
   * Start the Interface for the server, listening on a default.
   *
   * @param args none. Use Main to start via commandline.
   * @see ServerLogic
   */
  public static void main(String[] args) {
    StartServer startServer = new StartServer(11337);
    startServer.startServer();
  }

  /**
   * Start the Interface for the server, listening on a specific port.
   *
   * @see ServerLogic
   */
  public void startServer() {
    // Create and start server logic
    while (!created) {
      try {
        serverLogic = new ServerLogic(serverPort);
        serverLogic.waitForPlayers();
        created = true;
      } catch (IOException e) {
        logger.error("Could not create Server - Use different Port - IO");
      } catch (NumberFormatException nfe) {
        logger.error("Entered Server Port Incorrectly");
      } finally {
        if (serverLogic != null) {
          serverLogic.kill();
        }
      }
    }
  }

  public ServerLogic getServerLogic() {
    return serverLogic;
  }
}
