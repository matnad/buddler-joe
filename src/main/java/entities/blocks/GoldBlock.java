package entities.blocks;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.Player;
import game.Game;
import game.stages.Playing;
import org.joml.Vector3f;

/**
 * Gold Block.
 *
 * <p>Holds methods and variables specific to Gold Blocks.
 */
public class GoldBlock extends Block {

  private static float hardness = 1f;
  private static TexturedModel blockModel;
  private static TexturedModel debrisModel;
  private int value;

  /** Extended Constructor, dont call directly. */
  GoldBlock(
      Vector3f position, float rotX, float rotY, float rotZ, float scale, int gridX, int gridY) {
    super(
        blockModel,
        BlockMaster.BlockTypes.GOLD,
        hardness,
        3f,
        position,
        rotX,
        rotY,
        rotZ,
        scale,
        gridX,
        gridY);
    setModel(blockModel);
    setTextureIndex(0);
    value = 50 + gridY * 5;
  }

  /** Shortened constructor with just position. Dont call directly. */
  GoldBlock(Vector3f position, int gridX, int gridY) {
    this(position, 0, 0, 0, 3, gridX, gridY);
  }

  static void init(Loader loader) {
    RawModel rawBlock = loader.loadToVao(ObjFileLoader.loadObj("block"));
    ModelTexture blockAtlas = new ModelTexture(loader.loadTexture("gold4x4"));
    blockAtlas.setNumberOfRows(2);
    blockModel = new TexturedModel(rawBlock, blockAtlas);

    // Replace Gold Nuggets with their own texture?
    // ModelTexture goldTexture = new ModelTexture(loader.loadTexture("pureGold"));
    // goldTexture.setShineDamper(0.5f);
    // debrisModel = new TexturedModel(rawBlock, goldTexture);

  }

  public static float getHardness() {
    return hardness;
  }

  @Override
  protected void onDestroy() {
    if (getDestroyedBy() == Game.getActivePlayer()) {
      //  new Timer()
      //      .schedule(
      //          new TimerTask() {
      //            @Override
      //            public void run() {
      //              ((ServerPlayer) getDestroyedBy()).increaseCurrentGold(value);
      //              Playing.addFloatingGoldText(value);
      //            }
      //          },
      //          1100);
      ((Player) getDestroyedBy()).increaseCurrentGold(value);
      //Playing.addFloatingGoldText(value);
    }
  }

  @Override
  public TexturedModel getDebrisModel() {
    return blockModel;
  }
}
