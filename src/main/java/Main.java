import game.Game;
import game.Settings;
import game.SettingsSerialiser;
import net.StartServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Start the Main Game Thread. */
public class Main {

  public static final Logger logger = LoggerFactory.getLogger(Main.class);
  private static SettingsSerialiser settingsSerialiser = new SettingsSerialiser();
  private static Settings settings = settingsSerialiser.readSettings();
  // DEFAULT VALUES
  private static boolean client = true;
  private static int port = 11337;


  /**
   * Start the GUI and the Network client of the game.
   *
   * @param args not used
   */
  public static void main(String[] args) {

    // Client or server
    if (args.length >= 1 && args[0].equals("server")) {
      client = false;
    }

    if (args.length >= 2) {
      if (client) {
        String[] ipPort = args[1].split(":");
        settings.setIp(ipPort[0]);
        // Validate Port
        if (ipPort.length >= 2) {
          port = validatePort(ipPort[1]);
        }
      } else {
        port = validatePort(args[1]);
      }
    }

    if (args.length >= 3 && client && args[2].length() <= 30 && args[2].length() >= 4) {
      String username = args[2];
      settings.setUsername(username);
    }

    if (client) {
      Game game = new Game(settings.getIp(), port, settings.getUsername());
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
