package gui.text;

import engine.render.fontrendering.TextMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;

/** A generic, flexible String to be displayed via 2D Screen Coordinates. */
public class ChangableGuiText extends GuiString {

  private long createdAt;
  private float offsetX;
  private float offsetY;

  /**
   * Create an empty GuiString in the center of the screen with default settings. To change any of
   * the parameters, see {@link GuiString}
   *
   * @see GuiString
   */
  public ChangableGuiText() {
    this("", new Vector2f());
  }

  /**
   * Create a GuiString at a position on the screen with default settings. To change any of
   * the parameters, see {@link GuiString}
   *
   * @param text text to display
   * @param position position of the text in normalized device constants [(0,0) .. (1,1)]
   * @see GuiString
   */
  public ChangableGuiText(String text, Vector2f position) {
    super();
    setPosition(position);
    setTextColour(new Vector3f(1f, 1f, 1f));
    setCentered(true);
    setAlpha(1);
    setFontSize(2);
    setText(text);
    createdAt = System.currentTimeMillis();
  }

  /**
   * Changes the displayed text of the object.
   *
   * <p>Only use this method to change the text. Will re-compose the glyphs and re-add the item to
   * the render list.
   *
   * @param text new string to display
   */
  public void changeText(String text) {
    if (getGuiText() != null) {
      TextMaster.removeText(getGuiText());
    }
    setText(text);
    createGuiText();
  }

  /** Delete the Object from the render list. */
  public void delete() {
    if (getGuiText() != null) {
      TextMaster.removeText(getGuiText());
    }
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public float getOffsetX() {
    return offsetX;
  }

  public void setOffsetX(float offsetX) {
    this.offsetX = offsetX;
  }

  public float getOffsetY() {
    return offsetY;
  }

  public void setOffsetY(float offsetY) {
    this.offsetY = offsetY;
  }
}
