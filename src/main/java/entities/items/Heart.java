package entities.items;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import org.joml.Vector3f;

public class Heart extends Item {

  private static TexturedModel preloadedModel;
  private final float gravity = 20;
  private float time;
  private boolean active;

  /** Extended Constructor for Dynamite. Don't use directly. Use the Item Master to create items. */
  private Heart(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(ItemMaster.ItemTypes.HEART, getPreloadedModel(), position, rotX, rotY, rotZ, scale);
  }

  /**
   * Constructor for the dynamite. Don't use directly. Use the Item Master to create items.
   *
   * @param position position to spawn the dynamite
   */
  Heart(Vector3f position) {
    this(position, 0, 0, 0, 1);
  }

  @Override
  public void update() {

    // Skip if heart is being placed or otherwise inactive
    if (!active) {
      return;
    }
  }

  public static void init(Loader loader) {
    RawModel rawDynamite = loader.loadToVao(ObjFileLoader.loadObj("heart"));
    setPreloadedModel(
        new TexturedModel(rawDynamite, new ModelTexture(loader.loadTexture("heart"))));
  }

  private static TexturedModel getPreloadedModel() {
    return preloadedModel;
  }

  private static void setPreloadedModel(TexturedModel preloadedModel) {
    Heart.preloadedModel = preloadedModel;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  //TODO: (Viktor) write method give heart to give the destroyer of the block a heart
}
