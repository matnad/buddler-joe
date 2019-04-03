package game.map;

import entities.blocks.BlockMaster;
import entities.blocks.DirtBlock;
import entities.blocks.GoldBlock;
import entities.blocks.GrassBlock;
import entities.blocks.StoneBlock;

public class ServerBlock {

  private BlockMaster.BlockTypes type;
  private float hardness;

  ServerBlock(BlockMaster.BlockTypes type) {
    this.type = type;
    switch (type) {
      case DIRT:
        hardness = DirtBlock.getHardness();
        break;
      case STONE:
        hardness = StoneBlock.getHardness();
        break;
      case GOLD:
        hardness = GoldBlock.getHardness();
        break;
      case GRASS:
        hardness = GrassBlock.getHardness();
        break;
      case AIR:
        hardness = 0;
        break;
      default:
        hardness = 0;
    }
  }

  public BlockMaster.BlockTypes getType() {
    return type;
  }

  public float getHardness() {
    return hardness;
  }

  /**
   * Damage a block.
   *
   * @param damage damage to deal to a block
   */
  public void damageBlock(float damage) {
    hardness -= damage;
    if (hardness < 0) {
      this.type = BlockMaster.BlockTypes.AIR;
    }
  }

  /**
   * Get Base hardness of this block's type.
   *
   * @return starting hardness of this block's type.
   */
  public float getBaseHardness() {
    switch (type) {
      case DIRT:
        return DirtBlock.getHardness();
      case STONE:
        return StoneBlock.getHardness();
      case GOLD:
        return GoldBlock.getHardness();
      case GRASS:
        return GrassBlock.getHardness();
      case AIR:
        return 0;
      default:
        return 0;
    }
  }

  @Override
  public String toString() {
    return getType().toString();
  }
}
