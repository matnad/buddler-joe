package net.packets.playerprop;

import game.NetPlayerMaster;
import net.ServerLogic;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketPos extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketPos.class);

  private int playerId;
  private float posX;
  private float posY;
  private float rotY;

  /**
   * Client creates a position update packet to send to the server which will be distributed among
   * all players in the lobby. We don't validate to save some performance. This is sent 60 times per
   * second.
   *
   * @param posX current X position in world coordinates
   * @param posY current Y position in world coordinates
   * @param rotY current rotation around the Y axis
   */
  public PacketPos(float posX, float posY, float rotY) {
    super(PacketTypes.POSITION_UPDATE);
    setData(posX + "║" + posY + "║" + rotY);
    // No validation here to save performance
  }

  /**
   * The server receives the packet and adds the client id to the data. This can then be sent to the
   * lobby.
   *
   * @param clientId client ID of the packet sender
   * @param data position data received from the client
   */
  public PacketPos(int clientId, String data) {
    super(PacketTypes.POSITION_UPDATE);
    setClientId(clientId);
    setData(clientId + "║" + data);
    validate(); // And construct variables in one step to save performance.
  }

  /**
   * Client receives a packet from the server that includes a player that moved and his position
   * data.
   *
   * @param data client id with position data
   */
  public PacketPos(String data) {
    super(PacketTypes.POSITION_UPDATE);
    setData(data);
    validate(); // And construct variables in one step to save performance.
  }

  /** On reception we check if all the data is present and in the correct number format. */
  @Override
  public void validate() {
    if (getData() == null) {
      addError("No position data found.");
      return;
    }
    String[] posArray = getData().split("║");
    if (posArray.length != 4) {
      addError("Invalid position data.");
      return;
    }
    try {
      this.playerId = Integer.parseInt(posArray[0]);
      this.posX = Float.parseFloat(posArray[1]);
      this.posY = Float.parseFloat(posArray[2]);
      this.rotY = Float.parseFloat(posArray[3]);
    } catch (NumberFormatException e) {
      addError("Invalid position data.");
    }
  }

  /**
   * The server will just propagate the packet to the lobby while the client will update the
   * position of the respective net player.
   */
  @Override
  public void processData() {
    if (!hasErrors()) {
      if (getClientId() > 0) {
        // Server
        sendToLobby(ServerLogic.getPlayerList().getPlayer(getClientId()).getCurLobbyId());
      } else {
        // Client
        NetPlayerMaster.updatePosition(playerId, posX, posY, rotY);
      }
    } else {
      logger.error("Errors while transmitting position data. " + createErrorMessage());
    }
  }
}
