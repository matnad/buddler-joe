package entities.items;

import engine.models.TexturedModel;
import entities.Entity;
import org.joml.Vector3f;

/** Abstract Item Class. Provides getters and setters for mandatory properties. */
public abstract class Item extends Entity {

  private final ItemMaster.ItemTypes type;

  /**
   * Abstract Constructor.
   *
   * @param type Item Type as described by {@link ItemMaster.ItemTypes}
   * @param model Textured Model
   * @param position 3D coordinate for placement
   * @param rotX rotation on X axis
   * @param rotY rotation on Y axis
   * @param rotZ rotation on Z axis
   * @param scale scaling multiplier
   */
  Item(
      ItemMaster.ItemTypes type,
      TexturedModel model,
      Vector3f position,
      float rotX,
      float rotY,
      float rotZ,
      float scale) {
    super(model, position, rotX, rotY, rotZ, scale);
    if (model == null) {
      System.out.println("WARNING! No model preloaded!");
    }
    this.type = type;
  }

  public abstract void update();

  public ItemMaster.ItemTypes getType() {
    return type;
  }
}
