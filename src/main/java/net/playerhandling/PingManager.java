package net.playerhandling;

import static java.lang.Thread.sleep;

import java.util.concurrent.CopyOnWriteArrayList;
import net.ClientLogic;
import net.packets.loginlogout.PacketDisconnect;
import net.packets.pingpong.PacketPing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The function of the PingManager is to automatically send pings to client and server and to
 * calculate the respective average ping. This class is activated by the <code>ClientThread</code>
 * and the <code>ClientLogic</code> class.
 *
 * @see ClientThread
 * @see net.ClientLogic
 */
public class PingManager implements Runnable {

  public static final Logger logger = LoggerFactory.getLogger(PingManager.class);
  private final int freq = 1000;
  private volatile boolean exit = false;
  /**
   * Class Variables listOfPingTs this list contains the creation time of all sent pings. ping the
   * average ping clientId the identity of the client freq frequency
   */
  private CopyOnWriteArrayList<String> listOfPingTs;

  private float ping;
  private int clientId;

  /**
   * Creates a <code>PingManager</code> object when sending ping from server to client. The client
   * is determined by the <code>clientId</code>.
   *
   * @param clientId client unique identifier
   */
  public PingManager(int clientId) {
    listOfPingTs = new CopyOnWriteArrayList<>();
    ping = 0;
    this.clientId = clientId;
  }

  /** Creates a <code>PingManager</code> object when sending ping from client to server. */
  public PingManager() {
    listOfPingTs = new CopyOnWriteArrayList<>();
    ping = 0;
    this.clientId = 0;
  }

  /**
   * Executes every second the automized sending of the pings and saves the creation time in <code>
   * listOfPingTs</code>. The destination is determined by the <code>clientId</code>. If the
   * clientId was passed, the ping would be sent to the client. Otherwise, the clientId will have
   * the default value 0 and the ping will be sent to the server. A ping object will be created by
   * instantiating the <code>PacketPing</code> class.
   *
   * @see PacketPing
   */
  public void run() {
    while (!exit) {
      try {
        sleep(freq);
      } catch (InterruptedException e) {
        System.out.println("Sleep interrupted");
      }
      checkForDisconnect();
      long currTime;
      if (clientId > 0) { // from server to client
        currTime = System.currentTimeMillis();
        String data = String.valueOf(currTime);
        append(data);
        PacketPing packetPing = new PacketPing(clientId, data);
        packetPing.sendToClient(clientId);
      } else { // from client to server
        currTime = System.currentTimeMillis();
        String data = String.valueOf(currTime);
        append(data);
        PacketPing packetPing = new PacketPing(data);
        packetPing.sendToServer();
      }
    }
  }

  /**
   * Appends the creation time of the <code>PacketPing</code> object to <code>listOfPingTs</code>.
   *
   * @param timestamp creation time of the ping
   */
  private void append(String timestamp) {
    listOfPingTs.add(timestamp);
  }

  /**
   * Deletes the creation time of the <code>PacketPing</code> object in <code>listOfPingTs</code> if
   * the respective <code>PacketPong</code> object returned successfully.
   *
   * @param timestamp creation time of the ping
   */
  public void delete(String timestamp) {
    listOfPingTs.remove(timestamp);
  }

  /**
   * Updates the average <code>ping</code>.
   *
   * @param diffTime the difference between the arrival time of the final <code>PacketPong</code>
   *     object and the creation time of the <code>PacketPing</code> object
   */
  public void updatePing(long diffTime) {
    ping = (ping * 9 + diffTime) / 10f;
  }

  /**
   * Check if connection is timed out. First remove old pings. Then check for packet loss and max
   * ping.
   */
  private void checkForDisconnect() {

    // Delete pings older than 10 seconds
    long currTime = System.currentTimeMillis();
    for (String pingElement : listOfPingTs) {
      try {
        long pingStart = Long.parseLong(pingElement);
        if (currTime - pingStart > freq * 10) {
          delete(pingElement);
          logger.info("Ping Expired.");
        }
      } catch (NumberFormatException e) {
        logger.warn("Ping time has wrong formatting: " + pingElement);
      }
    }

    // Check for disconnect conditions
    if (listOfPingTs.size() > 0) {
      logger.debug("Number of unanswered pings: " + listOfPingTs.size());
    }
    if (listOfPingTs.size() >= 0.8f / freq * 10000 || ping > 1000) {
      if (clientId > 0) {
        // Server kicks client out
        new PacketDisconnect(clientId).processData();
        stop(); // Stop this thread
      } else {
        // Client disconnects from server
        ClientLogic.setDisconnectFromServer(true);
        stop(); // Stop this thread
      }
    }
  }

  /**
   * Informs every player about their ping.
   *
   * @return average ping time.
   */
  public long getPing() {
    return (int) (ping);
  }

  /** Stop this thread. */
  public void stop() {
    exit = true;
  }
}
