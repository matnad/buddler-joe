package entities.items;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import game.Game;
import net.packets.items.PacketItemUsed;
import org.joml.Vector3f;

public class Ice extends Item {

  private static TexturedModel preloadedModel;
  private final float freezeTime = 8f;
  private float time;
  private int itemId;
  private boolean freezeTriggered = false;

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
    this(position, 0, 0, 0, .5f);
  }

  /**
   * Method to preload the textures of the item to then be loaded on the GUI later on.
   *
   * @param loader The current loader passed on by ItemMaster.
   */
  public static void init(Loader loader) {
    RawModel rawDynamite = loader.loadToVao(ObjFileLoader.loadObj("block"));
    setPreloadedModel(
        new TexturedModel(rawDynamite, new ModelTexture(loader.loadTexture("lightblue"))));
  }

  private static TexturedModel getPreloadedModel() {
    return preloadedModel;
  }

  private static void setPreloadedModel(TexturedModel preloadedModel) {
    Ice.preloadedModel = preloadedModel;
  }

  /**
   * Method to update the ice and show the item for ten seconds.
   *
   * <p>The time is calculated by the number of frames per second. The ice instantly has an effect
   * on the owner and freezes the owner for then seconds and the ice object is shown for ten
   * seconds.
   */
  @Override
  public void update() {
    time += Game.dt();
    if (isOwned()) {
      if (time >= freezeTime) {
        Game.getActivePlayer().defreeze();
        setDestroyed(true);
      } else if (time >= 0.2f) {
        Game.getActivePlayer().freeze(!freezeTriggered);
        freezeTriggered = true;
      }
    } else {
      if (time >= freezeTime) {
        setDestroyed(true);
      }
    }
  }

  /**
   * Method to set the ice as destroyed and to delete it from the ServerItemState.
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

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }
}
