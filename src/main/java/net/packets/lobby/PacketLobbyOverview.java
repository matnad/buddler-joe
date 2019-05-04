package net.packets.lobby;

import game.Game;
import game.LobbyEntry;
import java.util.concurrent.CopyOnWriteArrayList;
import net.packets.Packet;

/**
 * A packed that is send from the server to the client, which contains a List of at max 10 Lobbies
 * that are currently available on the server and not full. Packet-Code: LOBOV
 *
 * @author Sebastian Schlachter
 */
public class PacketLobbyOverview extends Packet {

  private String[] in;

  /**
   * Constructor that is used by the Client to build the Packet, after receiving the Command LOBOV.
   *
   * @param data a single String that begins with "OK║" and contains a List of max 10 Lobbies (and
   *     information to them). Each list entry is separated by "║". In the case that an error
   *     occurred before, the String is an errormessage and does not begin with "OK║". The variable
   *     data gets split at the positions of "║". Every substring gets then saved in to the Array
   *     called {@code in}.
   */
  public PacketLobbyOverview(String data) {
    // Client receives
    super(PacketTypes.LOBBY_OVERVIEW);
    setData(data);
    if (data == null) {
      addError("No Data available.");
      return;
    }
    in = getData().split("║");
    validate();
  }

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientId ClientId of the the receiver.
   * @param data A single String that begins with "OK║" and contains a List of max 10 Lobbies (and
   *     information to them). Each list entry is separated by "║". In the case that an error
   *     occurred before the String is an errormessage and does not begin with "OK║". The variable
   *     data gets split at the positions of "║". Every substring gets then saved in to the Array
   *     called {@code in}.
   */
  public PacketLobbyOverview(int clientId, String data) {
    // server builds
    super(PacketTypes.LOBBY_OVERVIEW);
    setClientId(clientId);
    setData(data);
    if (data == null) {
      addError("No Data available.");
      return;
    }
    in = getData().split("║");
    validate();
  }

  /**
   * Validation method to check the data that has, or will be send in this packet. Checks if {@code
   * data} is not null. Checks for every element of the Array {@code in}, that it consists of
   * extendet ASCII Characters. In the case of an error it gets added with {@link
   * Packet#addError(String)}.
   */
  @Override
  public void validate() {
    if (getData() != null) {
      for (String s : in) {
        isExtendedAscii(s);
      }
      if (in.length == 2 && in[1].equals("No open Lobbies")) {
        // No need to validate anything else
        return;
      }
      if (in.length == 2 && in[1].equals("No Lobbies online")) {
        // No need to validate anything else
        return;
      }
      if (in.length > 1 && isInt(in[1])) {

        for (int i = 2; i < in.length; i = i + 3) {
          try {
            if (!isInt(in[i + 1])) {
              addError("Data Format error");
              return;
            }
            if (isExtendedAscii(in[i + 2])) {
              if (!in[i + 2].equals("s") && !in[i + 2].equals("m") && !in[i + 2].equals("l")) {
                addError("Illegal mapsize.");
              }
            }
          } catch (ArrayIndexOutOfBoundsException e) {
            addError("Data Format error");
            return;
          }
        }
      }
    }
  }

  /**
   * Method that lets the Client react to the receiving of this packet. Check for errors in
   * validate. If {@code in[0]} equals "OK" the list of lobbies gets printed. Else in the case of an
   * error only the error message gets printed.
   */
  @Override
  public void processData() {
    if (hasErrors()) {
      /*Do we still need this if statement?*/
    } else if (in[0].equals("OK")) { // the "OK" gets added in PacketCreatLobby.processData and
      CopyOnWriteArrayList<LobbyEntry> catalog = new CopyOnWriteArrayList<LobbyEntry>();
      if (in.length > 2) {
        for (int i = 2; i < in.length; i = i + 3) {
          catalog.add(new LobbyEntry(in[i], in[i + 1], in[i + 2]));
        }
      }
      Game.setLobbyCatalog(catalog);
    } else {
      /*Do we still need this else statement?*/
    }
  }
}
