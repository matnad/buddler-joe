package entities.items;

import engine.models.TexturedModel;
import entities.Entity;
import game.SettingsSerialiser;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Abstract Item Class. Provides getters and setters for mandatory properties. */
public abstract class Item extends Entity {

  public static final Logger logger = LoggerFactory.getLogger(Item.class);

  private final ItemMaster.ItemTypes type;
  private boolean owned;
  private int owner;
  private int key;

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
      logger.warn("WARNING! No model preloaded!");
    }
    this.type = type;
    this.key++;
    this.owned = false;
  }

  public abstract void update();

  public boolean isOwned() {
    return owned;
  }

  public void setOwned(boolean owned) {
    this.owned = owned;
  }

  public int getOwner() {
    return owner;
  }

  public void setOwner(int owner) {
    this.owner = owner;
  }

  public ItemMaster.ItemTypes getType() {
    return type;
  }

  public int getKey() {
    return key;
  }

  public void setKey(int key) {
    this.key = key;
  }
}
