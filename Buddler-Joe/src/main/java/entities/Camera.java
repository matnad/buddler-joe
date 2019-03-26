package entities;

import static game.Game.Stage.PLAYING;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_3;

import engine.io.InputHandler;
import engine.io.Window;
import game.Game;
import org.joml.Vector3f;
import util.Maths;

/**
 * The main camera of the game that stays around the player character. It can be - moved (pan):
 * Middle Mouse Button or Arrow Keys - tilted (pitch): Left Mouse Button - turned (yaw): Right Mouse
 * Button - zoomed: Scroll Wheel - reset: R
 *
 * <p>All the movement options have maximum and minimum values to keep the character somewhat in
 * frame. If more than one button is pressed at the same time, the camera can be moved along all
 * selected axes
 *
 * <p>Currently we only have one camera, but support for multiple cameras is definitely possible and
 * relatively easy to accomplish.
 *
 * <p>This object will be used in a lot of places to calculate the transformation of the World
 * Coordinates. See here: {@link Maths#createViewMatrix(Camera)}
 */
public class Camera {

  private static final Vector3f position = new Vector3f(0, 0, 0);
  private static float pitch;
  private static float yaw;
  private final Window window;
  private final float panSpeed = 20;
  private final Player player;
  private float roll; // Not used right now, but we might
  private float offsetX;
  private float offsetY;
  private float offsetZ;

  /**
   * Create a new Camera.
   *
   * @param player player to track
   * @param window window where the camera is used in
   */
  public Camera(Player player, Window window) {
    this.window = window;
    this.player = player;
    resetCam();
  }

  /** Update function. Call this every frame to update position and transformation of camera. */
  public void move() {

    if (Game.getActiveStages().size() == 1 && Game.getActiveStages().get(0) == PLAYING) {

      calculateZoom();
      calculatePitch();
      calculateYaw();
      calculatePan();
      isReset();
    }

    position.z = offsetZ;
    position.x = player.getPosition().x + offsetX;
    position.y = player.getPosition().y + offsetY;
  }

  /** Check if a reset is requested by the player and reset the camera. */
  private void isReset() {
    if (InputHandler.isKeyDown(GLFW_KEY_R)) {
      resetCam();
    }
  }

  /** Reset the camera to the default position and orientation. */
  private void resetCam() {
    pitch = 35;
    yaw = 0;

    offsetZ = 65;
    offsetY = 35;
    offsetX = 0;
  }

  /**
   * Update pan for the current frame. Camera can be panned with the arrow keys or by holding down
   * the middle mouse button and moving the mouse This will update camera position and make sure the
   * pan never goes above the maximum distance from the character
   */
  private void calculatePan() {
    float speed = (float) (panSpeed * window.getFrameTimeSeconds()); // panSpeed is in seconds, so
    // we multiply by frame delta
    if (InputHandler.isKeyDown(GLFW_KEY_LEFT)) {
      offsetX -= speed;
    } else if (InputHandler.isKeyDown(GLFW_KEY_RIGHT)) {
      offsetX += speed;
    } else if (InputHandler.isKeyDown(GLFW_KEY_UP)) {
      offsetY += speed;
    } else if (InputHandler.isKeyDown(GLFW_KEY_DOWN)) {
      offsetY -= speed;
    }

    if (InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_3)) {
      offsetX += (float) (InputHandler.getCursorPosDx() * 0.1f);
      offsetY -= (float) (InputHandler.getCursorPosDy() * 0.1f);
    }

    if (offsetX > 30) {
      offsetX = 30;
    } else if (offsetX < -30) {
      offsetX = -30;
    } else if (offsetY > 50) {
      offsetY = 50;
    } else if (offsetY < -30) {
      offsetY = -30;
    }
  }

  /**
   * Camera can be zoomed in and out by scrolling the mouse wheel. This will update camera position
   * and make sure the zoom never goes above or below the allowed distances
   */
  private void calculateZoom() {
    float zoomLevel = (float) (InputHandler.getMouseScrollY() * 2f);
    offsetZ -= zoomLevel;
    if (offsetZ < 5) {
      offsetZ = 5;
    }
    if (offsetZ > 100) {
      offsetZ = 100;
    }
  }

  /**
   * Camera can be pitched up and down while holding the left mouse button. This will update update
   * and bound the pitch. "Updating" of the position based on pitch is done in {@link
   * Maths#createViewMatrix(Camera)}
   *
   * <p>Pitch is also used for Ray Casting calculations.
   */
  private void calculatePitch() {
    if (InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_1)) {
      float pitchChange = (float) (InputHandler.getCursorPosDy() * 0.2f);
      pitch += pitchChange;
      if (pitch > 60) {
        pitch = 60;
      } else if (pitch < -10) {
        pitch = -10;
      }
    }
  }

  /**
   * Camera can be yawed left and right while holding the right mouse button. This will update
   * update and bound the yaw. "Updating" of the position based on yaw is done in {@link
   * Maths#createViewMatrix(Camera)}
   *
   * <p>Yaw is also used for Ray Casting calculations.
   */
  private void calculateYaw() {
    if (InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_2)) {
      float yawChange = (float) (InputHandler.getCursorPosDx() * 0.2f);
      yaw += yawChange;
      if (yaw < -75) {
        yaw = -75;
      } else if (yaw > 75) {
        yaw = 75;
      }
    }
  }

  public Vector3f getPosition() {
    return position;
  }

  /// **
  // * Maybe as a setting.?
  // */
  // public void invertPitch() {
  //  pitch = -pitch;
  // }

  public float getPitch() {
    return pitch;
  }

  public float getYaw() {
    return yaw;
  }

  public float getRoll() {
    return roll;
  }
}
