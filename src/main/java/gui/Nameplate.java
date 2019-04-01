package gui;

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

    setAlpha(.5f);
    setTextColour(new Vector3f(1, 1, 1));
    setFontSize(.5f);

    setText(player.getUsername());
  }

  @Override
  public void updateString() {
    if (getGuiText() != null) {
      TextMaster.removeText(getGuiText());
    }
    Vector2f loc = findLocation(Game.getActiveCamera());
    setPosition(loc);

    createGuiText();
  }

  private Vector2f findLocation(Camera camera) {
    // Transforms world coodinates to normalized device coordinates. Experimental feature!
    // This will generate the effect of the text pointing in the direction of the player.
    Vector4f loc =
        new Vector4f(
                player.getBbox().getMinX(),
                player.getBbox().getMaxY(),
                player.getBbox().getMaxZ() - player.getBbox().getDimZ() / 2,
                1f)
            .mul(Maths.createViewMatrix(camera))
            .mul(MasterRenderer.getProjectionMatrix());

    System.out.println(loc);
    System.out.println(MasterRenderer.getProjectionMatrix());

    if (loc.w <= 0) {
      return null;
    }
    float x = (loc.x / loc.w + 1) / 2f;
    float y = 1 - (loc.y / loc.w + 1) / 2f;
    return new Vector2f(x, y);
  }
}
