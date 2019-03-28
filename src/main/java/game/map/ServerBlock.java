package game.map;

import entities.blocks.BlockMaster;
import entities.blocks.DirtBlock;
import entities.blocks.GoldBlock;
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
        hardness = GoldBlock.getHardness();
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

  @Override
  public String toString() {
    return getType().toString();
  }
}
