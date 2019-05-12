package gui;

import org.joml.Vector2f;

/** Simple quad with a texture that can be scaled, faded, textured and positioned. */
public class GuiTexture {

  private final int texture;
  private Vector2f position;
  private Vector2f scale;
  private float alpha;

  /**
   * Create a simple image that is rendered onto the screen.
   *
   * @param texture texture ID as returned by the texture loader
   * @param position Screen Coordinates
   * @param scale 1 = full screen
   * @param alpha [0, 1]. 0 = invisible, 1 = fully visible
   */
  public GuiTexture(int texture, Vector2f position, Vector2f scale, float alpha) {
    this.texture = texture;
    this.position = position;
    this.scale = scale;
    this.alpha = alpha;
  }

  public int getTexture() {
    return texture;
  }

  public Vector2f getPosition() {
    return position;
  }

  public Vector2f getScale() {
    return scale;
  }

  public float getAlpha() {
    return alpha;
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  public void setPosition(Vector2f position) {
    this.position = position;
  }
}
