package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontmeshcreator.FontType;
import engine.render.fontmeshcreator.GuiText;
import engine.render.fontrendering.TextMaster;
import game.Game;
import gui.GuiTexture;
import gui.MenuButton;
import gui.text.ChangableGuiText;
import java.util.ArrayList;
import java.util.List;
import net.packets.lobby.PacketCreateLobby;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * LobbyCreation Menu specification and rendering. Must be initialized. Specifies all the elements
 * in the LobbyCreation Menu . Contains and manages the Game Loop while the LobbyCreation Menu is
 * active.
 *
 * @author Sebastian Schlachter
 */
public class LobbyCreation {
  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;
  private static GuiTexture buddlerJoe;

  private static GuiTexture background;

  private static MenuButton back;
  private static GuiTexture table;
  private static MenuButton small;
  private static MenuButton medium;
  private static MenuButton big;
  private static String mapSize = "";
  private static GuiTexture smallSta;
  private static GuiTexture mediumSta;
  private static GuiTexture bigSta;
  private static MenuButton create;
  private static String msg = "";
  private static ChangableGuiText msgDisplay = new ChangableGuiText();
  private static int cooldown = 0;
  private static boolean initializedText = false;
  private static boolean firstLoop = true;
  private static String newlobbyname = "";
  private static String lobbyname = "";
  private static String output;
  private static FontType font;
  private static Vector3f textColour;
  private static GuiText guiText;

  /**
   * Initializes the textures for this GUI-menu.
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

    table =
        new GuiTexture(
            loader.loadTexture("creationMenuSample"),
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

    small =
        new MenuButton(
            loader,
            "small_norm",
            "small_hover",
            new Vector2f(-0.309091f, -0.315f),
            new Vector2f(0.14018f, .082347f));

    smallSta =
        new GuiTexture(
            loader.loadTexture("small_hover"),
            new Vector2f(-0.309091f, -0.315f),
            new Vector2f(0.14018f, .082347f),
            1);

    medium =
        new MenuButton(
            loader,
            "medium_norm",
            "medium_hover",
            new Vector2f(0, -0.315f),
            new Vector2f(0.14018f, .082347f));

    mediumSta =
        new GuiTexture(
            loader.loadTexture("medium_hover"),
            new Vector2f(0, -0.315f),
            new Vector2f(0.14018f, .082347f),
            1);

    big =
        new MenuButton(
            loader,
            "big_norm",
            "big_hover",
            new Vector2f(0.309091f, -0.315f),
            new Vector2f(0.14018f, .082347f));

    bigSta =
        new GuiTexture(
            loader.loadTexture("big_hover"),
            new Vector2f(0.309091f, -0.315f),
            new Vector2f(0.14018f, .082347f),
            1);

    create =
        new MenuButton(
            loader,
            "create_norm",
            "create_hover",
            new Vector2f(0, -0.57f),
            new Vector2f(0.14018f, .082347f));
  }

  /** Updates the GUI every cycle. */
  @SuppressWarnings("Duplicates")
  public static void update() {
    if (!initializedText) {
      done();
      initText();
      initializedText = true;
    }
    if (firstLoop) {
      done();
      firstLoop = false;
    }

    newlobbyname = lobbyname;
    InputHandler.readInputOn();
    newlobbyname = InputHandler.getInputString();

    if (newlobbyname.length() > 16) {
      newlobbyname = lobbyname;
      StringBuilder temp = new StringBuilder(lobbyname);
      InputHandler.setInputString(temp);
    }

    if (!lobbyname.equals(newlobbyname)) {
      lobbyname = newlobbyname;
      updateGuiText();
    }

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
    guis.add(small.getHoverTexture(x, y));
    guis.add(medium.getHoverTexture(x, y));
    guis.add(big.getHoverTexture(x, y));
    guis.add(create.getHoverTexture(x, y));

    if (mapSize.equals("s")) {
      guis.add(smallSta);
      guis.remove(small.getHoverTexture(x, y));
    }
    if (mapSize.equals("m")) {
      guis.add(mediumSta);
      guis.remove(medium.getHoverTexture(x, y));
    }
    if (mapSize.equals("l")) {
      guis.add(bigSta);
      guis.remove(big.getHoverTexture(x, y));
    }

    if (cooldown != 0) {
      cooldown--;
    } else {
      msg = "";
    }
    msgDisplay.changeText(msg);

    if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
        || InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
      Game.addActiveStage(Game.Stage.CHOOSELOBBY);
      Game.removeActiveStage(Game.Stage.LOBBYCREATION);
      done();
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && small.isHover(x, y)) {
      mapSize = "s";
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && medium.isHover(x, y)) {
      mapSize = "m";
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && big.isHover(x, y)) {
      mapSize = "l";
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && create.isHover(x, y)) {
      new PacketCreateLobby(newlobbyname + "║" + mapSize).sendToServer();
      InputHandler.resetInputString();
      initializedText = false;
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
    msgDisplay.setPosition(new Vector2f(0.274306f, 0.31125f));
    msgDisplay.setFontSize(1);
    msgDisplay.setTextColour(new Vector3f(255, 0, 0));
    msgDisplay.setCentered(false);
  }

  /** Deletes all the texts from the rendering list. */
  public static synchronized void done() {
    mapSize = "";
    msg = "";
    initializedText = false;
    TextMaster.removeAll();
  }

  /**
   * Sets the Variable msg.
   *
   * @param msg value that msg should have.
   */
  public static void setMsg(String msg) {
    LobbyCreation.msg = msg;
    cooldown = 300;
  }

  private static void updateGuiText() {

    output = lobbyname;
    TextMaster.removeAll();

    guiText =
        new GuiText(
            output, 1.5f, font, new Vector3f(0f, 0f, 0f), 1f, new Vector2f(.30f, .465f), 1f, false);
  }
}
