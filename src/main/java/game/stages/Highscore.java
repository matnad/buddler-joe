package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontrendering.TextMaster;
import game.Game;
import game.HighscoreEntry;
import gui.GuiTexture;
import gui.MenuButton;
import gui.text.ChangableGuiText;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Highscore Screen specification and rendering. Must be initialized. Specifies all the elements in
 * the Highscore Screen . Contains and manages the Game Loop while the Highscore Screen is active.
 *
 * @author Viktor Gsteiger
 */
public class Highscore {

  public static final Logger logger = LoggerFactory.getLogger(Highscore.class);
  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;
  private static GuiTexture background;
  private static GuiTexture highscore;
  private static GuiTexture buddlerJoe;
  //private static GuiTexture title;
  private static MenuButton back;
  private static float[] namesY = {0.330864f, 0.4f, 0.469136f, 0.538272f, 0.607407f, 0.676534f};
  private static float[] countY = {0.330864f, 0.4f, 0.469136f, 0.538272f, 0.607407f, 0.676534f};
  private static CopyOnWriteArrayList<HighscoreEntry> catalog;
  private static ChangableGuiText[] usernames = new ChangableGuiText[6];
  private static ChangableGuiText[] times = new ChangableGuiText[6];
  private static int startInd = 0;
  private static boolean initializedText = false;
  private static Vector3f black = new Vector3f(0, 0, 0);

  /**
   * Initialize Highscore textures. Will load the texture files and generate the basic highscore
   * parts. This needs to be called once before using the higshcore screen.
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

    highscore =
        new GuiTexture(
            loader.loadTexture("lobbyOverview"),
            new Vector2f(0, -0.040741f),
            new Vector2f(0.554167f, 0.757804f),
            1);

    //title =
     //   new GuiTexture(
      //      loader.loadTexture("highscoreTitle"),
       //     new Vector2f(-0.053125f, 0.446296f),
         //   new Vector2f(0.379167f, 0.052778f),
           // 1);

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
   * Game Loop while the stage is active. This runs every frame as long as the Highscore-Screen is
   * active. Include all rendering and input handling here.
   */
  @SuppressWarnings("Duplicates")
  public static void update() {
    if (!initializedText) {
      initText();
      initializedText = true;
    }

    catalog = Game.getHighscoreCatalog();
    // System.out.println(catalog.toString());

    List<GuiTexture> guis = new ArrayList<>();
    // add textures here
    guis.add(background);
    guis.add(highscore);
    guis.add(buddlerJoe);
    //guis.add(title);

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    // add the back button here
    guis.add(back.getHoverTexture(x, y));

    // Update the current highscore:
    for (int i = 0; i < usernames.length; i++) {
      try {
        if (i + startInd < catalog.size()) {
          usernames[i].changeText(i+1 + ") " + "Name: " + catalog.get(i).getUsername());
          times[i].changeText("Time: " + catalog.get(i).getTime());
          // System.out.println(i);
        } else {
          usernames[i].changeText("");
          times[i].changeText("");
        }
      } catch (IndexOutOfBoundsException e) {
        System.out.println("error in highscore");
        logger.error(e.getMessage());
      }
    }

    // Input-Handling:
    if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
        || InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
      done();
      Game.addActiveStage(Game.Stage.MAINMENU);
      Game.removeActiveStage(Game.Stage.HIGHSCORE);
    }

    Game.getGuiRenderer().render(guis);
    TextMaster.render();
  }

  /**
   * Instantiates the ChangeableGuiText for the player names and the player states. Also sets
   * Position, Colour, and Fontsize.
   */
  public static void initText() {
    for (int i = 0; i < usernames.length; i++) {
      usernames[i] = new ChangableGuiText();
      usernames[i].setPosition(new Vector2f(0.286719f, namesY[i]));
      usernames[i].setFontSize(1);
      usernames[i].setTextColour(black);
      usernames[i].setCentered(false);
      times[i] = new ChangableGuiText();
      times[i].setPosition(new Vector2f(0.586719f, countY[i]));
      times[i].setFontSize(1);
      times[i].setTextColour(black);
      times[i].setCentered(false);
    }
  }

  /** Deletes all the texts from this Page from the rendering list. */
  public static void done() {
    for (int i = 0; i < usernames.length; i++) {
      usernames[i].delete();
      times[i].delete();
    }
    initializedText = false;
  }
}
