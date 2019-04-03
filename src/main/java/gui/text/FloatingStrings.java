package gui.text;

import collision.BoundingBox;
import game.Game;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Maths;

public class FloatingStrings {

  private BoundingBox bbox;

  private float lifeTime;
  private float speed = 3f; // Screen Units per second

  private List<ChangableGuiText> floatingStrings;

  public FloatingStrings(BoundingBox bbox, float lifeTime) {
    this.bbox = bbox;
    this.lifeTime = lifeTime;
    floatingStrings = new CopyOnWriteArrayList<>();
  }

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

  private Vector2f getPos2D(float offsetX, float offsetY) {
    Vector3f pos3D =
        new Vector3f(bbox.getMaxX() - bbox.getDimX() / 2 + offsetX, bbox.getMaxY() + offsetY, 3);
    Vector2f pos2D = Maths.worldToScreen(pos3D, Game.getActiveCamera());
    return pos2D;
  }

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
        floatingString.setOffsetY(
            (float) (floatingString.getOffsetY() + speed * Game.window.getFrameTimeSeconds()));
      }
    }
  }
}
