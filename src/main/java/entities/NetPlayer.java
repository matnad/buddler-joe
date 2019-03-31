package entities;

import engine.models.TexturedModel;
import entities.light.Light;
import entities.light.LightMaster;
import org.joml.Rayf;
import org.joml.Vector3f;

/**
 * This will be reworked very shortly so I will not fully document it. A LOT will change and it is
 * not really used now. Ignore the class please.
 */
public class NetPlayer extends Entity {

  private int clientId;
  private String username;
  private Light headlight;
  // private DirectionalUsername directionalUsername;

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
    headlight =
        LightMaster.generateLight(
            LightMaster.LightTypes.SPOT,
            getHeadlightPosition(),
            new Vector3f(1f, 244 / 255f, 229 / 255f).mul(1));
    headlight.setCutoff(35f);
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

  private Vector3f getHeadlightPosition() {
    return new Vector3f(getPosition()).add(.3f, 4, 0);
  }

  private void updateHeadlightPosition() {
    headlight.setPosition(getHeadlightPosition());
  }

  private void updateHeadlightDirection() {
    Vector3f direction = new Vector3f(0, 0, 1).rotateY((float) Math.toRadians(getRotY()));
    headlight.setDirection(direction);
  }

  @Override
  public void setRotY(float rotY) {
    super.setRotY(rotY);
    updateHeadlightDirection();
  }

  @Override
  public void increaseRotation(float dx, float dy, float dz) {
    super.increaseRotation(dx, dy, dz);
    updateHeadlightDirection();
  }

  @Override
  public void increaseRotation(Vector3f spin) {
    increaseRotation(spin.x, spin.y, spin.z);
    updateHeadlightDirection();
  }
}
