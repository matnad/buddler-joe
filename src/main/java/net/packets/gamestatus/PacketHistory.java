package net.packets.gamestatus;

import game.Game;
import java.util.concurrent.CopyOnWriteArrayList;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A packed that is send from the server to the client, which contains the "History". Packet-Code:
 * HISTO
 *
 * @author Sebastian Schlachter
 */
public class PacketHistory extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketHistory.class);
  private String[] in;
  CopyOnWriteArrayList<String> catalog = new CopyOnWriteArrayList<>();

  /**
   * Constructor that is used by the Client to build the Packet, after receiving the Command HISTO.
   *
   * @param data a single String that begins with "OK║" and contains a Listing of all open lobbies,
   *     lobbies that are currently in a round and a List of all Gemes that are finished. Each list
   *     entry is separated by "║". In the case that an error occurred before, the String is an
   *     errormessage and does not begin with "OK║". The variable data gets split at the positions
   *     of "║". Every substring gets then saved in to the Array called {@code in}.
   */
  public PacketHistory(String data) {
    // Client receives
    super(PacketTypes.HISTORY);
    setData(data);
    try {
      in = getData().split("║");
    } catch (NullPointerException e) {
      addError("Data is null.");
    }
    validate();
  }

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientId ClientId of the the receiver.
   * @param data a single String that begins with "OK║" and contains a Listing of all open lobbies,
   *     lobbies that are currently in a round and a List of all Gemes that are finished. Each list
   *     entry is separated by "║". In the case that an err or occurred before, the String is an
   *     errormessage and does not begin with "OK║". The variable data gets split at the positions
   *     of "║". Every substring gets then saved in to the Array called {@code in}.
   */
  public PacketHistory(int clientId, String data) {
    // server builds
    super(PacketTypes.HISTORY);
    setClientId(clientId);
    setData(data);
    try {
      in = getData().split("║");
    } catch (NullPointerException e) {
      addError("Data is null.");
    }
    validate();
  }

  /**
   * Validation method to check the data that has, or will be send in this packet. Checks if {@code
   * data} is not null and the entries are Strings in extended ASCII.
   */
  @Override
  public void validate() {
    if (getData() != null) {
      for (String s : in) {
        isExtendedAscii(s);
      }
    } else {
      addError("No data has been found.");
    }
  }

  /**
   * Method that lets the Client react to the receiving of this packet. Check for errors in
   * validate. If there are no errors and the first entry of {@code in} equals "OK", all the other
   * entries of {@code in} get printed to the console (one per line).
   */
  @Override
  public void processData() {
    if (hasErrors()) {
      // System.out.println(createErrorMessage());
    } else if (in[0].equals("OK")) {
      // System.out.println("-----------------------------------------------------");
      for (int i = 1; i < in.length; i++) {
        // System.out.println(in[i]);
        if ((catalog.size() + 1) % 6 == 0
            && (in[i].equals("Lobbies Of Running Games:") || in[i].equals("Old Games:"))) {
          catalog.add("");
        }
        catalog.add(in[i]);
      }
      Game.setHistoryCatalog(catalog);
      // System.out.println("-----------------------------------------------------");
    } else {
      // System.out.println(in[0]);
    }
  }

  public CopyOnWriteArrayList<String> getCatalog() {
    return catalog;
  }
}
