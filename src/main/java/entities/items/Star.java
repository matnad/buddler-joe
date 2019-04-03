package entities.items;

import engine.models.TexturedModel;
import engine.render.Loader;
import entities.light.Light;
import org.joml.Vector3f;

public class Star extends Item {
  private static TexturedModel preloadedModel;
  private final float gravity = 20;
  private float time;
  private boolean active;
  private boolean starred;
  private Light flash;

  /** Extended Constructor for Ice. Don't use directly. Use the Item Master to create items. */
  private Star(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(ItemMaster.ItemTypes.STAR, getPreloadedModel(), position, rotX, rotY, rotZ, scale);
  }

  /**
   * Constructor for the Ice. Don't use directly. Use the Item Master to create items.
   *
   * @param position position to spawn the dynamite
   */
  Star(Vector3f position) {
    this(position, 0, 0, 0, 1);
  }

  @Override
  public void update() {

    // Skip if ice is being placed or otherwise inactive
    if (!active) {
      return;
    }
  }

  public static void init(Loader loader) {
    // RawModel rawStar = loader.loadToVao(ObjFileLoader.loadObj("star"));
    // setPreloadedModel(new TexturedModel(rawStar, new ModelTexture(loader.loadTexture("star"))));
  }

  private static TexturedModel getPreloadedModel() {
    return preloadedModel;
  }

  private static void setPreloadedModel(TexturedModel preloadedModel) {
    Star.preloadedModel = preloadedModel;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
