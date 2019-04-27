package gui.lifestatus;

import engine.render.Loader;
import game.Game;
import gui.GuiTexture;
import org.joml.Vector2f;

public class LifeStatus {

  private GuiTexture heart1;
  private GuiTexture heart2;
  private final float xheart1 = -.95f;
  private final float yheart1 = .88f;
  private final float xheart2 = -.87f;
  private final float yheart2 = .88f;

  /**
   * Initializes life status(displayed hearts), only needs to be called once on game init. An
   * instance of this class contains two GuiTexture objects which are used to visualize the life
   * status.
   *
   * @param loader main loader
   */
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

  /**
   * Called every frame. Returns the player's actual life status.
   *
   * @return number of current lives
   */
  public int checkLifeStatus() {
    if (Game.getActivePlayer().getCurrentLives() == 1) {
      return 1;
    } else if (Game.getActivePlayer().getCurrentLives() <= 0) {
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
