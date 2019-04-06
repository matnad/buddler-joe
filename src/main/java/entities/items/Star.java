package entities.items;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.systems.Explosion;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import game.Game;
import org.joml.Vector3f;

public class Star extends Item {
  private static TexturedModel preloadedModel;
  private final Explosion particleExplosion;
  private final float freezeTime = 10f;
  private float time;

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

  @Override
  public void update() {
    if (!isOwned()) {
      Game.getActivePlayer().freeze();
      // particleExplosion.generateParticles(getPosition());
      time += Game.window.getFrameTimeSeconds();
      if (time >= freezeTime) {
        Game.getActivePlayer().defreeze();
        setDestroyed(true);
      }
    } else {
      // particleExplosion.generateParticles(getPosition());
      time += Game.window.getFrameTimeSeconds();
      if (time >= freezeTime) {
        setDestroyed(true);
      }
    }
  }
  /**
   * The loader to load the model of the heart in the initialisation.
   *
   * @param loader the loader to be passed on to this method.
   */

  public static void init(Loader loader) {
    RawModel rawDynamite = loader.loadToVao(ObjFileLoader.loadObj("star"));
    setPreloadedModel(
        new TexturedModel(rawDynamite, new ModelTexture(loader.loadTexture("dynamite"))));
  }

  private static TexturedModel getPreloadedModel() {
    return preloadedModel;
  }

  private static void setPreloadedModel(TexturedModel preloadedModel) {
    Star.preloadedModel = preloadedModel;
  }
}
