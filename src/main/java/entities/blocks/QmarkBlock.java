package entities.blocks;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.items.Dynamite;
import entities.items.Item;
import entities.items.ItemMaster;
import org.joml.Random;
import org.joml.Vector3f;

public class QmarkBlock extends Block {

  private static float hardness = 1f;
  private static TexturedModel blockModel;

  QmarkBlock(
      Vector3f position, float rotX, float rotY, float rotZ, float scale, int gridX, int gridY) {
    super(
        BlockMaster.BlockTypes.QMARK,
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
  QmarkBlock(Vector3f position, int gridX, int gridY) {
    this(position, 0, 0, 0, 3, gridX, gridY);
  }

  static void init(Loader loader) {
    RawModel rawBlock = loader.loadToVao(ObjFileLoader.loadObj("dirt"));
    ModelTexture blockAtlas = new ModelTexture(loader.loadTexture("item4x4"));
    blockAtlas.setNumberOfRows(2);
    blockModel = new TexturedModel(rawBlock, blockAtlas);
  }

  @Override
  protected void onDestroy() {}

  public static float getHardness() {
    return hardness;
  }
}
