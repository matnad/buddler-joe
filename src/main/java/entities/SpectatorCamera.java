package entities;

import engine.io.Window;
import engine.render.Loader;
import org.joml.Vector3f;

public class SpectatorCamera extends Camera {

  private Vector3f anchor;

  /**
   * Create a new Spectator Camera around (0,0,0).
   *
   */
  public SpectatorCamera() {
    this(new Vector3f());
  }

  /**
   * Create a new Spectator Camera centered on an anchor.
   *
   * @param anchor coordinates where the camera is anchored
   */
  public SpectatorCamera(Vector3f anchor) {
    super(null, null);
    intro = false;
    this.anchor = anchor;
    // "Unlock" camera
    maxZoom = 150;
    maxOffsetX = 500;
    maxOffsetY = 500;
    panSpeed = 30;
  }

  @Override
  protected void correctCameraPos() {
    position.z = offsetZ;
    position.x = anchor.x + offsetX;
    position.y = anchor.y + offsetY;
  }
}
