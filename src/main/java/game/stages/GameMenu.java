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

import net.packets.gamestatus.PacketGetHistory;
import net.packets.lobby.PacketLeaveLobby;
import net.packets.loginlogout.PacketDisconnect;
import org.joml.Vector2f;

/**
 * Main Menu specification and rendering. Must be initialized. Specifies all the elements in the
 * Game Menu. Contains and manages the Game Loop while the Game Menu is active.
 */
public class GameMenu {
  private static GuiTexture gameMenu;
  private static MenuButton continueB;
  private static MenuButton exit;
  private static MenuButton desktop;
  private static MenuButton highscore;
  private static MenuButton history;

  /**
   * Initialize Game Menu. Will load the texture files and generate the basic menu parts. This needs
   * to be called once before using the menu.
   *
   * @param loader main loader
   */
  public static void init(Loader loader) {
    // Main Menu
    gameMenu =
        new GuiTexture(
            loader.loadTexture("menuStone"),
            new Vector2f(0, 0),
            new Vector2f(.381944f * 1.1f, 0.555556f * 1.1f),
            1);

    continueB =
        new MenuButton(
            loader,
            "continue_norm",
            "continue_hover",
            new Vector2f(0, 0.2740f),
            new Vector2f(.305521f, .128333f));

    exit =
        new MenuButton(
            loader,
            "exitM_norm",
            "exitM_hover",
            new Vector2f(0, 0),
            new Vector2f(.305521f, .128333f));

    desktop =
        new MenuButton(
            loader,
            "desktop_norm",
            "desktop_hover",
            new Vector2f(0, -0.274074f),
            new Vector2f(.305521f, .128333f));

    highscore =
            new MenuButton(
                    loader,
                    "pokal_norm",
                    "pokal_hover",
                    new Vector2f(-0.232844f, 0.455556f),
                    new Vector2f(0.023625f * 0.6f, 0.05f * 0.6f));

    history =
            new MenuButton(
                    loader,
                    "book_norm",
                    "book_hover",
                    new Vector2f(-0.283822f, 0.455556f),
                    new Vector2f(0.035353f * 0.6f, 0.05f * 0.6f));
  }

  //new Vector2f(0.025489f, -0.496296f),
  //new Vector2f(-0.025489f, -0.496296f),

  /**
   * Game Loop while the stage is active. This runs every frame as long as the Game Menu is active.
   * Include all rendering and input handling here.
   */
  @SuppressWarnings("Duplicates")
  public static void update() {
    List<GuiTexture> guis = new ArrayList<>();
    guis.add(gameMenu);

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    guis.add(continueB.getHoverTexture(x, y));
    guis.add(exit.getHoverTexture(x, y));
    guis.add(desktop.getHoverTexture(x, y));
    guis.add(highscore.getHoverTexture(x, y));
    guis.add(history.getHoverTexture(x, y));

    // Input Handling:
    if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
        || InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && continueB.isHover(x, y)) {
      Game.addActiveStage(Game.Stage.PLAYING);
      Game.removeActiveStage(Game.Stage.GAMEMENU);
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && exit.isHover(x, y)) {
      new PacketLeaveLobby().sendToServer();
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && desktop.isHover(x, y)) {
      new PacketDisconnect().sendToServer();
      Game.window.stop();
    }else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && history.isHover(x, y)){
      new PacketGetHistory().sendToServer();
      Game.addActiveStage(Game.Stage.HISTORYMENU);
      Game.removeActiveStage(Game.Stage.GAMEMENU);
    }else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && highscore.isHover(x, y)){
      Highscore.setInGame(true);
      Game.addActiveStage(Game.Stage.HIGHSCORE);
      Game.removeActiveStage(Game.Stage.GAMEMENU);
    }
    Game.getGuiRenderer().render(guis);
  }
}
