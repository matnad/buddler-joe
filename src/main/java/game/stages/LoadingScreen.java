package game.stages;

import engine.render.Loader;
import engine.render.fontrendering.TextMaster;
import game.Game;
import gui.ChangableGuiText;
import gui.GuiTexture;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;

/** A simple loading screen with minimal animations and changeable loading message. */
public class LoadingScreen {

  private static List<GuiTexture> guis;
  private static ChangableGuiText text;
  private static String message;

  private static int dots;
  private static boolean decreaseDots;
  private static float elapsedSinceChange;

  static {
    guis = new ArrayList<>();
    dots = 3;
    decreaseDots = true;
    elapsedSinceChange = 0;
  }

  /**
   * Preload background and font with settings.
   *
   * @param loader main loader
   */
  public static void init(Loader loader) {
    // GuiTexture loadingScreen =
    //    new GuiTexture(loader.loadTexture("ffffff"), new Vector2f(0, 0), new Vector2f(1, 1), 1);
    GuiTexture loadingScreen =
        new GuiTexture(
            loader.loadTexture("mainMenuBackground"), new Vector2f(0, 0), new Vector2f(1, 1), 1);
    GuiTexture buddlerJoe =
        new GuiTexture(
            loader.loadTexture("buddlerjoe"),
            new Vector2f(-0.730208f, -0.32963f),
            new Vector2f(0.181771f, 0.67963f),
            1);

    guis.add(loadingScreen);
    guis.add(buddlerJoe);
    message = "LOADING";
    text = new ChangableGuiText();
    text.setPosition(new Vector2f(0, 0.5f));
    text.setFontSize(3);
    text.setTextColour(new Vector3f(1,1,1));
    generateDottedText();
  }

  /**
   * Update the loading screen. Run every frame. Will do the "..." animation and change the text
   * according to the message variable.
   */
  public static void update() {
    Game.window.update();

    if (!message.equals(text.getText())) {
      generateDottedText();
    }

    elapsedSinceChange += Game.window.getFrameTimeSeconds();
    if (elapsedSinceChange > .5f) {
      if (decreaseDots) {
        dots--;
        if (dots == 0) {
          decreaseDots = false;
        }
      } else {
        dots++;
        if (dots == 3) {
          decreaseDots = true;
        }
      }
      elapsedSinceChange = 0;
    }

    Game.getGuiRenderer().render(guis);
    TextMaster.render();
  }

  /**
   * Set a new message to display on the loading screen.
   *
   * @param loadingMessage new loading message
   */
  public static void updateLoadingMessage(String loadingMessage) {
    message = loadingMessage;
    progess();
  }

  /** Progress the dots and render one screen. */
  public static void progess() {
    elapsedSinceChange += 1f;
    update();
    Game.window.swapBuffers();
  }

  private static void generateDottedText() {

    if (message.equals("Ready!")) {
      text.changeText(message);
    } else {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < dots; i++) {
        sb.append(".");
      }
      sb.append(" ").append(message).append(" ");
      for (int i = 0; i < dots; i++) {
        sb.append(".");
      }
      text.changeText(sb.toString());
    }
  }

  /** Delete the gui elements that no longer need to be rendered when the loading screen is over. */
  public static void done() {
    text.delete();
  }
}
