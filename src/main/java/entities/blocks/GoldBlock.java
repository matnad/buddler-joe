package entities.blocks;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import org.joml.Vector3f;

/**
 * Gold Block.
 *
 * <p>Holds methods and variables specific to Gold Blocks.
 */
public class GoldBlock extends Block {

  private static float hardness = 2f;
  private static TexturedModel blockModel;

  /** Extended Constructor, dont call directly. */
  GoldBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(BlockMaster.BlockTypes.GOLD, hardness, 3f, position, rotX, rotY, rotZ, scale);
    setModel(blockModel);
    setTextureIndex(0);
  }

  /** Shortened constructor with just position. Dont call directly. */
  GoldBlock(Vector3f position, int gridX, int gridY) {
    this(position, 0, 0, 0, 3, gridX, gridY);
  }

  static void init(Loader loader) {
    RawModel rawBlock = loader.loadToVao(ObjFileLoader.loadObj("dirt"));
    ModelTexture blockAtlas = new ModelTexture(loader.loadTexture("gold4x4"));
    blockAtlas.setNumberOfRows(2);
    blockModel = new TexturedModel(rawBlock, blockAtlas);
  }

  @Override
  protected void onDestroy() {
    // Drop some dynamite!
    // Item dynamite = ItemMaster.generateItem(ItemMaster.ItemTypes.DYNAMITE, getPosition());
    // ((Dynamite) dynamite).setActive(true);
  }

  public static float getHardness() {
    return hardness;
  }
}
