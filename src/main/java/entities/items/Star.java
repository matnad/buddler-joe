package entities.items;

import engine.models.TexturedModel;
import engine.render.Loader;
import game.Game;
import org.joml.Vector3f;


public class Star extends Item {
  private static TexturedModel preloadedModel;
  private final float freezeTime = 10f;
  private float time;

  /** Extended Constructor for Ice. Don't use directly. Use the Item Master to create items. */
  private Star(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(ItemMaster.ItemTypes.STAR, getPreloadedModel(), position, rotX, rotY, rotZ, scale);
    time = 0;
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
    if (!isOwned()) {
      Game.getActivePlayer().freeze();
      time += Game.window.getFrameTimeSeconds();
      if (time >= freezeTime) {
        Game.getActivePlayer().defreeze();
      }
    } else {
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

}
