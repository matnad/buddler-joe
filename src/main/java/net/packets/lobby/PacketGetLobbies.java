package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;

/**
 * A Packet that gets sent from the Client to the Server, to get an Overview of available lobbies.
 * Packet-Code: LOBGE
 *
 * @author Sebastian Schlachter
 */
public class PacketGetLobbies extends Packet {

  /**
   * Constructor that is used by the Server to build the Packet, after receiving the Command
   * "LOBGE".
   *
   * @param clientId ClientId of the Client that has sent the command.
   */
  public PacketGetLobbies(int clientId) {
    // server builds
    super(PacketTypes.GET_LOBBIES);
    setClientId(clientId);
  }

  /**
   * Constructor that will be used by the Client to build the Packet. Which can then be send to the
   * Server. There are no parameters necessary here since the Packet has no real content(only a
   * Type, LOBGE).
   */
  public PacketGetLobbies() {
    // client builds
    super(PacketTypes.GET_LOBBIES);
  }

  /** Dummy method. Since there is no content to validate. */
  @Override
  public void validate() {
    // No data to validate since it is a Empty Packet
  }

  /**
   * Method that lets the Server react to the receiving of this packet. Adds Error if the client
   * that has sent the Packet, is not logged in to the Server. Constructs a {@link
   * PacketLobbyOverview}-Packet that contains either a list of max ten available lobbies or, in the
   * case of an error, a suitable errormessage. If there are no errors "OK" gets added to the String
   * of the {@link PacketLobbyOverview}-Packet Sends the {@link PacketLobbyOverview}-Packet to the
   * client that has send this packet.
   */
  @Override
  public void processData() {
    String info;
    if (!isLoggedIn()) {
      addError("Not loggedin yet");
    }
    if (hasErrors()) {
      info = createErrorMessage();
    } else {
      info = "OK║" + ServerLogic.getLobbyList().getTopTen();
    }
    PacketLobbyOverview p = new PacketLobbyOverview(getClientId(), info);
    p.sendToClient(getClientId());
  }
}
