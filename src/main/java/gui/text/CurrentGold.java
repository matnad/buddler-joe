package gui.text;

import game.Game;
import org.joml.Vector2f;
import org.joml.Vector3f;

/** Gui Object to display and update "Gold: (amount)". */
public class CurrentGold {

  private static final Vector2f position = new Vector2f(0.01f, 0.1f);
  private final String prefix = "Gold: ";

  private ChangableGuiText guiText;

  /**
   * Create a text object to display the current gold of the active player. Must be updated every
   * frame or it will not be created and kept up to date.
   */
  public CurrentGold() {
    guiText = new ChangableGuiText();
    guiText.setFontSize(1);
    guiText.setTextColour(new Vector3f(255, 165, 0));
    guiText.setCentered(false);
    guiText.setPosition(position);
  }

  /**
   * Generate the text with prefix and gold amount to be displayed in the game.
   *
   * @return String to be displayed in the game
   */
  private String getGoldText() {
    return prefix + Game.getActivePlayer().getCurrentGold();
  }

  /**
   * If necessary, update the amount of gold owned by the current player. Creates a new GuiString
   * object and replaces the old one.
   */
  public void update() {
    if (!guiText.getText().equals(getGoldText())) {
      guiText.changeText(getGoldText());
    }
  }
}
