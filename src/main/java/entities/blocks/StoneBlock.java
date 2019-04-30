package entities.blocks;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import org.joml.Vector3f;

/**
 * Stone Block
 *
 * <p>Holds methods and variables specific to Stone Blocks.
 */
public class StoneBlock extends Block {

  private static float hardness = 7f;
  private static TexturedModel blockModel;

  /** Extended Constructor, dont call directly. */
  StoneBlock(
      Vector3f position, float rotX, float rotY, float rotZ, float scale, int gridX, int gridY) {
    super(
        blockModel,
        BlockMaster.BlockTypes.STONE,
        hardness,
        2f,
        position,
        rotX,
        rotY,
        rotZ,
        scale,
        gridX,
        gridY);
    setModel(blockModel);
    setTextureIndex(0);
  }

  /** Shortened constructor with just position. Dont call directly. */
  StoneBlock(Vector3f position, int gridX, int gridY) {
    this(position, 0, 0, 0, 3, gridX, gridY);
  }

  static void init(Loader loader) {
    RawModel rawBlock = loader.loadToVao(ObjFileLoader.loadObj("block"));
    ModelTexture blockAtlas = new ModelTexture(loader.loadTexture("stone4x4"));
    blockAtlas.setNumberOfRows(2);
    blockModel = new TexturedModel(rawBlock, blockAtlas);
  }

  public static float getHardness() {
    return hardness;
  }

  public static TexturedModel getBlockModel() {
    return blockModel;
  }

  @Override
  protected void onDestroy() {}

  @Override
  public TexturedModel getDebrisModel() {
    return blockModel;
  }
}
