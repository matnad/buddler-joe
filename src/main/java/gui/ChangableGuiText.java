package gui;

import engine.render.fontrendering.TextMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class ChangableGuiText extends GuiString {


  public ChangableGuiText() {
    super();
    setPosition(new Vector2f(0, 0));
    setTextColour(new Vector3f(1f, 1f, 1f));
    setCentered(true);
    setAlpha(1);
    setFontSize(2);
    setText("");
  }


  public void changeText(String text) {
    if (getGuiText() != null) {
      TextMaster.removeText(getGuiText());
    }
      setText(text);
      createGuiText();
  }

  public void delete() {
    if (getGuiText() != null) {
      TextMaster.removeText(getGuiText());
    }
  }

}
