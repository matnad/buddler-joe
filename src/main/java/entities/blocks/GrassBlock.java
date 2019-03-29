package entities.blocks;

import org.joml.Vector3f;

/**
 * Grass Block.
 *
 * <p>Holds methods and variables specific to Grass Blocks.
 */
public class GrassBlock extends Block {

  /** Extended Constructor, dont call directly. */
  GrassBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale, int gridX, int gridY) {
    super(BlockMaster.BlockTypes.GRASS, .7f, 1f, position, rotX, rotY, rotZ, scale, gridX, gridY);
  }

  /** Shortened constructor with just position. Dont call directly. */
  GrassBlock(Vector3f position, int gridX, int gridY) {
    this(position, 0, 0, 0, 3, gridX, gridY);
  }

  @Override
  protected void onDestroy() {}
}
