package entities.blocks;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import org.joml.Vector3f;

/**
 * Dirt Block.
 *
 * <p>Holds methods and variables specific to Dirt Blocks.
 */
public class DirtBlock extends Block {

  private static float hardness = 0.9f;
  private static TexturedModel blockModel;

  /** Extended Constructor, dont call directly. */
  DirtBlock(
      Vector3f position, float rotX, float rotY, float rotZ, float scale, int gridX, int gridY) {
    // Must pass block type and hardness here as they are required
    super(
        BlockMaster.BlockTypes.DIRT, hardness, 1f, position, rotX, rotY, rotZ, scale, gridX, gridY);
    setModel(blockModel);
    setTextureIndex(0);
  }

  /** Shortened constructer with just position. Dont call directly. */
  DirtBlock(Vector3f position, int gridX, int gridY) {
    this(position, 0, 0, 0, 3, gridX, gridY);
  }

  static void init(Loader loader) {
    RawModel rawBlock = loader.loadToVao(ObjFileLoader.loadObj("dirt"));
    ModelTexture blockAtlas = new ModelTexture(loader.loadTexture("dirt4x4"));
    blockAtlas.setNumberOfRows(2);
    blockModel = new TexturedModel(rawBlock, blockAtlas);
  }

  @Override
  protected void onDestroy() {}

  public static float getHardness() {
    return hardness;
  }
}
