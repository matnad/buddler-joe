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

public class Options {
  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;

  private static GuiTexture background;
  private static GuiTexture options;
  private static GuiTexture buddlerJoe;

  private static MenuButton back;
  private static MenuButton r2160;
  private static MenuButton r1440;
  private static MenuButton r1080;
  private static MenuButton r720;
  private static MenuButton r480;
  private static MenuButton fullscreen;

  /**
   * * Initialize Options-Menu. Will load the texture files and generate the basic menu parts. This
   * needs to be called once before using the menu.
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

    options =
        new GuiTexture(
            loader.loadTexture("options_placeholder"),
            new Vector2f(0, 0),
            new Vector2f(0.5f, 0.5f),
            1);

    float buttonWidht = .035521f;
    float buttonHight = .048333f;

    r2160 =
        new MenuButton(
            loader,
            "r2160_placeholder",
            "r2160_placeholder",
            new Vector2f(0, 0.1f),
            new Vector2f(buttonWidht, buttonHight));

    r1440 =
        new MenuButton(
            loader,
            "r1440_placeholder",
            "r1440_placeholder",
            new Vector2f(0, 0),
            new Vector2f(buttonWidht, buttonHight));

    r1080 =
        new MenuButton(
            loader,
            "r1080_placeholder",
            "r1080_placeholder",
            new Vector2f(0, -0.1f),
            new Vector2f(buttonWidht, buttonHight));

    r720 =
        new MenuButton(
            loader,
            "r720_placeholder",
            "r720_placeholder",
            new Vector2f(0, -0.2f),
            new Vector2f(buttonWidht, buttonHight));
    r480 =
        new MenuButton(
            loader,
            "r480_placeholder",
            "r480_placeholder",
            new Vector2f(0, -0.3f),
            new Vector2f(buttonWidht, buttonHight));
    fullscreen =
        new MenuButton(
            loader,
            "toggleFullscreen_placeholder",
            "toggleFullscreen_placeholder",
            new Vector2f(0, -0.45f),
            new Vector2f(buttonWidht, buttonHight));

    // Back
    back =
        new MenuButton(
            loader,
            "back_norm",
            "back_hover",
            new Vector2f(0.75f, -0.851852f),
            new Vector2f(.097094f, .082347f));
  }

  /**
   * Game Loop while the stage is active. This runs every frame as long as the Main Menu is active.
   * Include all rendering and input handling here.
   */
  @SuppressWarnings("Duplicates")
  public static void update() {
    List<GuiTexture> guis = new ArrayList<>();
    // add textures here
    guis.add(background);
    guis.add(options);
    guis.add(buddlerJoe);

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    // add buttons here
    guis.add(back.getHoverTexture(x, y));
    guis.add(r2160.getHoverTexture(x, y));
    guis.add(r1440.getHoverTexture(x, y));
    guis.add(r1080.getHoverTexture(x, y));
    guis.add(r720.getHoverTexture(x, y));
    guis.add(r480.getHoverTexture(x, y));
    guis.add(fullscreen.getHoverTexture(x, y));

    if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
        || InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
      Game.addActiveStage(Game.Stage.MAINMENU);
      Game.removeActiveStage(Game.Stage.OPTIONS);
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && r2160.isHover(x, y)) {
      // TODO set Resolution
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && r1440.isHover(x, y)) {
      // TODO set Resolution
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && r1080.isHover(x, y)) {
      // TODO set Resolution
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && r720.isHover(x, y)) {
      // TODO set Resolution
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && r480.isHover(x, y)) {
      // TODO set Resolution
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && fullscreen.isHover(x, y)) {
      // TODO toggle Fullscreen
    }

    InputHandler.update();

    Game.window.update();

    Game.getGuiRenderer().render(guis);
  }
}
