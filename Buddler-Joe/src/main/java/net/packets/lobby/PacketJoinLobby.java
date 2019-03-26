package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;
import net.playerhandling.Player;

/**
 * Packet that gets send from the client to the server if he wants to join a lobby. Packet-Code:
 * LOBJO
 *
 * @author Sebastian Schlachter
 */
public class PacketJoinLobby extends Packet {

  private String lobbyname;

  /**
   * Constructor that will be used by the Server to build the Packet, if he receives "LOBJO".
   *
   * @param data The name of the desired lobby.
   * @param clientId ClientId of the client that has sent this packet. {@link
   *     PacketJoinLobby#lobbyname} gets set here, to equal data
   */
  public PacketJoinLobby(int clientId, String data) {
    // server builds
    super(PacketTypes.JOIN_LOBBY);
    setData(data);
    setClientId(clientId);
    lobbyname = getData();
    validate();
  }

  /**
   * Constructor that will be used by the Client to build this Packet.
   *
   * @param data The name of the desired lobby. {@link PacketJoinLobby#lobbyname} gets set here, to
   *     equal data.
   */
  public PacketJoinLobby(String data) {
    // client builds
    super(PacketTypes.JOIN_LOBBY);
    setData(data);
    lobbyname = getData();
    validate();
  }

  /**
   * Check if a {@link PacketJoinLobby#lobbyname} consists of extendet ASCII characters. In the case
   * of an error it gets added with {@link Packet#addError(String)}.
   */
  @Override
  public void validate() {
    isExtendedAscii(lobbyname);
  }

  /**
   * Method that lets the Server react to the receiving of this packet. Check if a lobby with the
   * given lobbyname exists. Check that the Client that has sent the packet is logged in and not in
   * a lobby. In the case of an error it gets added with {@link Packet#addError(String)}. If there
   * are no errors the client gets added to the lobby. Constructs a {@link
   * PacketJoinLobbyStatus}-Packet that contains either "OK" if the join attempt was successful, or
   * in the case of an error, a suitable errormessage. Sends the {@link
   * PacketJoinLobbyStatus}-Packet to the client that tried to join a lobby. If no errors: Creates
   * and sends a {@link PacketLobbyOverview}-Packet to all clients that are not in a Lobby at the
   * moment Creates and sends a {@link PacketCurLobbyInfo}-Packet to all clients that are in the
   * lobby which the sender just joined. (including to the sender himself).
   */
  @Override
  public void processData() {
    String status;
    if (ServerLogic.getLobbyList().getLobbyId(lobbyname) == -1) {
      addError("Chosen lobby does not exist.");
    }
    if (!isLoggedIn()) {
      addError("Not loggedin yet.");
    }
    if (isInALobby()) {
      addError("Already in a lobby, leave current lobby first.");
    }
    if (hasErrors()) {
      status = createErrorMessage();
    } else {
      Player player = ServerLogic.getPlayerList().getPlayer(getClientId());
      int lobbyId = ServerLogic.getLobbyList().getLobbyId(lobbyname);
      status = ServerLogic.getLobbyList().getLobby(lobbyId).addPlayer(player);
      player.setCurLobbyId(lobbyId);
    }
    PacketJoinLobbyStatus p = new PacketJoinLobbyStatus(getClientId(), status);
    p.sendToClient(getClientId());
    if (!hasErrors() && status.equals("OK")) {
      // CurrentLobbyInfo Update jor clients in this lobby
      int lobbyId = ServerLogic.getLobbyList().getLobbyId(lobbyname);
      String info = "OK║" + ServerLogic.getLobbyList().getLobby(lobbyId).getPlayerNames();
      PacketCurLobbyInfo pcli = new PacketCurLobbyInfo(getClientId(), info);
      pcli.sendToLobby(lobbyId);
      // LobbyOverview update jor clients currently not in a Lobby
      info = "OK║" + ServerLogic.getLobbyList().getTopTen();
      PacketLobbyOverview packetLobbyOverview = new PacketLobbyOverview(getClientId(), info);
      packetLobbyOverview.sendToClientsNotInALobby();
    }
  }
}
