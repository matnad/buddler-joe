package net.packets.gamestatus;

import net.packets.Packet;

/**
 * A packed that is send from the server to the client, which contains the "History". Packet-Code:
 * HISTO
 *
 * @author Sebastian Schlachter
 */
public class PacketHistory extends Packet {

  private String[] in;

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
    in = getData().split("║");
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
    in = getData().split("║");
    validate();
  }

  @Override
  public void validate() {
    if (getData() != null) {
      for (String s : in) {
        isExtendedAscii(s);
      }
    } else {
      addError("No data has been found");
    }
  }

  @Override
  public void processData() {
    if (hasErrors()) {
      System.out.println(createErrorMessage());
    } else if (in[0].equals("OK")) {
      System.out.println("-----------------------------------------------------");
      for (int i = 1; i < in.length; i++) {
        System.out.println(in[i]);
      }
      System.out.println("-----------------------------------------------------");
    } else {
      System.out.println(in[0]);
    }
  }
}
