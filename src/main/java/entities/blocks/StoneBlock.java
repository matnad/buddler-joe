package entities.blocks;

import org.joml.Vector3f;

/**
 * Stone Block
 *
 * <p>Holds methods and variables specific to Stone Blocks.
 */
public class StoneBlock extends Block {

  private static float hardness = 7f;

  /** Extended Constructor, dont call directly. */
  StoneBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale, int gridX, int gridY) {
    super(BlockMaster.BlockTypes.STONE, hardness, 2f, position, rotX, rotY, rotZ, scale, gridX, gridY);
  }

  /** Shortened constructor with just position. Dont call directly. */
  StoneBlock(Vector3f position, int gridX, int gridY) {
    this(position, 0, 0, 0, 3, gridX, gridY);
  }

  @Override
  protected void onDestroy() {}

  public static float getHardness() {
    return hardness;
  }
}
