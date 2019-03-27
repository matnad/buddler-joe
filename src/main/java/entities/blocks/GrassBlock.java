package entities.blocks;

import org.joml.Vector3f;

/**
 * Grass Block.
 *
 * <p>Holds methods and variables specific to Grass Blocks.
 */
public class GrassBlock extends Block {

  /** Extended Constructor, dont call directly. */
  GrassBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(BlockMaster.BlockTypes.GRASS, .7f, 1f, position, rotX, rotY, rotZ, scale);
  }

  /** Shortened constructor with just position. Dont call directly. */
  GrassBlock(Vector3f position) {
    this(position, 0, 0, 0, 3);
  }

  @Override
  protected void onDestroy() {}
}
