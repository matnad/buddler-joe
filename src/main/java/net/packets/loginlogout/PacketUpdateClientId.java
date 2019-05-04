package net.packets.loginlogout;

import game.Game;
import net.packets.Packet;
import net.packets.lobby.PacketCurLobbyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketUpdateClientId extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketUpdateClientId.class);
  private String clientIdString;
  private int clientId;

  /**
   * Constructor when the client receives a PacketUpdateClientId packet from the server.
   *
   * @param data Should contain the clientID as a string
   */
  public PacketUpdateClientId(String data) {
    super(Packet.PacketTypes.UPDATE_CLIENT_ID);
    setData(data);
    this.clientIdString = data;
    validate();
  }

  /**
   * Constructor when the Server creates a PacketUpdateClientId packet to be sent to the client.
   *
   * @param clientId The client to which this particular clientId belongs to
   */
  public PacketUpdateClientId(int clientId) {
    super(Packet.PacketTypes.UPDATE_CLIENT_ID);
    clientIdString = String.valueOf(clientId);
    setData(clientIdString);
    setClientId(clientId);
    validate();
  }

  /** No special validation needed. */
  @Override
  public void validate() {
    if (clientIdString == null) {
      addError("No Status found.");
      return;
    }
    try {
      this.clientId = Integer.parseInt(clientIdString);
    } catch (NumberFormatException e) {
      addError("Invalid client ID for current player received from server. ID: " + clientIdString);
      logger.error(
          "Invalid client ID for current player received from server. ID: " + clientIdString);
    }
  }

  /** Client updates his client ID. */
  @Override
  public void processData() {
    if (!hasErrors()) {
      try {
        Game.getActivePlayer().setClientId(clientId);
      } catch (NullPointerException ignored) {
        addError("Not connected to the server.");
      }
    }
  }
}
