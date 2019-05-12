package game.stages;

import engine.render.Loader;
import engine.render.fontrendering.TextMaster;
import game.Game;
import gui.GuiTexture;
import gui.text.ChangableGuiText;
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

  private static boolean showDots;

  static {
    guis = new ArrayList<>();
    dots = 3;
    showDots = true;
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
    setFontSize();
    setFontColour();
    generateDottedText();
  }

  /**
   * Change font size for loading screen.
   *
   * @param fontSize new font size
   */
  public static void setFontSize(float fontSize) {
    text.setFontSize(fontSize);
  }

  /** Reset Font Size. */
  public static void setFontSize() {
    setFontSize(3);
  }

  /**
   * Change font size for loading screen.
   *
   * @param colour new font colour
   */
  public static void setFontColour(Vector3f colour) {
    text.setTextColour(colour);
  }

  /** Reset Font Colour. */
  public static void setFontColour() {
    setFontColour(new Vector3f(1, 1, 1));
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

    elapsedSinceChange += Game.dt();
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
    updateLoadingMessage(loadingMessage, true);
  }

  /**
   * Set a new message to display on the loading screen. And specify if you want dots.
   *
   * @param loadingMessage new loading message
   * @param showDots is true if a point should be showed
   */
  public static void updateLoadingMessage(String loadingMessage, boolean showDots) {
    LoadingScreen.showDots = showDots;
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
    if (message.equals("Ready!") || !showDots) {
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
