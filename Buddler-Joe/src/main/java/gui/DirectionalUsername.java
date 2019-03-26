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
public class DirectionalUsername extends GuiString {

  private NetPlayer player;

  /**
   * Create directional username. WIP/TEMPORARY CLASS!
   *
   * @param player player to generate name for
   */
  public DirectionalUsername(NetPlayer player) {
    super();
    this.player = player;

    setAlpha(.5f);
    setTextColour(new Vector3f(1, 1, 1));
    setFontSize(.5f);

    setGuiStringString(player.getUsername());
  }

  @Override
  public void updateString() {
    if (getGuiString() != null) {
      TextMaster.removeText(getGuiString());
    }
    setPosition(findLocation(Game.getActiveCamera()));

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
            .mul(MasterRenderer.getProjectionMatrix())
            .normalize();

    float normX = (loc.x + 1) / 2;
    float normY = (1 - loc.y) / 2;
    return new Vector2f(normX, normY);
  }
}
