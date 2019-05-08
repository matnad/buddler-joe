package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontrendering.TextMaster;
import game.Game;
import gui.GuiTexture;
import gui.MenuButton;
import gui.text.ChangableGuiText;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;

/** A simple loading screen with minimal animations and changeable loading message. */
public class GameOver {

  private static List<GuiTexture> guis;
  private static ChangableGuiText gameOver = null;
  private static ChangableGuiText winnerMsg = null;

  private static GuiTexture gameOverScreen;
  private static GuiTexture buddlerJoe;
  private static MenuButton exitGame;

  private static String winnerString;

  /**
   * Preload background and font with settings.
   *
   * @param loader main loader
   */
  public static void init(Loader loader) {
    // GuiTexture loadingScreen =
    //    new GuiTexture(loader.loadTexture("ffffff"), new Vector2f(0, 0), new Vector2f(1, 1), 1);
    gameOverScreen =
        new GuiTexture(
            loader.loadTexture("mainMenuBackground"), new Vector2f(0, 0), new Vector2f(1, 1), 1);
    buddlerJoe =
        new GuiTexture(
            loader.loadTexture("buddlerjoe"),
            new Vector2f(-0.730208f, -0.32963f),
            new Vector2f(0.181771f, 0.67963f),
            1);

    // Exit Game
    exitGame =
        new MenuButton(
            loader,
            "quitWood_norm",
            "quitWood_hover",
            new Vector2f(0.75f, -0.851852f),
            new Vector2f(.097094f, .082347f));
  }

  /**
   * Update the loading screen. Run every frame. Will do the "..." animation and change the text
   * according to the message variable.
   */
  public static void update() {

    InputHandler.update();
    Game.window.update();

    guis = new ArrayList<>();

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    guis.add(gameOverScreen);
    guis.add(buddlerJoe);
    guis.add(exitGame.getHoverTexture(x, y));

    if ((InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && exitGame.isHover(x, y))) {
      Game.restart();

    }

    if (gameOver == null || winnerMsg == null) {
      createTexts();
    }

    Game.getGuiRenderer().render(guis);
    TextMaster.render();
  }

  private static void createTexts() {
    if (gameOver != null) {
      gameOver.delete();
    }
    gameOver = new ChangableGuiText("GAME OVER", new Vector2f(0, 0.4f));
    gameOver.setFontSize(3);
    gameOver.setTextColour(new Vector3f(1, 1, 1));
    gameOver.createGuiText();

    if (winnerMsg != null) {
      winnerMsg.delete();
    }
    winnerMsg = new ChangableGuiText(winnerString, new Vector2f(0, 0.55f));
    winnerMsg.setTextColour(new Vector3f(1, 1, 1));
    winnerMsg.setFontSize(1);
    winnerMsg.createGuiText();
  }

  /**
   * Set the message on the game over screen.
   *
   * @param msg message to display on the game over screen
   */
  public static void setMsg(String msg) {
    gameOver = null;
    winnerString = msg;
  }

  /** Delete the gui elements that no longer need to be rendered when the loading screen is over. */
  public static void done() {
    gameOver.delete();
  }
}
