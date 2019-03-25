package gui;

import engine.render.fontrendering.TextMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Shows FPS as text on screen.
 */
public class Fps extends GuiString {


  /**
   * Create a yellow GuiString in the top right corner ready for rendering.
   */
  public Fps() {
    super();

    setPosition(new Vector2f(.92f, .02f));
    setTextColour(new Vector3f(1f, 1f, 0f));
    setAlpha(1);
  }

  /**
   * To update, we need to re-create the string object as with all text objects.
   *
   * @param fps pass current fps from the window class
   */
  public void updateString(String fps) {
    if (getGuiString() != null) {
      TextMaster.removeText(getGuiString());
    }
    try {
      double fpsD = Double.parseDouble(fps);
      fpsD = Math.round(fpsD);
      setGuiStringString("" + fpsD);
      createGuiText();
    } catch (NumberFormatException ignored) {
      //Don't update if we get invalid string
    }
  }


}
