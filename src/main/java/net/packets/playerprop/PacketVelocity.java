package net.packets.playerprop;

import game.NetPlayerMaster;
import net.ServerLogic;
import net.packets.Packet;
import net.playerhandling.ServerPlayer;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketVelocity extends Packet {
  private static final Logger logger = LoggerFactory.getLogger(PacketPos.class);

  private int playerId;
  private float curvX;
  private float curvY;
  private float tarvX;
  private float tarvY;

  /**
   * Client constructs Velocity packet if their velocity changed to inform other clients and the
   * server. This will be validated Server-Side.
   *
   * @param curvX current X velocity
   * @param curvY current Y velocity
   * @param tarvX goal X velocity for interpolation
   * @param tarvY goal Y velocity for interpolation
   */
  public PacketVelocity(float curvX, float curvY, float tarvX, float tarvY) {
    super(PacketTypes.PLAYER_VELOCITY);
    setData(curvX + "║" + curvY + "║" + tarvX + "║" + tarvY);
    // No validation here to save performance
  }

  /**
   * The server receives the packet and adds the client id to the data. This can then be sent to the
   * lobby.
   *
   * @param clientId client ID of the packet sender
   * @param data velocity data received from the client
   */
  public PacketVelocity(int clientId, String data) {
    super(PacketTypes.PLAYER_VELOCITY);
    setClientId(clientId);
    setData(clientId + "║" + data);
    validate(); // And construct variables in one step to save performance.
  }

  /**
   * Client receives a packet from the server that includes a player that changed his velocity and
   * both velocity vectors.
   *
   * @param data client id with velocity data
   */
  public PacketVelocity(String data) {
    super(PacketTypes.PLAYER_VELOCITY);
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
    if (posArray.length != 5) {
      addError("Invalid player in velocity data.");
      return;
    }
    try {
      this.playerId = Integer.parseInt(posArray[0]);
      this.curvX = Float.parseFloat(posArray[1]);
      this.curvY = Float.parseFloat(posArray[2]);
      this.tarvX = Float.parseFloat(posArray[3]);
      this.tarvY = Float.parseFloat(posArray[4]);
    } catch (NumberFormatException e) {
      addError("Invalid velocity data.");
    }
  }

  /**
   * The server will just propagate the packet to the lobby while the client will update the
   * velocity of the respective net player.
   */
  @Override
  public void processData() {
    if (!hasErrors()) {
      if (getClientId() > 0) {
        // Server
        ServerPlayer player = ServerLogic.getPlayerList().getPlayer(getClientId());
        player.setCurrentVelocity2d(new Vector2f(curvX, curvY));
        player.setGoalVelocity2d(new Vector2f(tarvX, tarvY));
        sendToLobby(player.getCurLobbyId());
      } else {
        // Client
        NetPlayerMaster.updateVelocities(playerId, curvX, curvY, tarvX, tarvY);
      }
    } else {
      logger.error("Errors while transmitting velocity data. " + createErrorMessage());
    }
  }
}
