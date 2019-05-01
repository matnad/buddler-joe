package entities;

import engine.io.Window;
import org.joml.Vector3f;

public class SpectatorCamera extends Camera {

  private Vector3f anchor;

  /**
   * Create a new Spectator Camera around (0,0,0).
   *
   * @param window window where the camera is used in
   */
  public SpectatorCamera(Window window) {
    this(window, new Vector3f());
  }

  /**
   * Create a new Spectator Camera centered on an anchor.
   *
   * @param window window where the camera is used in
   * @param anchor coordinates where the camera is anchored
   */
  public SpectatorCamera(Window window, Vector3f anchor) {
    super(null, window);
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
