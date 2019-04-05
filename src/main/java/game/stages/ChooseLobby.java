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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import gui.text.ChangableGuiText;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChooseLobby {

  private static final float FADE_TIME = .5f;
  private static float fadeTimer;
  private static float currentAlpha;

  private static GuiTexture background;
  private static GuiTexture lobbyOverview;
  private static GuiTexture buddlerJoe;
  private static GuiTexture titel;

  private static MenuButton back;
  private static MenuButton[] join = new MenuButton[6];

  private static int n = 6; // varibale that defines how many join buttons are displayed. Max is 6.

  private static float[] joinY = {0.312963f, 0.175926f, 0.037037f, -0.1f, -0.238889f, -0.375926f};
  private static float[] namesY = {0.312963f, 0.175926f, 0.037037f, -0.1f, -0.238889f, -0.375926f};
  private static float[] countY = {0.312963f, 0.175926f, 0.037037f, -0.1f, -0.238889f, -0.375926f};
  private static CopyOnWriteArrayList<LobbyEntry> catalog;
  private static ChangableGuiText[] names = new ChangableGuiText[6];
  private static ChangableGuiText[] count = new ChangableGuiText[6];
  private static int startInd = 0;
  private static ChangableGuiText text;

  public static final Logger logger = LoggerFactory.getLogger(ChooseLobby.class);

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
            loader.loadTexture("availableLobbiesType"),
            new Vector2f(-0.053125f, 0.446296f),
            new Vector2f(0.379167f, 0.052778f),
            1);

    // Back
    back =
        new MenuButton(
            loader,
            "back_norm",
            "back_hover",
            new Vector2f(0.75f, -0.851852f),
            new Vector2f(.097094f, .082347f));

    // initialize all join Buttens
    for (int i = 0; i < join.length; i++) {
      join[i] =
          new MenuButton(
              loader,
              "join_norm",
              "join_hover",
              new Vector2f(0.391667f, joinY[i]),
              new Vector2f(0.082365f, .069444f));
    }
  }

  /**
   * Game Loop while the stage is active. This runs every frame as long as the InLobby-Menu is
   * active. Include all rendering and input handling here.
   */
  @SuppressWarnings("Duplicates")
  public static void update() {
    initText();
    catalog =  Game.getLobbyCatalog();
    System.out.println(catalog.toString());

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

    int page = 0;
    startInd = page*7;
    for (int i = 0; i < names.length; i++) {
      try{
      if(i+startInd < catalog.size()){
        System.out.println(catalog.get(i+startInd).getName());
        names[i].changeText(catalog.get(i+startInd).getName());
        count[i].changeText(String.valueOf(catalog.get(i+startInd).getPlayers()));
        System.out.println(i);
      }else{
        names[i].changeText("");
        count[i].changeText("");
      }
      }catch(IndexOutOfBoundsException e){
        System.out.println("error in choose lobby");
        logger.error(e.getMessage());
      }
    }

    for (int i = 0; i < n; i++) {
      guis.add(join[i].getHoverTexture(x, y));
    }
    for (int i = 5; i > -1 + n; i--) {
      guis.remove(join[i].getHoverTexture(x, y));
    }



    if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
        || InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
      done();
      Game.addActiveStage(Game.Stage.MAINMENU);
      Game.removeActiveStage(Game.Stage.CHOOSELOBBY);
    } else {
      for (int i = 0; i < n; i++) {
        if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && join[i].isHover(x, y)) {
          // TODO trigger join lobby at position i
          break;
        }
      }
    }




    TextMaster.render();
    Game.getGuiRenderer().render(guis);
  }

  public static void initText(){

    text = new ChangableGuiText();
    text.changeText("Schiff Ahoi");
    text.setTextColour(new Vector3f(1, 1, 1));
    text.setFontSize(3);
    for (int i = 0; i< names.length; i++) {
      names[i] = new ChangableGuiText();
      names[i].setPosition(new Vector2f(-0.5f, namesY[i]));
      names[i].setFontSize(3);
      names[i].setTextColour(new Vector3f(0,0,1));
      count[i] = new ChangableGuiText();
      count[i].setPosition(new Vector2f(0, countY[i]));
      count[i].setFontSize(3);
      count[i].setTextColour(new Vector3f(0,0,1));
    }
  }

  public static void done(){

    text.delete();
    for (int i = 0; i< names.length; i++) {
      names[i].delete();
      count[i].delete();
    }
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
    ChooseLobby.n = n;
  }
}
