package gui.text;

import engine.render.fontrendering.TextMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;

/** Shows FPS as text on screen. */
public class Fps extends GuiString {

  /** Create a yellow GuiString in the top right corner ready for rendering. */
  public Fps() {
    super();

    setPosition(new Vector2f(.98f, .055f));
    setTextColour(new Vector3f(.5f, .5f, .5f));
    setFontSize(.5f);
    setAlpha(.6f);
    setText("0");
    createGuiText();
  }

  /**
   * To update, we need to re-create the string object as with all text objects.
   *
   * @param fps pass current fps from the window class
   */
  public void updateString(String fps) {
    if (getGuiText() != null) {
      TextMaster.removeText(getGuiText());
    }
    try {
      setText(fps);
      createGuiText();
    } catch (NumberFormatException ignored) {
      // Don't update if we get invalid string
    }

    // Vector3f pos3 = new Vector3f(Game.getActivePlayer().getPosition());
    // Vector4f pos4 = new Vector4f(pos3.x, pos3.y, pos3.z, 1);
    //
    //// Matrix4f proj = new Matrix4f(1.5f, 0, 0 ,0, 0, 1, 0, 0, 0, 0, -1.2f, -2.2f, 0, 0, -1, 0);
    // MasterRenderer.getProjectionMatrix();
    // Vector4f loc =
    //    new Vector4f(pos4).mul(Maths.createViewMatrix(Game.getActiveCamera()))
    //        .mul(MasterRenderer.getProjectionMatrix());
    //
    // float x = (loc.x / loc.w + 1) / 2f;
    // float y = 1 - (loc.y / loc.w + 1) / 2f;
    //
    //// System.out.println(proj);
    // System.out.println(loc);
    //
    // setPosition(new Vector2f(x, y));
  }
}
