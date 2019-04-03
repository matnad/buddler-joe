package entities.items;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.light.Light;
import entities.light.LightMaster;
import game.Game;
import org.joml.Vector3f;

public class Ice extends Item {

  private static TexturedModel preloadedModel;
  private final float gravity = 20;
  private float time;
  private boolean active;
  private boolean iced;
  private Light flash;

  /** Extended Constructor for Ice. Don't use directly. Use the Item Master to create items. */
  private Ice(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(ItemMaster.ItemTypes.ICE, getPreloadedModel(), position, rotX, rotY, rotZ, scale);
  }

  /**
   * Constructor for the Ice. Don't use directly. Use the Item Master to create items.
   *
   * @param position position to spawn the dynamite
   */
  Ice(Vector3f position) {
    this(position, 0, 0, 0, 1);
  }

  @Override
  public void update() {

    // Skip if ice is being placed or otherwise inactive
    if (!active) {
      return;
    }
  }

  /** Damage the blocks in range of the explosion and hide the dynamite. */
  private void explode() {
    if (iced) {
      return;
    }
    iced = true;
    setScale(new Vector3f()); // Hide the model, but keep the object for the explosion effect
    flash =
        LightMaster.generateLight(
            LightMaster.LightTypes.FLASH, getPosition(), new Vector3f(1, 1, 1));
    if (Game.isConnectedToServer()) {
      // Send to players that there has been a star
    }
  }

  public static void init(Loader loader) {
    // RawModel rawIce = loader.loadToVao(ObjFileLoader.loadObj("ice"));
    // setPreloadedModel(new TexturedModel(rawIce, new ModelTexture(loader.loadTexture("ice"))));
  }

  private static TexturedModel getPreloadedModel() {
    return preloadedModel;
  }

  private static void setPreloadedModel(TexturedModel preloadedModel) {
    Ice.preloadedModel = preloadedModel;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
