package entities.light;

import entities.Entity;
import org.joml.Vector3f;

/**
 * Simple Light Object with position and colour. Used in the shaders to calculate color of objects
 *
 * <p>We will expand on this if time permits!
 */
public class Light {
  private final LightMaster.LightTypes type;
  private Vector3f position;
  private Vector3f colour;
  private float brightness;
  private Vector3f attenuation;
  private boolean destroyed;
  private float distanceSq;
  private Vector3f direction;
  private float cutoff;

  /**
   * POINT LIGHT. Strength of the light depends on distance and angle.
   *
   * @param type type of light
   * @param position world coordinates
   * @param colour r, g, b
   */
  public Light(LightMaster.LightTypes type, Vector3f position, Vector3f colour) {
    this(type, position, colour, new Vector3f(), (float) Math.cos(Math.toRadians(180f)));
  }

  /**
   * SPOT LIGHT. Strength of the light depends on distance and angle.
   *
   * @param type type of light
   * @param position world coordinates
   * @param colour r, g, b
   * @param direction direction vector of the light if it is a spot light
   * @param cutoff cutoff angle for a spotlight (the angle is deviation from the direction, so it
   *     will be doubled).
   */
  public Light(
      LightMaster.LightTypes type,
      Vector3f position,
      Vector3f colour,
      Vector3f direction,
      float cutoff) {
    this.type = type;
    this.position = position;
    this.colour = colour;
    this.brightness = 1;
    this.attenuation = type.getBaseAttenuation();
    this.direction = direction;
    this.cutoff = (float) Math.cos(Math.toRadians(cutoff));
    this.distanceSq = 0;
  }

  /**
   * Calculate distance squared from light to entity.
   *
   * @param entity entity to calculate distance from
   */
  public void update(Entity entity) {
    /*We use distance squared since it is faster and makes no difference. It is used to measure
    which particle is closer to the entity.*/
    distanceSq = position.distanceSquared(entity.getPosition());
  }

  public Vector3f getPosition() {
    return position;
  }

  public void setPosition(Vector3f position) {
    this.position = position;
  }

  public Vector3f getAdjustedColour() {
    return new Vector3f(colour).mul(brightness);
  }

  // public Vector3f getColour() {
  //  return colour;
  // }

  public float getBrightness() {
    return brightness;
  }

  public void setBrightness(float brightness) {
    this.brightness = brightness;
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

  public Vector3f getDirection() {
    return direction;
  }

  public void setDirection(Vector3f direction) {
    this.direction = direction;
  }

  public float getCutoff() {
    return cutoff;
  }

  public void setCutoff(float cutoff) {
    this.cutoff = (float) Math.cos(Math.toRadians(cutoff));
  }
}
