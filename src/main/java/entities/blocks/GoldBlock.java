package entities.blocks;

import org.joml.Vector3f;

/**
 * Gold Block.
 *
 * <p>Holds methods and variables specific to Gold Blocks.
 */
public class GoldBlock extends Block {

  private static float hardness = 2f;

  /** Extended Constructor, dont call directly. */
  GoldBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale, int gridX, int gridY) {
    super(BlockMaster.BlockTypes.GOLD, hardness, 3f, position, rotX, rotY, rotZ, scale, gridX, gridY);
  }

  /** Shortened constructor with just position. Dont call directly. */
  GoldBlock(Vector3f position, int gridX, int gridY) {
    this(position, 0, 0, 0, 3, gridX, gridY);
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
