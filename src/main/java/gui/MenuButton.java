package gui;

import engine.render.Loader;
import org.joml.Vector2f;

public class MenuButton {

  private final GuiTexture stateStatic;
  private final GuiTexture stateHover;

  private float minX;
  private float minY;
  private float maxX;
  private float maxY;
  private float alpha;
  private float activationMinAlpha;

  /**
   * Generate a Menu Button that keeps track of its position and size. Has a texture for hovered and
   * not hovered and can tell if it is being hovered. Has a value alpha that defines the Buttons
   * transparency. If alpha is smaller than activationMinAlpha the button can not be hovered.
   * Standard values: alpha = 1 and activationMinAlpha = 0.
   *
   * @param loader main loader
   * @param fileUp file name of texture while not hovering
   * @param fileDown file name of texture while hovering
   * @param position position on the screen
   * @param scale 1 = full screen
   */
  public MenuButton(
      Loader loader, String fileUp, String fileDown, Vector2f position, Vector2f scale) {
    stateStatic = new GuiTexture(loader.loadTexture(fileUp), position, scale, 1);
    stateHover = new GuiTexture(loader.loadTexture(fileDown), position, scale, 1);
    minX = position.x - scale.x;
    minY = position.y - scale.y;
    maxX = position.x + scale.x;
    maxY = position.y + scale.y;
    setAlpha(1);
    activationMinAlpha = 0f;
  }

  /**
   * Get the current texture of the button for the renderer, depending on hover status.
   *
   * @param mouseX current mouse X position
   * @param mouseY current mouse Y position
   * @return hover or non-hover texture
   */
  public GuiTexture getHoverTexture(double mouseX, double mouseY) {
    if (isHover(mouseX, mouseY)) {
      return stateHover;
    } else {
      return stateStatic;
    }
  }

  /**
   * Returns true if the mouse is over the button.
   *
   * @param mouseX current mouse X position
   * @param mouseY current mouse Y position
   * @return true if the mouse is over the button
   */
  public boolean isHover(double mouseX, double mouseY) {
    return (mouseX > minX && mouseX < maxX) && (mouseY > minY && mouseY < maxY) && isActive();
  }

  /**
   * Sets the alpha value for this button.
   *
   * @param alpha the new value for alpha.
   */
  public void setAlpha(float alpha) {
    stateStatic.setAlpha(alpha);
    stateHover.setAlpha(alpha);
    this.alpha = alpha;
  }

  /** Returns true if the Button is active, based on his current alpha value. */
  private boolean isActive() {
    return alpha >= activationMinAlpha;
  }

  public void setActivationMinAlpha(float activationMinAlpha) {
    this.activationMinAlpha = activationMinAlpha;
  }
}
