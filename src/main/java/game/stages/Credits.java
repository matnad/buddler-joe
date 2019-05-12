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
 * credits Menu specification and rendering. Must be initialized. Specifies all the elements in the
 * CreditsMenu . Contains and manages the Game Loop while the credits Menu is active.
 *
 * @author Sebastian Schlachter
 */
public class Credits {

  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;
  private static GuiTexture buddlerJoe;

  private static GuiTexture background;
  private static GuiTexture credits;

  private static MenuButton back;
  private static float startY = -6.5f;
  private static boolean firstloop = true;

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

    credits =
        new GuiTexture(
            loader.loadTexture("creditsTotal"),
            new Vector2f(0, startY),
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
      Vector2f tmp = new Vector2f();
      tmp.set(tmp.x, startY);
      credits.setPosition(tmp);
      firstloop = false;
    }

    List<GuiTexture> guis = new ArrayList<>();
    // add textures here
    guis.add(background);
    guis.add(credits);
    guis.add(buddlerJoe);

    Vector2f tmpPos = credits.getPosition();
    if (tmpPos.y > -startY) {
      tmpPos.set(tmpPos.x, startY);
    } else {
      tmpPos.set(tmpPos.x, tmpPos.y + 0.006214f);
    }
    System.out.println(tmpPos.y);
    credits.setPosition(tmpPos);

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
}
