package net.packets.loginlogout;

import game.Game;
import net.packets.Packet;
import net.packets.lobby.PacketCurLobbyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketUpdateClientId extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketCurLobbyInfo.class);

  private String clientIdString;

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
    }
  }

  /** Client updates his client ID. */
  @Override
  public void processData() {
    if (!hasErrors()) {
      try {
        int id = Integer.parseInt(clientIdString);
        try {
          Game.getActivePlayer().setClientId(id);
        } catch (NullPointerException ignored) {
          // This is a network only client and no game is running, or the game has not loaded yet
        }
      } catch (NumberFormatException e) {
        logger.error(
            "Invalid client ID for current player received from server. ID: " + clientIdString);
      }
    }
  }
}
