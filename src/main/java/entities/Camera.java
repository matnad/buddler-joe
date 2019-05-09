package entities;

import static game.Game.Stage.PLAYING;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

import engine.io.InputHandler;
import engine.render.Loader;
import game.Game;
import gui.GuiTexture;
import gui.tutorial.Tutorial;
import java.util.ArrayList;
import org.joml.Vector2f;
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

  protected static Vector3f position = new Vector3f(0, 0, 0);
  private static float pitch;
  private static float yaw;
  private final Player player;
  protected float panSpeed = 20;
  protected float offsetX;
  protected float offsetY;
  protected float offsetZ;
  protected float minZoom = 5;
  protected float maxZoom = 100;
  protected float maxOffsetX = 30;
  protected float maxOffsetY = 50;
  protected float maxPitch = 60;
  protected float minPitch = -10;
  private float roll; // Not used right now, but we might

  protected boolean intro = true;
  private float introTimer;
  private static final float INTRO_START = 4;
  private static final float INTRO_DURATION = 7;
  private Vector3f camIncrement = new Vector3f();
  private float pitchIncrement;
  private float yawIncrement;

  private GuiTexture blackscreen;
  private GuiTexture logo;

  /**
   * Create a new Camera.
   *
   * @param player player to track
   */
  public Camera(Player player, Loader loader) {
    this.player = player;
    resetCam();

    if (loader != null) {
      blackscreen =
          new GuiTexture(loader.loadTexture("black"), new Vector2f(0, 0), new Vector2f(1, 1), 1);
      logo =
          new GuiTexture(
              loader.loadTexture("yellow"), new Vector2f(0, 0), new Vector2f(.3f, .3f), 1);
    } else {
      intro = false;
    }
  }

  /** Update function. Call this every frame to update position and transformation of camera. */
  public void move() {
    if (intro) {
      ArrayList<GuiTexture> guis = new ArrayList<>();
      if (introTimer <= 0) {
        Vector3f camTargetPos = new Vector3f(Game.getActivePlayer().getPosition()).add(0, 35, 0);
        camTargetPos.z = 65;
        pitch = 0;
        pitchIncrement = 35 / INTRO_DURATION;
        // Setup
        float mapWidth = Game.getMap().getWidth() * 6;
        if (Game.getActivePlayer().getPosition().x > (mapWidth / 2f)) {
          position = new Vector3f(-30, -20, 150);
          yaw = 45;
        } else {
          position = new Vector3f(mapWidth + 30, -20, 150);
          yaw = -45;
        }
        yawIncrement = -yaw / INTRO_DURATION;
        Vector3f camDirection = new Vector3f();
        camTargetPos.sub(position, camDirection);
        camDirection.div(INTRO_DURATION, camIncrement);
      }
      introTimer += Game.dt();
      if (introTimer <= INTRO_START) {
        guis.add(blackscreen);
        float alpha = Math.min(1, (INTRO_START + 1.1f - introTimer) / 2);
        blackscreen.setAlpha(alpha);
        logo.setAlpha(alpha);
      } else {
        position.add(new Vector3f(camIncrement).mul((float) Game.dt()));
        pitch += pitchIncrement * Game.dt();
        yaw += yawIncrement * Game.dt();
        if (introTimer >= INTRO_START + INTRO_DURATION) {
          resetCam();
          intro = false;
        }
      }
      guis.add(logo);
      logo.setAlpha(Math.min(1, (INTRO_START + 4 - introTimer) / 4));
      Game.getGuiRenderer().render(guis);
    } else {
      if (Game.getActiveStages().size() == 1 && Game.getActiveStages().get(0) == PLAYING) {
        calculateZoom();
        calculatePitch();
        calculateYaw();
        calculatePan();
        isReset();
      }
      correctCameraPos(); // Follow player for example
    }
  }

  protected void correctCameraPos() {
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
    float speed = (float) (panSpeed * Game.dt()); // panSpeed is in seconds, so
    // we multiply by frame delta
    if (InputHandler.isKeyDown(GLFW_KEY_LEFT)) {
      Tutorial.Topics.CAMERA.stopTopic();
      offsetX -= speed;
    } else if (InputHandler.isKeyDown(GLFW_KEY_RIGHT)) {
      Tutorial.Topics.CAMERA.stopTopic();
      offsetX += speed;
    } else if (InputHandler.isKeyDown(GLFW_KEY_UP)) {
      Tutorial.Topics.CAMERA.stopTopic();
      offsetY += speed;
    } else if (InputHandler.isKeyDown(GLFW_KEY_DOWN)) {
      Tutorial.Topics.CAMERA.stopTopic();
      offsetY -= speed;
    }

    if (InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_1)) {
      offsetX += (float) (InputHandler.getCursorPosDx() * 0.1f);
      offsetY -= (float) (InputHandler.getCursorPosDy() * 0.1f);
      if (offsetX > 0.02) {
        Tutorial.Topics.CAMERA.stopTopic();
      }
    }

    if (offsetX > maxOffsetX) {
      offsetX = maxOffsetX;
    } else if (offsetX < -maxOffsetX) {
      offsetX = -maxOffsetX;
    } else if (offsetY > maxOffsetY) {
      offsetY = maxOffsetY;
    } else if (offsetY < -maxOffsetY) {
      offsetY = -maxOffsetY;
    }
  }

  /**
   * Camera can be zoomed in and out by scrolling the mouse wheel. This will update camera position
   * and make sure the zoom never goes above or below the allowed distances
   */
  private void calculateZoom() {
    float zoomLevel = (float) (InputHandler.getMouseScrollY() * 2f);
    offsetZ -= zoomLevel;
    if (offsetZ < minZoom) {
      offsetZ = minZoom;
    }
    if (offsetZ > maxZoom) {
      offsetZ = maxZoom;
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
    if (InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_2)) {
      float pitchChange = (float) (InputHandler.getCursorPosDy() * 0.2f);
      pitch += pitchChange;
      if (pitch > maxPitch) {
        pitch = maxPitch;
      } else if (pitch < minPitch) {
        pitch = minPitch;
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

  public boolean isIntro() {
    return intro;
  }
}
