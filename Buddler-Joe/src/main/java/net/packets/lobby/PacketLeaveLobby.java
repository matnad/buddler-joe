package net.packets.lobby;

import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;
import net.playerhandling.Player;

/**
 * Packet that gets send from the client to the server if he wants to leave his current lobby.
 * Packet-Code: LOBLE
 *
 * @author Sebastian Schlachter
 */
public class PacketLeaveLobby extends Packet {

  /**
   * Constructor that will be used by the Server to build the Packet, if he receives "LOBLE".
   *
   * @param clientId ClientId of the client that has sent this packet.
   */
  public PacketLeaveLobby(int clientId) {
    // Server builds
    super(PacketTypes.LEAVE_LOBBY);
    setClientId(clientId);
    validate();
  }

  /**
   * Constructor that will be used by the Client to build the Packet. There are no parameters
   * necessary here, since the Packet has no real content(only a Type, LOBLE).
   */
  public PacketLeaveLobby() {
    // client builds
    super(PacketTypes.LEAVE_LOBBY);
  }

  /** Dummy method. Since there is no content to validate. */
  @Override
  public void validate() {
    // Nothing to validate
  }

  /**
   * Method that lets the Server react to the receiving of this packet. Check that the Client that
   * has sent the packet is logged in and in a lobby. In the case of an error it gets added with
   * {@link Packet#addError(String)}. If there are no errors, the client gets removed from his
   * current lobby. Constructs a {@link PacketLeaveLobbyStatus}-Packet that contains either "OK" if
   * the leave-attempt was successful, or in the case of an error, a suitable errormessage. Sends
   * the {@link PacketLeaveLobbyStatus}-Packet to the client that tried to leave a lobby. If no
   * errors: Creates and sends a {@link PacketLobbyOverview}-Packet to all clients that are not in a
   * Lobby at the moment (including the client that has just left his lobby). Creates and sends a
   * {@link PacketCurLobbyInfo}-Packet to all clients that are in the lobby which the sender just
   * left.
   */
  @Override
  public void processData() {
    String status;
    int lobbyId = -1;
    if (!isLoggedIn()) {
      addError("Not loggedin yet.");
    }
    if (!isInALobby()) {
      addError("You are not in a lobby.");
    }
    if (hasErrors()) {
      status = createErrorMessage();
    } else {
      Player player = ServerLogic.getPlayerList().getPlayer(getClientId());
      lobbyId = player.getCurLobbyId();
      Lobby lobby = ServerLogic.getLobbyList().getLobby(lobbyId);
      status = lobby.removePlayer(getClientId());
      player.setCurLobbyId(0);
    }
    PacketLeaveLobbyStatus packetLeaveLobbyStatus =
        new PacketLeaveLobbyStatus(getClientId(), status);
    packetLeaveLobbyStatus.sendToClient(getClientId());

    if (!hasErrors() && status.equals("OK")) {
      // LobbyOverview Update for clients that are not in a lobby
      String info = "OK║" + ServerLogic.getLobbyList().getTopTen();
      PacketLobbyOverview p = new PacketLobbyOverview(getClientId(), info);
      p.sendToClientsNotInALobby();
      // CurrentLobbyInfo Update for clients in this Lobby.
      //info = "OK║" + ServerLogic.getLobbyList().getLobby(lobbyId).getPlayerNames();
      PacketCurLobbyInfo packetCurLobbyInfo = new PacketCurLobbyInfo(getClientId(), lobbyId);
      packetCurLobbyInfo.sendToLobby(lobbyId);
    }
  }
}
