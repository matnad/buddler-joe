package gui.text;

import game.Game;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CurrentGold {

  private static final Vector2f position = new Vector2f(0.01f, 0.1f);
  private final String prefix = "Gold: ";

  private ChangableGuiText guiText;

  public CurrentGold() {
    guiText = new ChangableGuiText();
    guiText.setFontSize(1);
    guiText.setTextColour(new Vector3f(255, 165, 0));
    guiText.setCentered(false);
    guiText.setPosition(position);
  }

  private String getGoldText() {
    return prefix + Game.getActivePlayer().getCurrentGold();
  }

  public void update() {
    if (!guiText.getText().equals(getGoldText())) {
      guiText.changeText(getGoldText());
    }
  }
}
