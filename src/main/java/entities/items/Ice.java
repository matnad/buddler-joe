package entities.items;

import engine.io.InputHandler;
import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.Player;
import entities.light.Light;
import entities.light.LightMaster;
import game.Game;
import org.joml.Vector3f;

public class Ice extends Item {

  private static TexturedModel preloadedModel;
  private float time;
  private boolean iced;
  private final float freezeTime = 10f;

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
    if (isOwned()) {
      Game.getActivePlayer().freeze();
      time += Game.window.getFrameTimeSeconds();
      if (time >= freezeTime) {
        Game.getActivePlayer().defreeze();
        setDestroyed(true);
      }
    } else {
      if (time >= freezeTime) {
        time += Game.window.getFrameTimeSeconds();
        setDestroyed(true);
      }
    }
  }

  public static void init(Loader loader) {
    RawModel rawDynamite = loader.loadToVao(ObjFileLoader.loadObj("dynamite"));
    setPreloadedModel(
            new TexturedModel(rawDynamite, new ModelTexture(loader.loadTexture("dynamite"))));
  }

  private static TexturedModel getPreloadedModel() {
    return preloadedModel;
  }

  private static void setPreloadedModel(TexturedModel preloadedModel) {
    Ice.preloadedModel = preloadedModel;
  }

}
