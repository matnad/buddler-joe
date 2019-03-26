package entities;

import engine.models.TexturedModel;
import gui.DirectionalUsername;
import java.net.InetAddress;
import org.joml.Vector3f;

/**
 * This will be reworked very shortly so I will not fully document it. A LOT will change and it is
 * not really used now. Ignore the class please.
 */
public class NetPlayer extends Entity {

  private InetAddress ipAddress;
  private int port;
  private String username;
  // private String model;
  // private String texture;
  // private float modelSize;

  private DirectionalUsername directionalUsername;

  /**
   * Create a net player.
   *
   * <p>THIS WILL SEE A MAJOR REWORK!
   *
   * @param playerModel model
   * @param position 3D world coords
   * @param rotX rotation x axis
   * @param rotY rotation y axis
   * @param rotZ rotation z axis
   * @param scale scale factor
   * @param inetAddress ip address of net player
   * @param port port of net player
   * @param username username of net player
   * @param strModel model file name
   * @param texture texture file name
   * @param modelSize scale factor of model
   */
  NetPlayer(
      TexturedModel playerModel,
      Vector3f position,
      float rotX,
      float rotY,
      float rotZ,
      float scale,
      InetAddress inetAddress,
      int port,
      String username,
      String strModel,
      String texture,
      float modelSize) {
    super(playerModel, position, rotX, rotY, rotZ, modelSize);

    this.ipAddress = inetAddress;
    this.port = port;
    this.username = username;
    // this.model = strModel;
    // this.texture = texture;
    // this.modelSize = modelSize;

  }

  public InetAddress getIpAddress() {
    return ipAddress;
  }

  public int getPort() {
    return port;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public boolean equals(Object obj) {
    if (!obj.getClass().isInstance(this)) {
      return false;
    }

    return (this.ipAddress == ((NetPlayer) obj).getIpAddress()
        && this.port == ((NetPlayer) obj).getPort());
  }

  // public String getModelStr() {
  //  return model;
  // }
  //
  // public String getTextureStr() {
  //  return texture;
  // }
  //
  // public float getModelSize() {
  //  return modelSize;
  // }
  //
  // public void loadDirectionalUsername() {
  //  this.directionalUsername = new DirectionalUsername(this);
  // }

  public DirectionalUsername getDirectionalUsername() {
    return directionalUsername;
  }
}
