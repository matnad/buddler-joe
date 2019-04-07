package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.render.Loader;
import game.Game;
import gui.GuiTexture;
import gui.MenuButton;
import gui.text.ChangableGuiText;
import java.util.ArrayList;
import java.util.List;

import net.packets.gamestatus.PacketGetHistory;
import net.packets.lobby.PacketGetLobbies;
import org.joml.Vector2f;

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
  }

  /**
   * Game Loop while the stage is active. This runs every frame as long as the Main Menu is active.
   * Include all rendering and input handling here.
   */
  @SuppressWarnings("Duplicates")
  public static void update() {

    List<GuiTexture> guis = new ArrayList<>();
    guis.add(background);
    guis.add(buddlerJoe);
    guis.add(titel);

    // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    guis.add(chooseLobby.getHoverTexture(x, y));
    guis.add(exitGame.getHoverTexture(x, y));
    guis.add(credits.getHoverTexture(x, y));
    guis.add(options.getHoverTexture(x, y));

    for (GuiTexture gui : guis) {
      gui.setAlpha(currentAlpha);
    }
    /*
    if (fadeTimer > 0) {

      // Main Menu fading out, no longer accepting inputs, just rendering
      if (fadeTimer >= FADE_TIME) {
        // Fading finished, reset variables and hide GUI
        fadeTimer = 0;
        Game.removeActiveStage(Game.Stage.MAINMENU);
        currentAlpha = 1;
      } else {
        fadeTimer += Game.window.getFrameTimeSeconds();
        currentAlpha = (FADE_TIME - fadeTimer) / FADE_TIME;
      }

    } else {
      // Active Main Menu, accepting inputs

      if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
          || ((InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_1) && exitGame.isHover(x, y)))) {
        Game.window.stop();
      } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && chooseLobby.isHover(x, y)) {
        Game.addActiveStage(Game.Stage.PLAYING);/*
        fadeTimer = (float) Game.window.getFrameTimeSeconds();
        currentAlpha = (FADE_TIME - fadeTimer) / FADE_TIME;
      }


    }
    */
    if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && chooseLobby.isHover(x, y)) {
      new PacketGetLobbies().sendToServer();
      Game.addActiveStage(Game.Stage.CHOOSELOBBY);
      Game.removeActiveStage(Game.Stage.MAINMENU);
      // trigger here
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && credits.isHover(x, y)) {
      Game.addActiveStage(Game.Stage.CREDITS);
      Game.removeActiveStage(Game.Stage.MAINMENU);
    } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && options.isHover(x, y)) {
      Game.addActiveStage(Game.Stage.OPTIONS);
      Game.removeActiveStage(Game.Stage.MAINMENU);
    } else if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)) {
      Game.addActiveStage(Game.Stage.WELCOME);
      Game.removeActiveStage(Game.Stage.MAINMENU);
    } else if ((InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && exitGame.isHover(x, y))) {
      Game.window.stop();
    } else if (InputHandler.isKeyPressed(GLFW_KEY_L)) {
      // TODO: remove this if option
      Game.addActiveStage(Game.Stage.INLOBBBY);
      Game.removeActiveStage(Game.Stage.MAINMENU);
    } else if (InputHandler.isKeyPressed(GLFW_KEY_H)) {
      // TODO: remove this if option
      new PacketGetHistory().sendToServer();
    }

    Game.getGuiRenderer().render(guis);
  }
}
