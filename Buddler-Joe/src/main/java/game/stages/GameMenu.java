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
 * Must be initialized. Specifies all the elements in the Game Menu.
 * Contains and manages the Game Loop while the Game Menu is active.
 */
public class GameMenu {
  private static GuiTexture gameMenu;
  private static MenuButton exitGame;


  /**
   * Initialize Game Menu.
   * Will load the texture files and generate the basic menu parts.
   * This needs to be called once before using the menu.
   *
   * @param loader main loader
   */
  public static void init(Loader loader) {
    //Main Menu
    gameMenu = new GuiTexture(loader.loadTexture("gameMenu"), new Vector2f(0, 0),
        new Vector2f(.5f, .5f / 1.5f), 1);

    //Exit Game
    exitGame = new MenuButton(loader, "exitGame1", "exitGame2", new Vector2f(0, 0),
        new Vector2f(.4f, .4f / 3));
  }


  /**
   * Game Loop while the stage is active.
   * This runs every frame as long as the Game Menu is active.
   * Include all rendering and input handling here.
   */
  @SuppressWarnings("Duplicates")
  public static void update() {
    List<GuiTexture> guis = new ArrayList<>();
    guis.add(gameMenu);

    //OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)) {
      Game.addActiveStage(Game.Stage.PLAYING);
      Game.removeActiveStage(Game.Stage.GAMEMENU);
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && exitGame.isHover(x, y)) {
      Game.addActiveStage(Game.Stage.MAINMENU);
      Game.removeActiveStage(Game.Stage.PLAYING);
      Game.removeActiveStage(Game.Stage.GAMEMENU);
    }

    InputHandler.update();

    guis.add(exitGame.getHoverTexture(x, y));

    Game.window.update();

    Game.getGuiRenderer().render(guis);
  }
}
