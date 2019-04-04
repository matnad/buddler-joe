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

public class InLobby {
  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;

  private static GuiTexture background;
  private static GuiTexture inLobby;

  private static MenuButton leave;

  private static MenuButton ready;

  /**
   * Initialisation of the textures for this GUI-menu.
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

    inLobby =
        new GuiTexture(
            loader.loadTexture("inLobbyTable"),
            new Vector2f(0, -0.040741f),
            new Vector2f(0.554167f, 0.757804f),
            1);

    leave =
        new MenuButton(
            loader,
            "leave_norm",
            "leave_hover",
            new Vector2f(-0.107511f, -0.9f),
            new Vector2f(.097094f, .082347f));

    ready =
        new MenuButton(
            loader,
            "ready_norm",
            "ready_hover",
            new Vector2f(0.107511f, -0.9f),
            new Vector2f(.097094f, .082347f));
  }

  /** Updates the GUI every cycle. */
  @SuppressWarnings("Duplicates")
  public static void update() {
    List<GuiTexture> guis = new ArrayList<>();
    // add textures here
    guis.add(background);
    guis.add(inLobby);

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    // add buttons here
    guis.add(leave.getHoverTexture(x, y));

    guis.add(ready.getHoverTexture(x, y));

    if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && leave.isHover(x, y)) {
      // TODO trigger Lobbyleave
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && ready.isHover(x, y)) {
      // TODO trigger Playerready
    } else if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)) {
      // TODO: Removce this part
      Game.addActiveStage(Game.Stage.MAINMENU);
      Game.removeActiveStage(Game.Stage.INLOBBBY);
    }

    Game.getGuiRenderer().render(guis);
  }
}
