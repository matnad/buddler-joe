package net.packets.lobby;

import game.Game;
import game.NetPlayerMaster;
import java.util.ArrayList;
import java.util.Arrays;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A packed that is send from the server to the client, which contains the names of all clients that
 * are in the lobby. Packet-Code: LOBCI
 *
 * @author Sebastian Schlachter
 */
public class PacketCurLobbyInfo extends Packet {

  private String info;
  private String[] infoArray;

  private static final Logger logger = LoggerFactory.getLogger(PacketCurLobbyInfo.class);

  /**
   * Constructor that is used by the Server to build the Packet with a lobby id.
   *
   * @param clientId clientId of the the receiver.
   * @param lobbyId the lobby id to build the info string for
   */
  public PacketCurLobbyInfo(int clientId, int lobbyId) {
    // server builds
    super(PacketTypes.CUR_LOBBY_INFO);
    setClientId(clientId);
    Lobby lobby = ServerLogic.getLobbyList().getLobby(lobbyId);
    if (lobby == null) {
      addError("Lobby doesn't exist");
      setData(createErrorMessage());
      return;
    }
    info = "OK║" + lobby.getLobbyName() + "║" + lobby.getPlayerNamesAndIds();
    setData(info);
    infoArray = info.split("║"); // necessary since infoArray is not really used on the Server side,
    // but needed in validate
    validate();
  }

  /**
   * Constructor that is used by the Server to build the Packet with an error string.
   *
   * @param clientId clientId of the the receiver.
   * @param data A single String that is an errormessage and does not begin with "OK║". {@link
   *     PacketCurLobbyInfo#info} gets set to equal data.
   */
  public PacketCurLobbyInfo(int clientId, String data) {
    // server builds
    super(PacketTypes.CUR_LOBBY_INFO);
    setClientId(clientId);
    setData(data);
    info = getData();
    infoArray = new String[0]; // necessary since infoArray is not really used on the Server side,
    // but needed in validate
    validate();
  }

  /**
   * Constructor that is used by the Client to build the Packet, after receiving the Command LOBCI.
   *
   * @param data A single String that begins with "OK║" and contains the names of all clients that
   *     are in the lobby of the receiver. (names are separated by "║") In the case that an error
   *     occurred before, the String is an errormessage and does not begin with "OK║". {@link
   *     PacketCurLobbyInfo#info} gets set to equal data. The variable data gets split at the
   *     positions of "║". Every substring gets then saved in to the Array {@code infoArray}.
   */
  public PacketCurLobbyInfo(String data) {
    // client builds
    super(PacketTypes.CUR_LOBBY_INFO);
    setData(data);
    info = getData();
    infoArray = data.split("║");
    validate();
  }

  /**
   * Validation method to check the data that has, or will be send in this packet. Checks if {@code
   * data} is not null. Checks for every element of the Array {@code infoArray}, that it consists of
   * extendet ASCII Characters. In the case of an error it gets added with {@link
   * Packet#addError(String)}.
   */
  @Override
  public void validate() {
    if (info != null) {
      for (String s : infoArray) {
        isExtendedAscii(s);
      }
    } else {
      addError("No Status found.");
    }
  }

  /**
   * Method that lets the Client react to the receiving of this packet. Check for errors in
   * validate. If {@code in[0]} equals "OK" the names of the clients get printed. Else in the case
   * of an error only the error message gets printed.
   */
  @Override
  public void processData() {
    if (hasErrors()) { // Errors ClientSide
      String s = createErrorMessage();
      System.out.println(s);
    } else if (infoArray[0].equals("OK")) { // No Errors ServerSide
      System.out.println("-------------------------------------");
      System.out.println("Players in Lobby \"" + infoArray[1] + "\":");
      for (int i = 2; i < infoArray.length; i += 2) {
        System.out.println(infoArray[i] + " - " + infoArray[i + 1]);
      }
      System.out.println("-------------------------------------");
      System.out.println("To chat with players in this lobby, type: C <message>");
      System.out.println("To leave this lobby, type: leave");

      // Game Logic updates

      // Add missing players and create list of present players
      NetPlayerMaster.setLobbyname(infoArray[1]);
      ArrayList<Integer> presentIds = new ArrayList<>();
      for (int i = 2; i < infoArray.length; i += 2) {
        try {
          int id = Integer.parseInt(infoArray[i]);
          if (id == Game.getActivePlayer().getClientId()) {
            continue;
          }
          presentIds.add(id);
          NetPlayerMaster.addPlayer(id, infoArray[i + 1]);
        } catch (NumberFormatException e) {
          logger.error(
              "Invalid client ID received from server. ID: "
                  + infoArray[i]
                  + ", Username: "
                  + infoArray[i + 1]);
        } catch (NullPointerException ignored) {
          // This is a network only client and no game is running, or the game has not loaded yet
        }
      }
      // Check if we need to remove a player
      if (presentIds.size() < NetPlayerMaster.getIds().size()) {
        NetPlayerMaster.removeMissing(presentIds);
      }

    } else { // Errors ServerSide
      System.out.println(infoArray[0]);
    }
  }
}
