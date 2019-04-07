package entities.items;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.systems.Explosion;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import game.Game;
import net.packets.items.PacketItemUsed;
import org.joml.Vector3f;

public class Star extends Item {
  private static TexturedModel preloadedModel;
  private final Explosion particleExplosion;
  private final float freezeTime = 10f;
  private float time;
  private int itemId;

  /** Extended Constructor for Ice. Don't use directly. Use the Item Master to create items. */
  private Star(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(ItemMaster.ItemTypes.STAR, getPreloadedModel(), position, rotX, rotY, rotZ, scale);
    time = 0;

    /* Generate Fancy Particle Effects for an explosion */
    // Generate Explosion Effect
    particleExplosion = new Explosion(200, 11, 0, .4f, 15);
    particleExplosion.setScaleError(.4f);
    particleExplosion.setSpeedError(.3f);
    particleExplosion.setLifeError(.2f);
  }

  /**
   * Constructor for the Ice. Don't use directly. Use the Item Master to create items.
   *
   * @param position position to spawn the dynamite
   */
  Star(Vector3f position) {
    this(position, 0, 0, 0, 1);
  }

  /**
   * Method to update the ice and show the item for ten seconds.
   *
   * <p>The time is calculated by the number of frames per second. The star instantly has an effect
   * on the other players and freezes them for then seconds and the star object is shown for ten
   * seconds.
   */
  @Override
  public void update() {
    if (!isOwned()) {
      Game.getActivePlayer().freeze();
      time += Game.window.getFrameTimeSeconds();
      if (time >= freezeTime) {
        Game.getActivePlayer().defreeze();
        setDestroyed(true);
      }
    } else {
      time += Game.window.getFrameTimeSeconds();
      if (time >= freezeTime) {
        setDestroyed(true);
      }
    }
  }
  /**
   * Method to set the star as destroyed and to delete it from the ServerItemState.
   *
   * @param destroyed Boolean whether the item is destroyed or not.
   */

  @Override
  public void setDestroyed(boolean destroyed) {
    super.setDestroyed(destroyed);
    if (isOwned()) {
      PacketItemUsed packetItemUsed = new PacketItemUsed(itemId);
      packetItemUsed.sendToServer();
    }
  }

  /**
   * The loader to load the model of the heart in the initialisation.
   *
   * @param loader the loader to be passed on to this method.
   */
  public static void init(Loader loader) {
    RawModel rawDynamite = loader.loadToVao(ObjFileLoader.loadObj("dynamite"));
    setPreloadedModel(
        new TexturedModel(rawDynamite, new ModelTexture(loader.loadTexture("dynamite"))));
  }

  private static TexturedModel getPreloadedModel() {
    return preloadedModel;
  }

  private static void setPreloadedModel(TexturedModel preloadedModel) {
    Star.preloadedModel = preloadedModel;
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }
}
