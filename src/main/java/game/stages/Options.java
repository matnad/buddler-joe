package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontrendering.TextMaster;
import game.Game;
import game.Settings;
import gui.GuiTexture;
import gui.MenuButton;
import gui.text.ChangableGuiText;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Options Menu specification and rendering. Must be initialized. Specifies all the elements in the
 * Options Menu . Contains and manages the Game Loop while the Options Menu is active.
 *
 * @author Sebastian Schlachter
 */
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
  private static MenuButton fsOn;
  private static MenuButton fsOff;
  private static MenuButton apply;
  private static GuiTexture r2160Sta;
  private static GuiTexture r1440Sta;
  private static GuiTexture r1080Sta;
  private static GuiTexture r720Sta;
  private static boolean firstLoop = true;
  private static int height = 0;
  private static int width = 0;
  private static boolean fullScreen = false;
  private static ChangableGuiText msgDisplay;
  private static String msg;
  private static boolean initializedText;
  private static GuiTexture fsOnSta;
  private static GuiTexture fsOffSta;

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
            loader.loadTexture("optionTable"),
            new Vector2f(0, -0.040741f),
            new Vector2f(0.554167f, 0.757804f),
            1);

    float buttonWidht = .035521f;
    float buttonHight = .048333f;

    r2160 =
        new MenuButton(
            loader,
            "3840x2160_norm",
            "3840x2160_hover",
            new Vector2f(-0.3625f, 0.192683f),
            new Vector2f(0.085807f, .050407f));

    r2160Sta =
        new GuiTexture(
            loader.loadTexture("3840x2160_hover"),
            new Vector2f(-0.3625f, 0.192683f),
            new Vector2f(0.085807f, .050407f),
            1);

    r1440 =
        new MenuButton(
            loader,
            "r1440_placeholder",
            "r1440_placeholder",
            new Vector2f(-0.179167f, 0.192683f),
            new Vector2f(0.085807f, .050407f));

    r1440Sta =
        new GuiTexture(
            loader.loadTexture("3840x2160_hover"),
            new Vector2f(-0.179167f, 0.192683f),
            new Vector2f(0.085807f, .050407f),
            1);

    r1080 =
        new MenuButton(
            loader,
            "r1080_placeholder",
            "r1080_placeholder",
            new Vector2f(0.000694f, 0.192683f),
            new Vector2f(0.085807f, .050407f));

    r1080Sta =
        new GuiTexture(
            loader.loadTexture("3840x2160_hover"),
            new Vector2f(0.000694f, 0.192683f),
            new Vector2f(0.085807f, .050407f),
            1);

    r720 =
        new MenuButton(
            loader,
            "r720_placeholder",
            "r720_placeholder",
            new Vector2f(0.181944f, 0.192683f),
            new Vector2f(0.085807f, .050407f));

    r720Sta =
        new GuiTexture(
            loader.loadTexture("3840x2160_hover"),
            new Vector2f(0.181944f, 0.192683f),
            new Vector2f(0.085807f, .050407f),
            1);

    fsOn =
        new MenuButton(
            loader,
            "toggleFullscreen_placeholder",
            "toggleFullscreen_placeholder",
            new Vector2f(-0.3625f, -0.082927f),
            new Vector2f(0.085807f, .050407f));

    fsOnSta =
        new GuiTexture(
            loader.loadTexture("toggleFullscreen_placeholder"),
            new Vector2f(-0.3625f, -0.082927f),
            new Vector2f(0.085807f, .050407f),
            1);

    fsOff =
        new MenuButton(
            loader,
            "toggleFullscreen_placeholder",
            "toggleFullscreen_placeholder",
            new Vector2f(-0.179167f, -0.082927f),
            new Vector2f(0.085807f, .050407f));

    fsOffSta =
        new GuiTexture(
            loader.loadTexture("toggleFullscreen_placeholder"),
            new Vector2f(-0.179167f, -0.082927f),
            new Vector2f(0.085807f, .050407f),
            1);

    // Back
    back =
        new MenuButton(
            loader,
            "back_norm",
            "back_hover",
            new Vector2f(0.75f, -0.851852f),
            new Vector2f(.097094f, .082347f));

    apply =
        new MenuButton(
            loader,
            "apply_norm",
            "apply_hover",
            new Vector2f(0, -0.623457f),
            new Vector2f(0.14018f, .082347f));
  }

  /**
   * Game Loop while the stage is active. This runs every frame as long as the Main Menu is active.
   * Include all rendering and input handling here.
   */
  @SuppressWarnings("Duplicates")
  public static void update() {
    if (!initializedText) {
      done();
      initText();
      initializedText = true;
    }
    if (firstLoop) {
      // get current settings;
      height = Game.getSettings().getHeight();
      width = Game.getSettings().getWidth();
      firstLoop = false;
    }

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
    guis.add(fsOn.getHoverTexture(x, y));
    guis.add(fsOff.getHoverTexture(x, y));
    guis.add(apply.getHoverTexture(x, y));

    if (height == 2160) {
      guis.add(r2160Sta);
      guis.remove(r2160.getHoverTexture(x, y));
    } else if (height == 1440) {
      guis.add(r1440Sta);
      guis.remove(r1440.getHoverTexture(x, y));
    } else if (height == 1080) {
      guis.add(r1080Sta);
      guis.remove(r1080.getHoverTexture(x, y));
    } else if (height == 720) {
      guis.add(r720Sta);
      guis.remove(r720.getHoverTexture(x, y));
    }

    if (fullScreen) {
      guis.add(fsOnSta);
      guis.remove(fsOn.getHoverTexture(x, y));
    } else {
      guis.add(fsOffSta);
      guis.remove(fsOff.getHoverTexture(x, y));
    }

    if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
        || InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
      done();
      Game.addActiveStage(Game.Stage.MAINMENU);
      Game.removeActiveStage(Game.Stage.OPTIONS);
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && r2160.isHover(x, y)) {
      height = 2160;
      width = 3840;
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && r1440.isHover(x, y)) {
      height = 1440;
      width = 2560;
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && r1080.isHover(x, y)) {
      height = 1080;
      width = 1920;
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && r720.isHover(x, y)) {
      height = 720;
      width = 1280;
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && fsOn.isHover(x, y)) {
      fullScreen = true;
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && fsOff.isHover(x, y)) {
      fullScreen = false;
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && apply.isHover(x, y)) {
      Settings settings = Game.getSettings();
      settings.setFullscreen(fullScreen);
      settings.setHeight(height);
      settings.setWidth(width);
      if (true) { // TODO: check if Textbox is empty.
        // settings.setIp(); //TODO: the entered IP should be set here.
      }
      // TODO: remove console printing
      System.out.println("Height: " + height + "p");
      System.out.println("Fullscreen: " + fullScreen);
    }

    Game.getGuiRenderer().render(guis);
    TextMaster.render();
  }

  /**
   * Instantiates the ChangeableGuiText for the msgDisplay. Also sets Position, Colour, and
   * Fontsize.
   */
  public static void initText() {
    msgDisplay = new ChangableGuiText();
    msgDisplay.setPosition(new Vector2f(0.274306f, 0.250849f));
    msgDisplay.setFontSize(1);
    msgDisplay.setTextColour(new Vector3f(255, 0, 0));
    msgDisplay.setCentered(false);
  }

  /** Deletes all the texts from the rendering list. */
  public static void done() {
    msg = "";
    initializedText = false;
    firstLoop = true;
    TextMaster.removeAll();
  }
}
