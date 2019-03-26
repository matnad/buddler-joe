package net.packets.lobby;

import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;

/**
 * A Packet that gets send from the Client to the Server, to create a new Lobby. Packet-Code: LOBCR
 *
 * @author Sebastian Schlachter
 */
public class PacketCreateLobby extends Packet {

  private String lobbyname;

  /**
   * Constructor that will be used by the Client to build the Packet. Which can then be send to the
   * Server.
   *
   * @param data The name that the new lobby should have. {@link PacketCreateLobby#lobbyname} gets
   *     set here, to equal {@param data}.
   */
  public PacketCreateLobby(String data) {
    // client builds
    super(PacketTypes.CREATE_LOBBY);
    setData(data);
    lobbyname = getData().trim();
    validate();
  }

  /**
   * Constructor that is used by the Server to build the Packet, after receiving the Command
   * "LOBCR".
   *
   * @param clientId ClientId of the Client that has sent the command.
   * @param data The desired name of the new lobby. {@link PacketCreateLobby#lobbyname} gets set
   *     here, to equal {@param data}.
   */
  public PacketCreateLobby(int clientId, String data) {
    // server builds
    super(PacketTypes.CREATE_LOBBY);
    setClientId(clientId);
    setData(data);
    lobbyname = getData().trim();
    validate();
  }

  /**
   * Check if a {@link PacketCreateLobby#lobbyname} has been sent. Check if {@link
   * PacketCreateLobby#lobbyname} is shorter than 17 characters. Check if {@link
   * PacketCreateLobby#lobbyname} is longer than 3 characters. Check if {@link
   * PacketCreateLobby#lobbyname} consists of extended ASCII characters. In the case of an error it
   * gets added with {@link Packet#addError(String)}.
   */
  @Override
  public void validate() {
    if (lobbyname == null) {
      addError("No lobbyname found.");
      return;
    }
    if (lobbyname.length() > 16) {
      addError("Lobbyname to long. Maximum is 16 Characters.");
    } else if (lobbyname.length() < 4) {
      addError("Lobbyname to short. Minimum is 4 Characters.");
    }
    isExtendedAscii(lobbyname);
  }

  /**
   * Method that lets the Server react to the receiving of this packet. Check that the Client that
   * has sent the packet is logged in and not in a lobby. In the case of an error it gets added with
   * {@link Packet#addError(String)}. If there are no errors a new lobby with the desired name gets
   * created and added to the Lobbylist of the Server. Constructs a {@link
   * PacketCreateLobbyStatus}-Packet that contains either "OK" if the lobby was successfully
   * created, or in the case of an error, a suitable errormessage. Sends the {@link
   * PacketCreateLobbyStatus}-Packet to the client that tried to create a lobby. Creates and sends a
   * {@link PacketLobbyOverview}-Packet to all clients that are not in a Lobby at the moment
   * (including the client that has created the new lobby).
   */
  @Override
  public void processData() {
    if (!isLoggedIn()) {
      addError("Not loggedin yet");
    }
    if (isInALobby()) {
      addError("You are in a lobby, leave the current lobby first");
    }
    String status;
    if (hasErrors()) {
      status = createErrorMessage();
    } else {
      Lobby lobby = new Lobby(lobbyname);
      status = ServerLogic.getLobbyList().addLobby(lobby);
    }
    PacketCreateLobbyStatus pcls = new PacketCreateLobbyStatus(getClientId(), status);
    pcls.sendToClient(getClientId());
    // Creat a LobbyOverview-Packet to be send to all Clients.
    if (!hasErrors() && status.equals("OK")) {
      String info = "OK║" + ServerLogic.getLobbyList().getTopTen();
      PacketLobbyOverview p = new PacketLobbyOverview(getClientId(), info);
      p.sendToClientsNotInALobby();
    }
  }
}
