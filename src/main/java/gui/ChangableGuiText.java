package gui;

import engine.render.fontrendering.TextMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;

/** A generic, flexible String to be displayed via 2D Screen Coordinates. */
public class ChangableGuiText extends GuiString {

  /**
   * Create an empty GuiString in the center of the screen with default settings. To change any of
   * the parameters, see {@link GuiString}
   *
   * @see GuiString
   */
  public ChangableGuiText() {
    super();
    setPosition(new Vector2f(0, 0));
    setTextColour(new Vector3f(1f, 1f, 1f));
    setCentered(true);
    setAlpha(1);
    setFontSize(2);
    setText("");
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
}
