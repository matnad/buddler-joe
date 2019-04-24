package entities.items;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import game.Game;
import net.packets.items.PacketItemUsed;
import org.joml.Vector3f;

public class Heart extends Item {

  private static TexturedModel preloadedModel;
  private final float showTime = 3f;
  private float time;
  private int itemId;

  /** Extended Constructor for Heart. Don't use directly. Use the Item Master to create items. */
  private Heart(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(ItemMaster.ItemTypes.HEART, getPreloadedModel(), position, rotX, rotY, rotZ, scale);
  }

  /**
   * Constructor for the Heart. Don't use directly. Use the Item Master to create items.
   *
   * @param position position to spawn the dynamite
   */
  Heart(Vector3f position) {
    this(position, 0, 0, -90, .1f);
  }

  /**
   * The loader to load the model of the heart in the initialisation.
   *
   * @param loader the loader to be passed on to this method.
   */
  public static void init(Loader loader) {
    RawModel rawHeart = loader.loadToVao(ObjFileLoader.loadObj("heart"));
    setPreloadedModel(new TexturedModel(rawHeart, new ModelTexture(loader.loadTexture("red"))));
  }

  private static TexturedModel getPreloadedModel() {
    return preloadedModel;
  }

  private static void setPreloadedModel(TexturedModel preloadedModel) {
    Heart.preloadedModel = preloadedModel;
  }

  /**
   * Method to update the heart and show the item for five seconds.
   *
   * <p>The time is calculated by the number of frames per second. The heart instantly has an effect
   * and the heart object is shown for five seconds.
   */
  @Override
  public void update() {
    if (isOwned()) {
      time += Game.window.getFrameTimeSeconds();
      if (time >= showTime) {
        Game.getActivePlayer().increaseCurrentLives();
        setDestroyed(true);
      }
    } else {
      time += Game.window.getFrameTimeSeconds();
      if (time >= showTime) {
        setDestroyed(true);
      }
    }
  }

  /**
   * Method to set the heart as destroyed and to delete it from the ServerItemState.
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

  public void setActive() {}

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }
}
