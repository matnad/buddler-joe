package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontrendering.TextMaster;
import game.Game;
import gui.GuiTexture;
import gui.MenuButton;
import gui.text.ChangableGuiText;
import java.util.ArrayList;
import java.util.List;
import net.packets.lists.PacketHighscore;
import net.packets.lobby.PacketGetLobbies;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Main Menu specification and rendering. Must be initialized. Specifies all the elements in the
 * Main Menu. Contains and manages the Game Loop while the Main Menu is active.
 */
public class MainMenu {

  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;
  private static GuiTexture titel;

  private static GuiTexture background;
  private static GuiTexture buddlerJoe;
  private static MenuButton chooseLobby;
  private static MenuButton exitGame;
  private static MenuButton credits;
  private static MenuButton options;
  private static ChangableGuiText text;
  private static ChangableGuiText userName;
  private static boolean initializedText;
  private static MenuButton changeName;
  private static GuiTexture name;
  private static MenuButton highscore;
  private static MenuButton history;

  /**
   * * Initialize Game Menu. Will load the texture files and generate the basic menu parts. This
   * needs to be called once before using the menu.
   *
   * @param loader main loader
   */
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

    titel =
        new GuiTexture(
            loader.loadTexture("titelBig"),
            new Vector2f(0, 0.588889f),
            new Vector2f(0.291667f, 0.3f),
            1);

    name =
        new GuiTexture(
            loader.loadTexture("nametype"),
            new Vector2f(-0.955556f, 0.975309f),
            new Vector2f(0.039722f, 0.016049f),
            1);

    // Choose Lobby
    chooseLobby =
        new MenuButton(
            loader,
            "lobby_norm4",
            "lobby_hover",
            new Vector2f(0, 0.0740f),
            new Vector2f(.305521f, .128333f));

    // Credits
    credits =
        new MenuButton(
            loader,
            "credits_norm",
            "credits_hover",
            new Vector2f(0, -0.2f),
            new Vector2f(.305521f, .128333f));

    // Options
    options =
        new MenuButton(
            loader,
            "options_norm",
            "options_hover",
            new Vector2f(0, -0.474074f),
            new Vector2f(.305521f, .128333f));

    // Exit Game
    exitGame =
        new MenuButton(
            loader,
            "quitWood_norm",
            "quitWood_hover",
            new Vector2f(0.75f, -0.851852f),
            new Vector2f(.097094f, .082347f));

    changeName =
        new MenuButton(
            loader,
            "changeAR_norm",
            "changeAR_hover",
            new Vector2f(-0.958334f, 0.894791f),
            new Vector2f(0.024038f, 0.037038f));

    highscore =
        new MenuButton(
            loader,
            "pokal_norm",
            "pokal_hover",
            new Vector2f(0.9625f + 0.0125f, 0.92963f + 0.022222f),
            new Vector2f(0.023625f * 0.6f, 0.05f * 0.6f));

    history =
        new MenuButton(
            loader,
            "book_norm",
            "book_hover",
            new Vector2f(0.911522f + 0.0125f, 0.92963f + 0.022222f),
            new Vector2f(0.035353f * 0.6f, 0.05f * 0.6f));

    // change_arrows.png
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
    }
    userName.changeText(Game.getSettings().getUsername());

    List<GuiTexture> guis = new ArrayList<>();
    guis.add(background);
    guis.add(buddlerJoe);
    guis.add(titel);
    guis.add(name);

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    guis.add(chooseLobby.getHoverTexture(x, y));
    guis.add(exitGame.getHoverTexture(x, y));
    guis.add(credits.getHoverTexture(x, y));
    guis.add(options.getHoverTexture(x, y));
    guis.add(changeName.getHoverTexture(x, y));
    guis.add(highscore.getHoverTexture(x, y));
    guis.add(history.getHoverTexture(x, y));

    for (GuiTexture gui : guis) {
      gui.setAlpha(currentAlpha);
    }

    if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && chooseLobby.isHover(x, y)) {
      new PacketGetLobbies().sendToServer();
      MainMenu.done();
      Game.addActiveStage(Game.Stage.CHOOSELOBBY);
      Game.removeActiveStage(Game.Stage.MAINMENU);
      // trigger here
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && credits.isHover(x, y)) {
      MainMenu.done();
      Game.addActiveStage(Game.Stage.CREDITS);
      Game.removeActiveStage(Game.Stage.MAINMENU);
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && options.isHover(x, y)) {
      MainMenu.done();
      Game.addActiveStage(Game.Stage.OPTIONS);
      Game.removeActiveStage(Game.Stage.MAINMENU);
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && changeName.isHover(x, y)) {
      MainMenu.done();
      Game.addActiveStage(Game.Stage.CHANGENAME);
      Game.removeActiveStage(Game.Stage.MAINMENU);
    } else if ((InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && exitGame.isHover(x, y))) {
      Game.window.stop();
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && highscore.isHover(x, y)) {
      new PacketHighscore().sendToServer();
      MainMenu.done();
      Game.addActiveStage(Game.Stage.HIGHSCORE);
      Game.removeActiveStage(Game.Stage.MAINMENU);
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && history.isHover(x, y)) {
      // TODO: go to History Page.
    }
    Game.getGuiRenderer().render(guis);
    TextMaster.render();
  }

  /**
   * Instantiates the ChangeableGuiText for the userName. Also sets Position, Colour, and Fontsize.
   */
  private static void initText() {
    userName = new ChangableGuiText();
    userName.setPosition(new Vector2f(0.045139f, -0.002469f));
    userName.setFontSize(0.9f);
    userName.setTextColour(new Vector3f(0, 0, 0));
    userName.setCentered(false);
    initializedText = true;
  }

  /** Deletes all the texts from the rendering list. */
  public static void done() {
    initializedText = false;
    TextMaster.removeAll();
  }
}
