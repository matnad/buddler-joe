package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontrendering.TextMaster;
import game.Game;
import game.LobbyEntry;
import gui.GuiTexture;
import gui.MenuButton;
import gui.text.ChangableGuiText;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.lobbyhandling.Lobby;
import net.packets.lobby.PacketJoinLobby;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * History Menu specification and rendering. Must be initialized. Specifies all the elements in the
 * History Menu . Contains and manages the Game Loop while the History Menu is active.
 *
 * @author Sebastian Schlachter
 */
public class HistoryMenu {

  public static final Logger logger = LoggerFactory.getLogger(ChooseLobby.class);
  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;
  private static GuiTexture background;
  private static GuiTexture lobbyOverview;
  private static GuiTexture buddlerJoe;
  private static GuiTexture titel;
  private static MenuButton back;
  private static MenuButton up;
  private static MenuButton down;
  private static int n = 6; // varibale that defines how many join buttons are displayed. Max is 6.
  private static float[] lineY = {0.330864f, 0.4f, 0.469136f, 0.538272f, 0.607407f, 0.676534f};
  private static CopyOnWriteArrayList<String> catalog;
  private static ChangableGuiText[] lines = new ChangableGuiText[6];
  private static int startInd = 0;
  private static int page = 0;
  private static ChangableGuiText pageIndex;
  private static boolean initializedText = false;
  private static boolean initializedPageIndex = false;
  private static Vector3f black = new Vector3f(0, 0, 0);

  /**
   * * Initialize ChooseLobby Menu. Will load the texture files and generate the basic menu parts.
   * This needs to be called once before using the menu.
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

    lobbyOverview =
        new GuiTexture(
            loader.loadTexture("lobbyOverview"),
            new Vector2f(0, -0.040741f),
            new Vector2f(0.554167f, 0.757804f),
            1);

    titel =
        new GuiTexture(
            loader.loadTexture("historytitel"),
            new Vector2f(-0.252083f, 0.446296f),
            new Vector2f(0.175157f, 0.052778f),
            1);

    // Back
    back =
        new MenuButton(
            loader,
            "back_norm",
            "back_hover",
            new Vector2f(0.75f, -0.851852f),
            new Vector2f(.097094f, .082347f));

    up =
        new MenuButton(
            loader,
            "up_norm",
            "up_hover",
            new Vector2f(0.350608f, -0.512963f),
            new Vector2f(0.040712f, 0.0694444f));

    down =
        new MenuButton(
            loader,
            "down_norm",
            "down_hover",
            new Vector2f(0.432032f, -0.512963f),
            new Vector2f(0.041406f, 0.0694444f));
  }

  /**
   * Game Loop while the stage is active. This runs every frame as long as the InLobby-Menu is
   * active. Include all rendering and input handling here.
   */
  @SuppressWarnings("Duplicates")
  public static void update() {
    if (!initializedText) {
      done();
      initText();
      initializedText = true;
    }

    catalog = Game.getHistoryCatalog();
    // System.out.println(catalog.toString());

    List<GuiTexture> guis = new ArrayList<>();
    // add textures here
    guis.add(background);
    guis.add(lobbyOverview);
    guis.add(buddlerJoe);
    guis.add(titel);

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    // add buttons here
    guis.add(back.getHoverTexture(x, y));

    startInd = page * 6;
    setN(catalog.size() - startInd);
    for (int i = 0; i < lines.length; i++) {
      try {
        if (i + startInd < catalog.size()) {
          // System.out.print(catalog.get(i+startInd).getPlayers()+" ");
          // System.out.println(i);
          lines[i].changeText(catalog.get(i + startInd));
        } else {
          lines[i].changeText("");
        }
      } catch (IndexOutOfBoundsException e) {
        System.out.println("error in choose lobby");
        logger.error(e.getMessage());
      }
    }
    // Place PageIndex-------------------------------------------------------------------
    if (n == 6 || page != 0) {
      guis.add(up.getHoverTexture(x, y));
      guis.add(down.getHoverTexture(x, y));
      if (!initializedPageIndex) {
        initPageIndex();
        initializedPageIndex = true;
      }
      pageIndex.changeText("Page: " + (page + 1));
    } else {
      if (initializedPageIndex) {
        pageIndex.delete();
        initializedPageIndex = false;
      }
      guis.remove(up.getHoverTexture(x, y));
      guis.remove(down.getHoverTexture(x, y));
    }

    // Input-Handling-------------------------------------------------------------------
    if (Game.getActiveStages().contains(Game.Stage.PLAYING)) {
      if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
              || InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
        done();
        Game.removeActiveStage(Game.Stage.HISTORYMENU);
      }
    } else {
      if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
          || InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
        done();
        Game.addActiveStage(Game.Stage.MAINMENU);
        Game.removeActiveStage(Game.Stage.HISTORYMENU);
      }
    }
    if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && up.isHover(x, y)) {
      if (page != 0) {
        page = page - 1;
      }
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && down.isHover(x, y)) {
      page = page + 1;
    }

    Game.getGuiRenderer().render(guis);
    TextMaster.render();
  }

  /**
   * Instantiates the ChangeableGuiText for the {@code pageIndex}. Also sets Position, Colour, and
   * Fontsize.
   */
  public static void initPageIndex() {
    pageIndex = new ChangableGuiText();
    pageIndex.setPosition(new Vector2f(0.665104f, 0.791667f));
    pageIndex.setFontSize(1);
    pageIndex.setTextColour(black);
    pageIndex.setCentered(false);
  }

  /** Instantiates the ChangeableGuiText for the lines. Also sets Position, Colour, and Fontsize. */
  public static void initText() {
    for (int i = 0; i < lines.length; i++) {
      lines[i] = new ChangableGuiText();
      lines[i].setPosition(new Vector2f(0.286719f, lineY[i]));
      lines[i].setFontSize(1);
      lines[i].setTextColour(black);
      lines[i].setCentered(false);
    }
  }

  /** Deletes all the texts from the rendering list. */
  public static synchronized void done() {
    page = 0;
    initializedPageIndex = false;
    initializedText = false;
    TextMaster.removeAll();
  }

  /**
   * Setter for n.
   *
   * @param n the value that n should be set to.
   */
  public static void setN(int n) {
    if (n > 6) {
      n = 6;
    } else if (n < 0) {
      n = 0;
    }
    HistoryMenu.n = n;
  }
}
