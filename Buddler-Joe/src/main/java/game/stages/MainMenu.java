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

/**
 * Main Menu specification and rendering.
 * Must be initialized. Specifies all the elements in the Main Menu.
 * Contains and manages the Game Loop while the Main Menu is active.
 */
public class MainMenu {

  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;

  private static GuiTexture mainMenu;
  private static MenuButton joinGame;
  private static MenuButton exitGame;


  /**
   * * Initialize Game Menu.
   * Will load the texture files and generate the basic menu parts.
   * This needs to be called once before using the menu.
   *
   * @param loader main loader
   */
  public static void init(Loader loader) {

    currentAlpha = 1;

    //Main Menu
    mainMenu = new GuiTexture(loader.loadTexture("mainMenu"), new Vector2f(0, 0), new Vector2f(1,
        1), 1);

    //Join Game
    joinGame = new MenuButton(loader, "joinGame1", "joinGame2", new Vector2f(0, 0),
        new Vector2f(.4f, .4f / 3));

    //Exit Game
    exitGame = new MenuButton(loader, "exitGame1", "exitGame2", new Vector2f(0, -.3f),
        new Vector2f(.4f, .4f / 3));
  }

  /**
   * Game Loop while the stage is active.
   * This runs every frame as long as the Main Menu is active.
   * Include all rendering and input handling here.
   */
  @SuppressWarnings("Duplicates")
  public static void update() {
    List<GuiTexture> guis = new ArrayList<>();
    guis.add(mainMenu);

    //OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    guis.add(joinGame.getHoverTexture(x, y));
    guis.add(exitGame.getHoverTexture(x, y));

    for (GuiTexture gui : guis) {
      gui.setAlpha(currentAlpha);
    }

    if (fadeTimer > 0) {

      //Main Menu fading out, no longer accepting inputs, just rendering
      if (fadeTimer >= FADE_TIME) {
        //Fading finished, reset variables and hide GUI
        fadeTimer = 0;
        Game.removeActiveStage(Game.Stage.MAINMENU);
        currentAlpha = 1;
      } else {
        fadeTimer += Game.window.getFrameTimeSeconds();
        currentAlpha = (FADE_TIME - fadeTimer) / FADE_TIME;
      }

    } else {
      //Active Main Menu, accepting inputs
      if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
          || ((InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_1) && exitGame.isHover(x, y)))) {
        Game.window.stop();
      } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && joinGame.isHover(x, y)) {
        Game.addActiveStage(Game.Stage.PLAYING);
        fadeTimer = (float) Game.window.getFrameTimeSeconds();
        currentAlpha = (FADE_TIME - fadeTimer) / FADE_TIME;
      }
      InputHandler.update();
    }

    Game.window.update();

    Game.getGuiRenderer().render(guis);
  }
}
