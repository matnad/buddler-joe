package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.render.Loader;
import game.Game;
import gui.GuiTexture;
import gui.MenuButton;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

public class Login {
  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;

  private static GuiTexture background;
  private static GuiTexture welcome;


  private static MenuButton back;
  private static MenuButton change;
  private static MenuButton proceed;
  private static GuiTexture typeField;

  /**
   * * Initialize Login-Menu. Will load the texture files and
   * generate the basic menu parts. This
   * needs to be called once before using the menu.
   *
   * @param loader main loader
   */
  @SuppressWarnings("Duplicates")
  public static void init(Loader loader) {

    currentAlpha = 1;

    // Background
    background =
            new GuiTexture(loader.loadTexture("mainMenuBackground"), new Vector2f(0, 0),
                    new Vector2f(1, 1), 1);

    welcome = new GuiTexture(loader.loadTexture("login_placeholder"), new Vector2f(0, 0),
            new Vector2f(0.5f, 0.5f), 1);

    typeField = new GuiTexture(loader.loadTexture("buttonTest"), new Vector2f(-0.25f, -0.2f),
            new Vector2f(.305521f, .128333f), 1);


    change =
            new MenuButton(
                    loader, "change_placeholder", "change_placeholder", new Vector2f(0.2f, -0.2f),
                    new Vector2f(.105521f, .128333f));
    proceed =
            new MenuButton(
                    loader, "proceed_placeholder", "proceed_placeholder", new Vector2f(0, -0.5f),
                    new Vector2f(.105521f, .128333f));

    // Back
    back =
            new MenuButton(
                    loader, "back_placeholder", "back_placeholder", new Vector2f(0.75f, -0.75f),
                    new Vector2f(.105521f, .128333f));


  }

  /**
   * Game Loop while the stage is active. This runs every frame as long as the Login-Menu is active.
   * Include all rendering and input handling here.
   */
  @SuppressWarnings("Duplicates")
  public static void update() {
    List<GuiTexture> guis = new ArrayList<>();
    //add textures here
    guis.add(background);
    guis.add(welcome);
    guis.add(typeField);

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    //add buttons here
    guis.add(back.getHoverTexture(x, y));
    guis.add(change.getHoverTexture(x, y));
    guis.add(proceed.getHoverTexture(x, y));


    if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && change.isHover(x, y)) {
      //TODO trigger Name-Change
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && proceed.isHover(x, y)) {
      //TODO trigger ServerLogin
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
      Game.addActiveStage(Game.Stage.WELCOME);
      Game.removeActiveStage(Game.Stage.LOGIN);
    }

    InputHandler.update();

    Game.window.update();

    Game.getGuiRenderer().render(guis);
  }
}
