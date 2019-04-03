package game.map;

import entities.blocks.BlockMaster;
import entities.blocks.DirtBlock;
import entities.blocks.GoldBlock;
import entities.blocks.GrassBlock;
import entities.blocks.StoneBlock;
import net.ServerLogic;

public class ServerBlock {

  private BlockMaster.BlockTypes type;
  private float hardness;
  private int gridX;
  private int gridY;
  private int goldValue;

  ServerBlock(BlockMaster.BlockTypes type, int gridX, int gridY) {
    this.gridX = gridX;
    this.gridY = gridY;
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

    if (type == BlockMaster.BlockTypes.GOLD) {
      goldValue = 50 + 5 * gridY;
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
   * @param clientThatDealsDamage clientId that damaged the block
   */
  public void damageBlock(int clientThatDealsDamage, float damage) {
    if (type == BlockMaster.BlockTypes.AIR) {
      return;
    }

    hardness -= damage;
    if (hardness < 0) {
      this.type = BlockMaster.BlockTypes.AIR;
      ServerLogic.getPlayerList().getPlayer(clientThatDealsDamage).increaseCurrentGold(goldValue);
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
