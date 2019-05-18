package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.render.Loader;
import game.Game;
import gui.GuiTexture;
import gui.MenuButton;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;

/**
 * creditsOne Menu specification and rendering. Must be initialized. Specifies all the elements in
 * the CreditsMenu . Contains and manages the Game Loop while the creditsOne Menu is active.
 *
 * @author Sebastian Schlachter
 */
public class Credits {

  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;
  private static GuiTexture buddlerJoe;

  private static GuiTexture background;
  private static GuiTexture creditsOne;
  private static GuiTexture creditsTwo;

  private static MenuButton back;
  private static float startY = -6.5f;
  private static float offsetY = -10.5f;
  private static boolean firstloop = true;
  private static int delay = 180;

  /**
   * Initializes the textures for this GUI-menu.
   *
   * @param loader main loader
   */
  @SuppressWarnings("Duplicates")
  public static void init(Loader loader) {

    currentAlpha = 1;

    // Background
    background =
        new GuiTexture(
            loader.loadTexture("mainMenuBackground"), new Vector2f(0, 0), new Vector2f(1, 1), 1);

    buddlerJoe =
        new GuiTexture(
            loader.loadTexture("buddlerjoe"),
            new Vector2f(-0.730208f, -0.32963f),
            new Vector2f(0.181771f, 0.67963f),
            1);

    creditsOne =
        new GuiTexture(
            loader.loadTexture("creditsPart1"),
            new Vector2f(0, startY),
            new Vector2f(0.78125f, 5.09259f),
            1);

    creditsTwo =
        new GuiTexture(
            loader.loadTexture("creditsPart2"),
            new Vector2f(0, startY + offsetY),
            new Vector2f(0.78125f, 5.09259f),
            1);

    // Back
    back =
        new MenuButton(
            loader,
            "back_norm",
            "back_hover",
            new Vector2f(0.75f, -0.851852f),
            new Vector2f(.097094f, .082347f));
  }

  /** Updates the GUI every cycle. */
  @SuppressWarnings("Duplicates")
  public static void update() {
    if (firstloop) {
      rewind();
      firstloop = false;
    }

    List<GuiTexture> guis = new ArrayList<>();
    // add textures here
    guis.add(background);
    guis.add(creditsOne);
    guis.add(creditsTwo);
    guis.add(buddlerJoe);

    Vector2f tmpPosOne = creditsOne.getPosition();
    Vector2f tmpPosTwo = creditsTwo.getPosition();
    if (tmpPosTwo.y > -startY - 2.2f) {
      if (delay >= 0) {
        delay--;
      } else {
        if (creditsTwo.getAlpha() - 0.008333f > 0) {
          creditsTwo.setAlpha(creditsTwo.getAlpha() - 0.008333f);
        } else {
          rewind();
        }
      }
    } else {
      tmpPosOne.set(tmpPosOne.x, tmpPosOne.y + 0.006214f);
      tmpPosTwo.set(tmpPosTwo.x, tmpPosTwo.y + 0.006214f);
      creditsOne.setPosition(tmpPosOne);
      creditsTwo.setPosition(tmpPosTwo);
    }

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    // add buttons here
    guis.add(back.getHoverTexture(x, y));

    if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
        || InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
      done();
      Game.addActiveStage(Game.Stage.MAINMENU);
      Game.removeActiveStage(Game.Stage.CREDITS);
    }

    Game.getGuiRenderer().render(guis);
  }

  /** resets variables. */
  public static void done() {
    firstloop = true;
  }

  /** Rewinds the credits to there starting position. */
  private static void rewind() {
    Vector2f tmp1 = new Vector2f();
    tmp1.set(tmp1.x, startY);
    creditsOne.setPosition(tmp1);
    Vector2f tmp2 = new Vector2f();
    tmp2.set(tmp2.x, startY + offsetY);
    creditsTwo.setPosition(tmp2);
    creditsTwo.setAlpha(1);
    delay = 180;
  }
}
