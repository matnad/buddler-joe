package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontmeshcreator.FontType;
import engine.render.fontmeshcreator.GuiText;
import engine.render.fontrendering.TextMaster;
import game.Game;
import game.LobbyPlayerEntry;
import game.NetPlayerMaster;
import gui.GuiTexture;
import gui.MenuButton;
import gui.text.ChangableGuiText;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.packets.gamestatus.PacketReady;
import net.packets.lobby.PacketLeaveLobby;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Text;

/**
 * InLobby Menu specification and rendering. Must be initialized. Specifies all the elements in the
 * InLobby Menu . Contains and manages the Game Loop while the InLobby Menu is active.
 *
 * @author Sebastian Schlachter
 */
public class InLobby {
  public static final Logger logger = LoggerFactory.getLogger(InLobby.class);
  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;
  private static GuiTexture background;
  private static GuiTexture inLobby;
  private static MenuButton leave;
  private static MenuButton ready;
  private static ChangableGuiText[] names = new ChangableGuiText[7];
  private static ChangableGuiText[] testnames = new ChangableGuiText[7];

  private static ChangableGuiText[] status = new ChangableGuiText[7];
  private static boolean initializedText = false;
  private static float[] namesY = {
    0.330864f, 0.4f, 0.469136f, 0.538272f, 0.607407f, 0.676534f, 0.745669f
  };
  private static float[] statusY = {
    0.330864f, 0.4f, 0.469136f, 0.538272f, 0.607407f, 0.676534f, 0.745669f
  };
  private static CopyOnWriteArrayList<LobbyPlayerEntry> playerCatalog;

  private static ChangableGuiText lobbyname;
  private static ChangableGuiText newlobbyname;
  private static ChangableGuiText testlobbyname;

  private static Vector3f black = new Vector3f(0, 0, 0);
  private static boolean removeAtEndOfFrame = false;
  private static FontType font;
  private static String newLobby;
  private static String testLobby;

  /**
   * Initialisation of the textures for this GUI-menu.
   *
   * @param loader main loader
   */
  @SuppressWarnings("Duplicates")
  public static void init(Loader loader) {
    font = new FontType(loader, "verdanaAsciiEx");
    testLobby = "";
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
    if (!initializedText) {
      done();
      initText();
      //      Game.getChat().setLobbyChatPosition();
      //      Game.getChat().setLobbyMaxLines();
      //      Game.getChat().setLobbyColour();
      //      Game.getChat().setLobbyMaxLineLength();
      //      Game.getChat().setLobbyMessagePosition();
      //      Game.getChat().setAlpha();
      initializedText = true;
    }

    playerCatalog = Game.getLobbyPlayerCatalog();
    if (lobbyname.getGuiText() == null) {

      updateLobbyName();
    }

    List<GuiTexture> guis = new ArrayList<>();
    guis.add(Game.getChat().getChatGui());
    Game.getChat().checkInputs();
    // add textures here
    guis.add(background);
    guis.add(inLobby);

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    // add buttons here
    guis.add(leave.getHoverTexture(x, y));
    guis.add(ready.getHoverTexture(x, y));
    for (int i = 0; i < names.length; i++) {
      try {
        if (i < playerCatalog.size()) {
          // System.out.print(catalog.get(i+startInd).getPlayers()+" ");
          // System.out.println(i);
          if (!testnames[i].equals(playerCatalog.get(i).getName())) {
            names[i].changeText(playerCatalog.get(i).getName());
          }
          if (playerCatalog.get(i).isReady()) {
            status[i].changeText("ready");
          } else {
            status[i].changeText("unready");
          }
        } else {
          names[i].changeText("");
          status[i].changeText("");
        }
      } catch (IndexOutOfBoundsException e) {
        System.out.println("error in choose lobby");
        logger.error(e.getMessage());
      }
      updatename();
    }

    if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && leave.isHover(x, y)) {
      new PacketLeaveLobby().sendToServer();
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && ready.isHover(x, y)) {
      new PacketReady().sendToServer();
    }

    Game.getGuiRenderer().render(guis);
    TextMaster.render();
    if (removeAtEndOfFrame) {
      done();
      removeAtEndOfFrame = false;
    }
  }

  /**
   * Instantiates the ChangeableGuiText for the lobby name, the player names and the player states.
   * Also sets Position, Colour, and Fontsize.
   */
  @SuppressWarnings("Duplicates")
  public static void initText() {
    lobbyname = new ChangableGuiText();
    lobbyname.setPosition(new Vector2f(0.286719f, 0.248766f));
    lobbyname.setFontSize(2);
    lobbyname.setTextColour(black);
    lobbyname.setCentered(false);
    for (int i = 0; i < names.length; i++) {
      names[i] = new ChangableGuiText();
      names[i].setPosition(new Vector2f(0.286719f, namesY[i]));
      names[i].setFontSize(1);
      names[i].setTextColour(black);
      names[i].setCentered(false);
      testnames[i] = new ChangableGuiText();
      testnames[i].setPosition(new Vector2f(0.286719f, namesY[i]));
      testnames[i].setFontSize(1);
      testnames[i].setTextColour(black);
      testnames[i].setCentered(false);
      status[i] = new ChangableGuiText();
      status[i].setPosition(new Vector2f(-0.059896f, statusY[i]));
      status[i].setFontSize(1);
      status[i].setTextColour(black);
    }
  }

  /** Deletes all the texts from the rendering list. */
  @SuppressWarnings("Duplicates")
  public static synchronized void done() {
    initializedText = false;
    TextMaster.removeAll();
  }

  public static void setRemoveAtEndOfFrame(boolean removeAtEndOfFrame) {
    InLobby.removeAtEndOfFrame = removeAtEndOfFrame;
  }

  /** cuts the names to the correct length for the window. */
  public static void updatename() {
    for (int i = 0; i < names.length; i++) {
      boolean changed = false;
      testnames[i] = names[i];

      if (names[i].getText().length() > 0) {

        while (names[i]
                .getGuiText()
                .getLengthOfLines()
                .get(names[i].getGuiText().getLengthOfLines().size() - 1)
            > 0.105f) {

          if (names[i].getGuiText() != null) {
            TextMaster.removeText(names[i].getGuiText());
          }

          if (names[i].getText().length() > 0) {
            names[i].setText(names[i].getText().substring(0, names[i].getText().length() - 1));
          }
          changed = true;

          names[i].updateString();
        }
      }

      if (names[i].getGuiText() != null) {
        TextMaster.removeText(names[i].getGuiText());
      }
      if (names[i].getText().length() > 0 && changed) {
        names[i].changeText(names[i].getText() + "...");
      }
      names[i].updateString();
    }
  }

  /** cuts the lobbyname to the correct length for the window. */
  public static void updateLobbyName() {
    boolean changed = false;
    newLobby = NetPlayerMaster.getLobbyname();
    lobbyname.changeText(newLobby);
    testLobby = newLobby;
    if (lobbyname.getText().length() > 0) {

      while (lobbyname
              .getGuiText()
              .getLengthOfLines()
              .get(lobbyname.getGuiText().getLengthOfLines().size() - 1)
          > 0.18f) {

        if (lobbyname.getGuiText() != null) {
          TextMaster.removeText(lobbyname.getGuiText());
        }

        if (lobbyname.getText().length() > 0) {
          lobbyname.setText(lobbyname.getText().substring(0, lobbyname.getText().length() - 1));
        }
        changed = true;

        lobbyname.updateString();
      }
    }

    if (lobbyname.getGuiText() != null) {
      TextMaster.removeText(lobbyname.getGuiText());
    }
    if (lobbyname.getText().length() > 0 && changed) {
      lobbyname.changeText(lobbyname.getText() + "...");
    }
    lobbyname.updateString();
  }
}
