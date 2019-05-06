package gui.text;

import entities.collision.BoundingBox;
import game.Game;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Maths;

/** A container for floating Strings that will fade up and away. */
public class FloatingStrings {

  private BoundingBox bbox;

  private float lifeTime;
  private float speed = 3f; // World Coordinate Units per second

  private List<ChangableGuiText> floatingStrings;

  /**
   * Generate a new container for floating strings. Strings will fade out over their lifetime and
   * float upwards. The origin is on top of the specified bounding box with some random offsets.
   *
   * <p>Note: Might add more parameters later if we re-use this class.
   *
   * @param bbox the bounding box of the entity above which the text should be displayed.
   * @param lifeTime the duration over which the texts will fade out
   */
  public FloatingStrings(BoundingBox bbox, float lifeTime) {
    this.bbox = bbox;
    this.lifeTime = lifeTime;
    floatingStrings = new CopyOnWriteArrayList<>();
  }

  /**
   * Add a string to the container. The string will spawn in a random spot above the container box.
   * The strings will be cleaned up after their lifetime is expended.
   *
   * @param text the text to be displayed by the string
   */
  public void addString(String text) {

    ChangableGuiText guiText = new ChangableGuiText(text, getPos2D());
    guiText.setTextColour(new Vector3f(255, 165, 0));
    guiText.setMaxLineLength(0.3f);
    guiText.setCentered(true);
    guiText.setFontSize(1.1f);
    guiText.setOffsetX(new Random().nextFloat() * 8f - 4f);
    guiText.setOffsetY(new Random().nextFloat() * 4);
    floatingStrings.add(guiText);
  }

  private Vector2f getPos2D() {
    return getPos2D(0, 0);
  }

  /**
   * Convert the 3D position with XY offsets into a 2D screeen position.
   *
   * @param offsetX X-shift of the text
   * @param offsetY Y-shift of the text
   * @return 2D screen position coordinates for the text to be displayed with the given offsets
   */
  private Vector2f getPos2D(float offsetX, float offsetY) {
    Vector3f pos3D =
        new Vector3f(bbox.getMaxX() - bbox.getDimX() / 2 + offsetX, bbox.getMaxY() + offsetY, 3);
    return Maths.worldToScreen(pos3D, Game.getActiveCamera());
  }

  /**
   * Move the string upwards and fade it out. Eventually remove the string and clean it up. Object
   * needs to be re-created each frame.
   */
  public void update() {
    for (ChangableGuiText floatingString : floatingStrings) {
      if (floatingString.getGuiText() == null) {
        floatingString.createGuiText();
      }
      float elapsed = (System.currentTimeMillis() - floatingString.getCreatedAt()) / 1000f;
      if (elapsed >= lifeTime) {
        floatingString.delete();
        floatingStrings.remove(floatingString);
      } else {
        floatingString.setPosition(
            getPos2D(floatingString.getOffsetX(), floatingString.getOffsetY()));
        float alpha = 1 - elapsed / lifeTime;
        floatingString.setAlpha(alpha);
        floatingString.updateString();
        floatingString.setOffsetY((float) (floatingString.getOffsetY() + speed * Game.dt()));
      }
    }
  }

  /** Delete all texts immediately. */
  public void done() {
    for (ChangableGuiText floatingString : floatingStrings) {
      floatingString.delete();
    }
  }
}
