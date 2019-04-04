package gui.text;

import engine.render.MasterRenderer;
import engine.render.fontrendering.TextMaster;
import entities.Camera;
import entities.NetPlayer;
import game.Game;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import util.Maths;

/**
 * TEMPORARY CLASS.
 *
 * <p>Displays username that stay on the edge of the screen.. still work to be done here.
 *
 * <p>Disregard this as it is not implemented yet
 */
public class Nameplate extends GuiString {

  private NetPlayer player;

  /**
   * WIP.
   *
   * @param player player to generate name for
   */
  public Nameplate(NetPlayer player) {
    super();
    this.player = player;

    setAlpha(1f);
    setTextColour(new Vector3f(1, 1, 1));
    setFontSize(.8f);
    setMaxLineLength(0.3f);
    setCentered(true);

    setText(player.getUsername());
  }

  /**
   * Recreate Nameplates every frame to update positions.
   *
   * <p>Note: This is quite expensive. Optimization efforts are welcome.
   */
  public void updateString() {
    if (getGuiText() != null) {
      TextMaster.removeText(getGuiText());
    }
    Vector2f loc = findLocation(Game.getActiveCamera());
    if (loc != null) {
      setPosition(loc);
    }
    float fontSize = (50 / (player.getPosition().distance(Game.getActiveCamera().getPosition())));
    setFontSize(fontSize);

    createGuiText();
  }

  private Vector2f findLocation(Camera camera) {
    // Transforms world coodinates to normalized device coordinates with some offsets for centering.
    // Experimental feature!
    Vector4f loc =
        new Vector4f(
                player.getBbox().getMaxX() - player.getBbox().getDimX() / 2,
                player.getBbox().getMaxY() + 2,
                player.getBbox().getMaxZ() - player.getBbox().getDimZ() / 2,
                1f)
            .mul(Maths.createViewMatrix(camera))
            .mul(MasterRenderer.getProjectionMatrix());

    if (loc.w <= 0) {
      return null;
    }
    float x = (loc.x / loc.w + 1) / 2f;
    float y = 1 - (loc.y / loc.w + 1) / 2f;
    return new Vector2f(x - 0.15f, y);
  }
}
