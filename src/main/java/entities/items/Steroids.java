package entities.items;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.systems.Amped;
import engine.particles.systems.Explosion;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.NetPlayer;
import game.Game;
import game.NetPlayerMaster;
import java.util.Random;
import net.packets.items.PacketItemUsed;
import org.joml.Vector3f;

public class Steroids extends Item {

  private static TexturedModel preloadedModel;
  private static final float steroidsTime = 10f;
  private static final float MOVEMENT_MULTIPLIER = 2f;
  private static final float DIG_DAMAGE_MULTIPLIER = 10f;
  private static final float JUMP_POWER_MULTIPLIER = 1.5f;
  private float time;
  private int itemId;

  private Amped ampedPlayerEffect;
  private Explosion ampedInitialEffect;

  /** Extended Constructor for Ice. Don't use directly. Use the Item Master to create items. */
  private Steroids(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(ItemMaster.ItemTypes.STEROIDS, getPreloadedModel(), position, rotX, rotY, rotZ, 0);

    ampedPlayerEffect = new Amped(40, 0, 0, 1f, 10);
    ampedPlayerEffect.setScaleError(.3f);

    ampedInitialEffect = new Explosion(1000, 40, 0f, .8f, 5f);
    ampedInitialEffect.setScaleError(.1f);
    ampedInitialEffect.setLifeError(.1f);
    ampedInitialEffect.setDirection(new Vector3f(1, 0, 0), 0);
    Random rnd = new Random(); // Give the shockwave a slight random tilt
    ampedInitialEffect.setRotationAxis(
        new Vector3f(rnd.nextFloat() * .4f - .2f, 1, rnd.nextFloat() * .4f - .2f), 0);
  }

  /**
   * Constructor for the Ice. Don't use directly. Use the Item Master to create items.
   *
   * @param position position to spawn the dynamite
   */
  Steroids(Vector3f position) {
    this(position, 0, 0, 0, 0f);
  }

  /**
   * Method to preload the textures of the item to then be loaded on the GUI later on.
   *
   * @param loader The current loader passed on by ItemMaster.
   */
  public static void init(Loader loader) {
    RawModel rawDynamite = loader.loadToVao(ObjFileLoader.loadObj("block"));
    setPreloadedModel(new TexturedModel(rawDynamite, new ModelTexture(loader.loadTexture("red"))));
  }

  private static TexturedModel getPreloadedModel() {
    return preloadedModel;
  }

  private static void setPreloadedModel(TexturedModel preloadedModel) {
    Steroids.preloadedModel = preloadedModel;
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
      if (time + 9.9f < steroidsTime) {
        if (Game.getActivePlayer().getSteroidIsPlaying()) {
          Game.getActivePlayer().setSteroidSoundOff();
        }
        Game.getActivePlayer().playSteroidSound();
      }
      // Stop sound when getting frozen
      if (Game.getActivePlayer().isFrozen()) {
        Game.getActivePlayer().setSteroidSoundOff();
        Game.getActivePlayer().playSteamSound();
      }
    }

    if (time >= steroidsTime) {
      // DeAmp is done by the player. We can't control multiple steroids from here.
      setDestroyed(true);
    }

    if (time < .2f) {
      ampedInitialEffect.generateParticles(getPosition());
    }

    NetPlayer owner = NetPlayerMaster.getNetPlayerById(getOwner());
    if (owner != null) {
      // Destroy/End item when a player gets freezed
      if (owner.isFrozen()) {
        setDestroyed(true);
      } else {
        ampedPlayerEffect.generateParticles(owner.getBbox().getCenter().add(new Vector3f(0, 1, 0)));
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

  public static float getSteroidsTime() {
    return steroidsTime;
  }

  public static float getMovementMultiplier() {
    return MOVEMENT_MULTIPLIER;
  }

  public static float getDigDamageMultiplier() {
    return DIG_DAMAGE_MULTIPLIER;
  }

  public static float getJumpPowerMultiplier() {
    return JUMP_POWER_MULTIPLIER;
  }
}
