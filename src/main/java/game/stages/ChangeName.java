package game.stages;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontrendering.TextMaster;
import game.Game;
import game.Settings;
import gui.GuiTexture;
import gui.MenuButton;
import gui.text.ChangableGuiText;
import net.packets.lobby.PacketCreateLobby;
import net.packets.name.PacketSetName;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

/**
 * ChangeName Menu specification and rendering. Must be initialized. Specifies all the elements in
 * the ChangeName Menu . Contains and manages the Game Loop while the ChangeName Menu is active.
 *
 * @author Sebastian Schlachter
 */
public class ChangeName {
  private static float currentAlpha;
  private static GuiTexture buddlerJoe;

  private static GuiTexture background;

  private static MenuButton back;
  private static GuiTexture table;
  private static MenuButton change;
  private static String msg = "";
  private static ChangableGuiText msgDisplay = new ChangableGuiText();
  private static ChangableGuiText curName = new ChangableGuiText();
  private static int cooldown = 0;
  private static boolean initializedText = false;


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

    table =
        new GuiTexture(
            loader.loadTexture("nameChTable"),
            new Vector2f(0, -0.040741f),
            new Vector2f(0.554167f, 0.757804f),
            1);

    // Back
    back =
        new MenuButton(
            loader,
            "back_norm",
            "back_hover",
            new Vector2f(0.75f, -0.851852f),
            new Vector2f(.097094f, .082347f));

    change =
        new MenuButton(
            loader,
            "change_norm",
            "change_hover",
            new Vector2f(0, -0.57f),
            new Vector2f(0.147798f, .082347f));
  }

  /** Updates the GUI every cycle. */
  @SuppressWarnings("Duplicates")
  public static void update() {
    if (!initializedText) {
      initText();
      initializedText = true;
    }

    curName.changeText(Game.getActivePlayer().getUsername());

    List<GuiTexture> guis = new ArrayList<>();
    // add textures here
    guis.add(background);
    guis.add(table);
    guis.add(buddlerJoe);

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    // add buttons here
    guis.add(back.getHoverTexture(x, y));
    guis.add(change.getHoverTexture(x, y));
    if (cooldown != 0) {
      cooldown--;
    } else {
      msg = "";
    }
    msgDisplay.changeText(msg);

    if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
        || InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
      done();
      Game.addActiveStage(Game.Stage.MAINMENU);
      Game.removeActiveStage(Game.Stage.CHANGENAME);
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && change.isHover(x, y)) {
      new PacketSetName("user" + Long.toString(System.currentTimeMillis()).substring(4))
          .sendToServer();
      // TODO: replace "user+currentTime" with entered UserName.
    }

    Game.getGuiRenderer().render(guis);
    TextMaster.render();
  }

  /**
   * Instantiates the ChangeableGuiText for the msgDisplay. Also sets Position, Colour, and
   * Fontsize.
   */
  @SuppressWarnings("Duplicates")
  public static void initText() {
    msgDisplay = new ChangableGuiText();
    msgDisplay.setPosition(new Vector2f(0.274306f, 0.250849f));
    msgDisplay.setFontSize(1);
    msgDisplay.setTextColour(new Vector3f(255, 0, 0));
    msgDisplay.setCentered(false);
    // ----------------------------------------------------------------------------------
    curName = new ChangableGuiText();
    curName.setPosition(new Vector2f(0.483333f, 0.367901f));
    curName.setFontSize(2);
    curName.setTextColour(new Vector3f(0.564706f, 0.564706f, 0.564706f));
    curName.setCentered(false);
  }

  /** Deletes all the texts from this Page from the rendering list. */
  public static void done() {
    msg = "";
    msgDisplay.delete();
    curName.delete();
    initializedText = false;
  }

  /**
   * Sets the Variable msg.
   *
   * @param msg value that msg should have.
   */
  public static void setMsg(String msg) {
    ChangeName.msg = msg;
    cooldown = 300;
  }
}
