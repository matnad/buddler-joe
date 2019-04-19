package gui.lifeStatus;

import engine.render.Loader;
import engine.render.fontmeshcreator.GuiText;
import game.Game;
import gui.GuiTexture;
import org.joml.Vector2f;

public class LifeStatus {

  private GuiTexture heart1;
  private GuiTexture heart2;
  private final float xheart1 = -.95f;
  private final float yheart1 = .88f;
  private final float xheart2 = -.5f;
  private final float yheart2 = .5f;

  /** @param loader main loader */
  public LifeStatus(Loader loader) {

    // Loads the heart to screen and set rendering parameters
    heart1 =
        new GuiTexture(
            loader.loadTexture("heart"),
            new Vector2f(xheart1, yheart1),
            new Vector2f(.04f, .07f),
            1);
    heart2 =
        new GuiTexture(
            loader.loadTexture("heart"),
            new Vector2f(xheart2, yheart2),
            new Vector2f(.04f, .07f),
            1);
  }

  /** Called every frame. Reads chat input and toggles chat window text input handler */
  public int checkInputs() {
      if ( Game.getActivePlayer().getCurrentLives() == 1) {
          return 1;
      } else if (Game.getActivePlayer().getCurrentLives() == 0) {
          return 0;
      } else {
          return 2;
      }
  }

  public GuiTexture[] getLifeStatusGui() {
      GuiTexture[] guiTextures = {heart1, heart2};
      return guiTextures;
  }
}
