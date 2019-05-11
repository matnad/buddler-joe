package entities.items;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.systems.Magic;
import engine.particles.systems.Snow;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.NetPlayer;
import game.Game;
import game.NetPlayerMaster;
import gui.tutorial.Tutorial;
import net.packets.items.PacketItemUsed;
import org.joml.Vector3f;

public class Star extends Item {
  private static TexturedModel preloadedModel;
  private static final float freezeTime = 8f;
  private float time;
  private int itemId;
  private boolean freezeTriggered = false;

  private Magic starExplosion;
  private Snow snowEffect;

  /** Extended Constructor for Ice. Don't use directly. Use the Item Master to create items. */
  private Star(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(ItemMaster.ItemTypes.STAR, getPreloadedModel(), position, rotX, rotY, rotZ, 0);
    time = 0;

    /* Generate Fancy Particle Effects for an explosion */
    starExplosion = new Magic(200, 8, 0, .8f, 5);
    starExplosion.setScaleError(.4f);
    starExplosion.setSpeedError(.3f);
    starExplosion.setLifeError(.2f);

    // Generate Player Effect
    snowEffect = new Snow(2, 0, .1f, 1.5f, .4f);
    snowEffect.setScaleError(.2f);
    snowEffect.setLifeError(.6f);
    snowEffect.setSpeedError(.3f);
  }

  /**
   * Constructor for the Ice. Don't use directly. Use the Item Master to create items.
   *
   * @param position position to spawn the dynamite
   */
  Star(Vector3f position) {
    this(position, 0, 0, 0, .5f);
  }

  /**
   * The loader to load the model of the heart in the initialisation.
   *
   * @param loader the loader to be passed on to this method.
   */
  public static void init(Loader loader) {
    RawModel rawDynamite = loader.loadToVao(ObjFileLoader.loadObj("block"));
    setPreloadedModel(
        new TexturedModel(rawDynamite, new ModelTexture(loader.loadTexture("yellow"))));
  }

  private static TexturedModel getPreloadedModel() {
    return preloadedModel;
  }

  private static void setPreloadedModel(TexturedModel preloadedModel) {
    Star.preloadedModel = preloadedModel;
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
    time += Game.dt();
    if (!isOwned()) {
      if (time >= freezeTime) {
        Game.getActivePlayer().defreeze();
        setDestroyed(true);
      } else if (time >= 0.2f) {
        if (!freezeTriggered) {
          Tutorial.Topics.setActive(Tutorial.Topics.STARRED, true);
        }
        Game.getActivePlayer().freeze(!freezeTriggered);
        freezeTriggered = true;
      }
    } else {
      if (!freezeTriggered) {
        Game.getActivePlayer().playFreezeSound();
        freezeTriggered = true;
      }
      if (time >= freezeTime) {
        setDestroyed(true);
      }
    }

    if (time < .5f) {
      starExplosion.generateParticles(getPosition());
    }

    NetPlayer owner = NetPlayerMaster.getNetPlayerById(getOwner());
    for (NetPlayer netPlayer : NetPlayerMaster.getNetPlayers().values()) {
      if (netPlayer != owner) {
        snowEffect.makeItSnow(netPlayer, 20);
      }
    }
    if (!isOwned()) {
      snowEffect.makeItSnow(Game.getActivePlayer(), 20);
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

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public static float getFreezeTime() {
    return freezeTime;
  }
}
