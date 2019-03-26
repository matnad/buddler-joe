package net.playerhandling;

import static java.lang.Thread.sleep;

import java.util.ArrayList;
import java.util.Iterator;

import net.packets.loginlogout.PacketDisconnect;
import net.packets.pingpong.PacketPing;

/**
 * The function of the PingManager is to automatically send pings to client and server and to
 * calculate the respective average ping. This class is activated by the <code>ClientThread</code>
 * and the <code>ClientLogic</code> class.
 *
 * @see ClientThread
 * @see net.ClientLogic
 */
public class PingManager implements Runnable {
  /**
   * Class Variables listOfPingTS this list contains the creation time of all sent pings. ping the
   * average ping clientId the identity of the client freq frequency
   */
  private ArrayList<String> listOfPingTS;

  private float ping;
  private int clientId;
  private final int freq = 1000;

  /**
   * Creates a <code>PingManager</code> object when sending ping from server to client. The client
   * is determined by the <code>clientId</code>.
   *
   * @param clientId client unique identifier
   */
  public PingManager(int clientId) {
    listOfPingTS = new ArrayList<>();
    ping = 0;
    this.clientId = clientId;
  }

  /** Creates a <code>PingManager</code> object when sending ping from client to server. */
  public PingManager() {
    listOfPingTS = new ArrayList<>();
    ping = 0;
  }

  /**
   * Executes every second the automized sending of the pings and saves the creation time in <code>
   * listOfPingTS</code>. The destination is determined by the <code>clientId</code>. If the
   * clientId was passed, the ping would be sent to the client. Otherwise, the clientId will have
   * the default value 0 and the ping will be sent to the server. A ping object will be created by
   * instantiating the <code>PacketPing</code> class.
   *
   * @see PacketPing
   */
  public void run() {
    while (true) {
      try {
        sleep(freq);
      } catch (InterruptedException e) {
        System.out.println("Sleep interrupted");
      }
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
   * Appends the creation time of the <code>PacketPing</code> object to <code>listOfPingTS</code>.
   *
   * @param timestamp creation time of the ping
   */
  private void append(String timestamp) {
    listOfPingTS.add(timestamp);
  }

  /**
   * Deletes the creation time of the <code>PacketPing</code> object in <code>listOfPingTS</code> if
   * the respective <code>PacketPong</code> object returned successfully.
   *
   * @param timestamp creation time of the ping
   */
  public void delete(String timestamp) {
    listOfPingTS.remove(timestamp);
  }

  /**
   * Updates the average <code>ping</code>.
   *
   * @param diffTime the difference between the arrival time of the final <code>PacketPong</code>
   *     object and the creation time of the <code>PacketPing</code> object
   */
  public void updatePing(long diffTime) {
    ping = (ping * 9 + diffTime) / 10f;
    Iterator<String> iter = listOfPingTS.iterator();
    long currTime = System.currentTimeMillis();
    String str;
    try {
      while (iter.hasNext()) {
        str = iter.next();
        if (currTime - Long.parseLong(str) > 10000) {
          iter.remove();
        }
      }
    } catch (NumberFormatException e) {
      System.out.println("Number Exception");
    }
    if (listOfPingTS.size() > 0.9f / freq * 10000 || ping > 1000) {
      new PacketDisconnect(clientId).processData();
    }
  }

  public ArrayList getListOfPingTS() {
    return listOfPingTS;
  }

  /**
   * Informs every player about their ping.
   *
   * @return average ping time.
   */
  public long getPing() {
    return (int) (ping);
  }
}
