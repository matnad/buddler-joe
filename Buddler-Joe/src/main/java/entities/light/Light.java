package entities.light;

import entities.Camera;
import org.joml.Vector3f;

/**
 * Simple Light Object with position and colour.
 * Used in the shaders to calculate color of objects
 *
 * <p>We will expand on this if time permits!
 */
public class Light {
  private Vector3f position;
  private Vector3f colour;
  private Vector3f attenuation;

  private final LightMaster.LightTypes type;

  private boolean destroyed;
  private float distanceSq;

  /**
   * Strength of the light depends on distance and angle.
   *
   * @param type type of light
   * @param position world coordinates
   * @param colour   r, g, b
   */
  public Light(LightMaster.LightTypes type, Vector3f position, Vector3f colour) {
    this.type = type;
    this.position = position;
    this.colour = colour;
    this.attenuation = type.getBaseAttenuation();
    this.distanceSq = 0;
  }

  /**
   * Calculate distance squared from light to camera.
   *
   * @param camera active camera
   */
  public void update(Camera camera) {
    /*We use distance squared since it is faster and makes no difference. It is used to measure
    which particle is closer to the camera.*/
    distanceSq = position.distanceSquared(camera.getPosition());

  }

  public Vector3f getPosition() {
    return position;
  }

  public Vector3f getColour() {
    return colour;
  }

  public void setPosition(Vector3f position) {
    this.position = position;
  }

  public void setColour(Vector3f colour) {
    this.colour = colour;
  }

  public Vector3f getAttenuation() {
    return attenuation;
  }

  public void setAttenuation(Vector3f attenuation) {
    this.attenuation = attenuation;
  }

  public LightMaster.LightTypes getType() {
    return type;
  }

  boolean isDestroyed() {
    return destroyed;
  }

  public void setDestroyed(boolean destroyed) {
    this.destroyed = destroyed;
  }

  float getDistanceSq() {
    return distanceSq;
  }
}
