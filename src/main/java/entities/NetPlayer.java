package entities;

import engine.models.TexturedModel;
import gui.DirectionalUsername;
import org.joml.Vector3f;

/**
 * This will be reworked very shortly so I will not fully document it. A LOT will change and it is
 * not really used now. Ignore the class please.
 */
public class NetPlayer extends Entity {

  private int clientId;
  private String username;
  //private DirectionalUsername directionalUsername;

  /**
   * Create a net player.
   *
   * <p>REWORK IN PROGRESS
   *
   * @param clientId client id is given out by server
   * @param playerModel model
   * @param position 3D world coords
   * @param rotX rotation x axis
   * @param rotY rotation y axis
   * @param rotZ rotation z axis
   * @param scale scale factor
   * @param username username of net player
   */
  public NetPlayer(
      int clientId,
      String username,
      TexturedModel playerModel,
      Vector3f position,
      float rotX,
      float rotY,
      float rotZ,
      float scale) {
    super(playerModel, position, rotX, rotY, rotZ, scale);

    this.clientId = clientId;
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }
}
