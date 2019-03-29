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

  public static void init(Loader loader) {
    GuiTexture loadingScreen =
        new GuiTexture(loader.loadTexture("ffffff"), new Vector2f(0, 0), new Vector2f(1, 1), 1);
    guis.add(loadingScreen);
    message = "LOADING";
    text = new ChangableGuiText();
    text.setPosition(new Vector2f(0, 0.5f));
    text.setFontSize(3);
    text.setTextColour(new Vector3f());
    generateDottedText();
  }

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

  public static void updateLoadingMessage(String loadingMessage) {
    message = loadingMessage;
  }

  private static void generateDottedText() {

    if (message.equals("done!")) {
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

  public static void done() {
    text.delete();
  }
}
