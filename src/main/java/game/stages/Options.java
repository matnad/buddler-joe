package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontmeshcreator.FontType;
import engine.render.fontmeshcreator.GuiText;
import engine.render.fontrendering.TextMaster;
import game.Game;
import game.Settings;
import gui.GuiTexture;
import gui.MenuButton;
import gui.text.ChangableGuiText;
import java.util.ArrayList;
import java.util.List;
import net.ClientLogic;
import net.packets.loginlogout.PacketDisconnect;
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
  private static FontType font;
  private static Vector3f textColour;
  private static GuiText guiText;
  private static String servername = "";
  private static String newservername = "";
  private static String output;
  private static String statusMsg = "";

  /**
   * * Initialize Options-Menu. Will load the texture files and generate the basic menu parts. This
   * needs to be called once before using the menu.
   *
   * @param loader main loader
   */
  @SuppressWarnings("Duplicates")
  public static void init(Loader loader) {
    font = new FontType(loader, "verdanaAsciiEx");
    textColour = new Vector3f(0f, 0f, 0f);

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
            "2560x1440_norm",
            "2560x1440_hover",
            new Vector2f(-0.179167f, 0.192683f),
            new Vector2f(0.085807f, .050407f));

    r1440Sta =
        new GuiTexture(
            loader.loadTexture("2560x1440_hover"),
            new Vector2f(-0.179167f, 0.192683f),
            new Vector2f(0.085807f, .050407f),
            1);

    r1080 =
        new MenuButton(
            loader,
            "1920x1080_norm",
            "1920x1080_hover",
            new Vector2f(0.000694f, 0.192683f),
            new Vector2f(0.085807f, .050407f));

    r1080Sta =
        new GuiTexture(
            loader.loadTexture("1920x1080_hover"),
            new Vector2f(0.000694f, 0.192683f),
            new Vector2f(0.085807f, .050407f),
            1);

    r720 =
        new MenuButton(
            loader,
            "1280x720_norm",
            "1280x720_hover",
            new Vector2f(0.181944f, 0.192683f),
            new Vector2f(0.085807f, .050407f));

    r720Sta =
        new GuiTexture(
            loader.loadTexture("1280x720_hover"),
            new Vector2f(0.181944f, 0.192683f),
            new Vector2f(0.085807f, .050407f),
            1);

    fsOn =
        new MenuButton(
            loader,
            "on_norm",
            "on_hover",
            new Vector2f(-0.3625f, -0.082927f),
            new Vector2f(0.085807f, .050407f));

    fsOnSta =
        new GuiTexture(
            loader.loadTexture("on_hover"),
            new Vector2f(-0.3625f, -0.082927f),
            new Vector2f(0.085807f, .050407f),
            1);

    fsOff =
        new MenuButton(
            loader,
            "off_norm",
            "off_hover",
            new Vector2f(-0.179167f, -0.082927f),
            new Vector2f(0.085807f, .050407f));

    fsOffSta =
        new GuiTexture(
            loader.loadTexture("off_hover"),
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
      StringBuilder stringBuilder = new StringBuilder(Game.getSettings().getIp());
      InputHandler.setInputString(stringBuilder);
      initializedText = true;
      updateGuiText();
    }
    if (firstLoop) {
      // get current settings;
      height = Game.getSettings().getHeight();
      width = Game.getSettings().getWidth();
      fullScreen = Game.getSettings().isFullscreen();
      statusMsg = "";
      firstLoop = false;
    }

    if (Game.getReconnectStep() > 0) {
      msgDisplay.setTextColour(new Vector3f(0, 1, 0));
      msgDisplay.changeText("Trying to reconnect...", true);
    } else if (!ClientLogic.isConnected()) {
      msgDisplay.setTextColour(new Vector3f(1, 0, 0));
      msgDisplay.changeText("Not connected! Enter a valid IP and click apply.", true);
    } else {
      msgDisplay.setTextColour(new Vector3f(0, 1, 0));
      msgDisplay.changeText(statusMsg, true);
    }

    newservername = servername;
    InputHandler.readInputOn();
    newservername = InputHandler.getInputString();

    if (newservername.length() > 30) {
      newservername = servername;
      StringBuilder temp = new StringBuilder(servername);
      InputHandler.setInputString(temp);
    }

    if (!servername.equals(newservername)) {
      servername = newservername;
      updateGuiText();
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
    } else if (InputHandler.isKeyPressed(GLFW_KEY_ENTER)
        || InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && apply.isHover(x, y)) {
      Settings settings = Game.getSettings();
      if (fullScreen != settings.isFullscreen()) {
        settings.setFullscreen(fullScreen);
        setStatusMsg("Settings changed. Please restart the game.");
      }
      if (height != settings.getHeight() || width != settings.getWidth()) {
        settings.setHeight(height);
        settings.setWidth(width);
        setStatusMsg("Settings changed. Please restart the game.");
      }
      if (output.length() > 0) { // TODO: check if Textbox is empty.
        if (!settings.getIp().equals(output) || !ClientLogic.isConnected()) {
          // Ip changed, try to reconnect
          new PacketDisconnect().sendToServer();
          ClientLogic.setDisconnectFromServer(true);
          Game.tryToReconnect(output, -1);
        }
        settings.setIp(output);

        InputHandler.setInputString(new StringBuilder(output));
      }
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
    InputHandler.setInputString(new StringBuilder());
  }

  private static void updateGuiText() {

    output = servername;

    if (guiText != null) {
      TextMaster.removeText(guiText);
    }
    guiText =
        new GuiText(
            output, 1.5f, font, new Vector3f(0f, 0f, 0f), 1f, new Vector2f(.30f, .685f), 1f, false);
  }

  public static void setStatusMsg(String statusMsg) {
    Options.statusMsg = statusMsg;
  }
}
