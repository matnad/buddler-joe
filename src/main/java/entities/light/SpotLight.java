package entities.light;

import org.joml.Vector3f;

public class SpotLight extends Light {

  private Vector3f direction;
  private float cutoff;

  /**
   * Strength of the light depends on distance and angle.
   *
   * @param position world coordinates
   * @param colour   r, g, b
   */
  public SpotLight(Vector3f position, Vector3f colour, Vector3f direction, float cutoff) {
    super(LightMaster.LightTypes.SPOT, position, colour);
    this.direction = direction;
    this.cutoff = cutoff;

  }



}
