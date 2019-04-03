import game.Game;
import net.StartServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Start the Main Game Thread. */
public class Main {

  // DEFAULT VALUES
  private static boolean client = true;
  //private static String ipAddress = "185.162.250.84";
  // private static String ipAddress = "192.168.1.121";
  // private static String ipAddress = "127.0.0.1";
  private static String ipAddress = "www.buddlerjoe.ch";
  private static int port = 11337;
  private static String username = util.RandomName.getRandomName();

  public static final Logger logger = LoggerFactory.getLogger(Main.class);

  /**
   * Start the GUI and the Network client of the game.
   *
   * @param args not used
   */
  public static void main(String[] args) {

    // Take ip and port from commandline and validate them
    if (args.length >= 1 && args[0].equals("server")) {
      client = false;
    }

    if (args.length >= 2) {
      if (client) {
        String serverIp;
        String[] ipPort = args[1].split(":");
        // Validate IP
        // try {
        //  serverIP = ipPort[0];
        //  String[] parts = serverIP.split("\\.");
        //  if (parts.length != 4) {
        //    serverIP = ipAddress;
        //    logger.error("Invalid IPv4 Address received (not enough groups). Using default.");
        //  } else {
        //    for (String part : parts) {
        //      // This throws NumberFormatException, which is subclass of IllegalArgumentException
        //      int i = Integer.parseInt(part);
        //      if (i < 0 || i > 255) {
        //        serverIP = ipAddress;
        //        logger.error("Invalid IPv4 Address received (invalid range). Using default.");
        //      }
        //    }
        //  }
        // } catch (IllegalArgumentException e) {
        //  serverIP = ipAddress;
        //  logger.error("Invalid IPv4 Address received. Using default.");
        // }
        // ipAddress = serverIP;
        ipAddress = ipPort[0];

        // Validate Port
        if (ipPort.length >= 2) {
          port = validatePort(ipPort[1]);
        }
      } else {
        port = validatePort(args[1]);
      }
    }

    if (args.length >= 3 && client && args[2].length() <= 30 && args[2].length() >= 4) {
      username = args[2];
    }

    if (client) {
      Game game = new Game(ipAddress, port, username);
      game.start();
    } else {
      StartServer server = new StartServer(port);
      server.startServer();
    }
  }

  private static int validatePort(String portString) throws NumberFormatException {
    // Validate Port
    int serverPort;
    try {
      serverPort = Integer.parseInt(portString);
      if (serverPort <= 0 || serverPort > 65535) { // 0 can't be used for TCP connections
        serverPort = port;
        logger.error("Port out of range. Using default.");
      }
    } catch (IllegalArgumentException e) {
      serverPort = port;
      logger.error("Port not properly formatted. Using default.");
    }
    return serverPort;
  }
}
