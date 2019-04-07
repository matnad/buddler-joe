package game.map;

import entities.blocks.BlockMaster;
import entities.blocks.DirtBlock;
import entities.blocks.GoldBlock;
import entities.blocks.GrassBlock;
import entities.blocks.QmarkBlock;
import entities.blocks.StoneBlock;
import entities.items.ItemMaster;
import java.util.Random;
import net.ServerLogic;
import net.packets.items.PacketSpawnItem;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerBlock {

  private static final Logger logger = LoggerFactory.getLogger(PacketSpawnItem.class);
  private final int gridZ = Map.getSize();
  private BlockMaster.BlockTypes type;
  private float hardness;
  private int gridX;
  private int gridY;
  private int goldValue;

  ServerBlock(BlockMaster.BlockTypes type, int gridX, int gridY) {
    this.gridX = gridX;
    this.gridY = gridY;
    this.type = type;
    this.hardness = getBaseHardness();
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
      if (this.type == BlockMaster.BlockTypes.QMARK) {
        onQmarkDestroy(clientThatDealsDamage);
      }
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
      case QMARK:
        return QmarkBlock.getHardness();
      default:
        return 0;
    }
  }

  @Override
  public String toString() {
    return getType().toString();
  }

  private void onQmarkDestroy(int clientId) {

    Random random = new Random();
    int r = random.nextInt(4);
    if (r == 0) {
      logger.info("Spawning dynamite.");
      PacketSpawnItem packetSpawnItem =
          new PacketSpawnItem(
              ItemMaster.ItemTypes.DYNAMITE, new Vector3f(gridX, gridY, gridZ), clientId);
      packetSpawnItem.sendToLobby(ServerLogic.getLobbyForClient(clientId).getLobbyId());
    } else if (r == 1) {
      logger.info("Spawning heart.");
      PacketSpawnItem packetSpawnItem =
          new PacketSpawnItem(
              ItemMaster.ItemTypes.HEART, new Vector3f(gridX, gridY, gridZ), clientId);
      packetSpawnItem.sendToLobby(ServerLogic.getLobbyForClient(clientId).getLobbyId());
    } else if (r == 2) {
      logger.info("Spawning star.");
      PacketSpawnItem packetSpawnItem =
          new PacketSpawnItem(
              ItemMaster.ItemTypes.STAR, new Vector3f(gridX, gridY, gridZ), clientId);
      packetSpawnItem.sendToLobby(ServerLogic.getLobbyForClient(clientId).getLobbyId());
    } else if (r == 3) {
      logger.info("Spawning ice.");
      PacketSpawnItem packetSpawnItem =
          new PacketSpawnItem(
              ItemMaster.ItemTypes.ICE, new Vector3f(gridX, gridY, gridZ), clientId);
      packetSpawnItem.sendToLobby(ServerLogic.getLobbyForClient(clientId).getLobbyId());
    }
  }

  public void setGoldValue(int goldValue) {
    this.goldValue = goldValue;
  }
}
