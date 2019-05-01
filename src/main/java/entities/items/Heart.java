package entities.items;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.systems.Sparkle;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.NetPlayer;
import game.Game;
import game.NetPlayerMaster;
import net.packets.items.PacketItemUsed;
import net.packets.life.PacketLifeStatus;
import org.joml.Vector3f;

public class Heart extends Item {

  private static TexturedModel preloadedModel;
  private final float showTime = 6f;
  private float time;
  private int itemId;

  private NetPlayer pickedUpBy;
  private boolean pickedUp;
  private final Sparkle sparkle;


  /** Extended Constructor for Heart. Don't use directly. Use the Item Master to create items. */
  private Heart(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(ItemMaster.ItemTypes.HEART, getPreloadedModel(), position, rotX, rotY, rotZ, scale);

    // Generate Sparkle Effect
    sparkle = new Sparkle(30, 1, -.02f, 4f, .7f);
    sparkle.setScaleError(.2f);
    sparkle.setLifeError(.6f);
    sparkle.setSpeedError(.3f);
    sparkle.setDirection(new Vector3f(0, 1, .3f), .5f);
    // sparkle.randomizeRotation();

    pickedUp = false;
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

    if (collidesWith(Game.getActivePlayer()) && !pickedUp) {
      if (Game.getActivePlayer().increaseCurrentLives()) {
        setPickedUpBy(Game.getActivePlayer());
      }
    }

    for (NetPlayer netPlayer : NetPlayerMaster.getNetPlayers().values()) {
      if (collidesWith(netPlayer) && !pickedUp) {
        setPickedUpBy(netPlayer);
      }
    }

    if (pickedUp && pickedUpBy != null) {
      setPosition(pickedUpBy.getBbox().getCenter());
    } else {
      setRotY((float) (getRotY() + 90 * Game.dt()) % 360);
    }

    time += Game.dt();
    if (time >= showTime) {
      setDestroyed(true);
    }

    if (time + 1.5f < showTime) {
      sparkle.generateParticles(
          new Vector3f(getPosition()).add(new Vector3f(0, getBbox().getDimY() / 2, 0)));
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

  private void setPickedUpBy(NetPlayer pickedUpBy) {
    this.pickedUpBy = pickedUpBy;
    pickedUp = true;
    setScale(new Vector3f(0,0,0));
  }
}
