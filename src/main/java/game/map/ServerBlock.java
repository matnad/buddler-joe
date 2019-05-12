package game.map;

import entities.blocks.BlockMaster;
import entities.blocks.DirtBlock;
import entities.blocks.GoldBlock;
import entities.blocks.GrassBlock;
import entities.blocks.Obsidian;
import entities.blocks.QmarkBlock;
import entities.blocks.StoneBlock;
import entities.items.ItemMaster;
import java.util.Random;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.items.PacketSpawnItem;
import net.playerhandling.ServerPlayer;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerBlock {

  private static final Logger logger = LoggerFactory.getLogger(PacketSpawnItem.class);
  private final int gridZ = GameMap.getSize();
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
    if (hardness >= 100f) {
      return;
    }

    if (type == BlockMaster.BlockTypes.AIR) {
      return;
    }

    hardness -= damage;
    if (hardness < 0) {
      if (this.type == BlockMaster.BlockTypes.QMARK) {
        onQmarkDestroy(clientThatDealsDamage);
      }
      this.type = BlockMaster.BlockTypes.AIR;
      // Add gold value if the block is worth anything
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
      case OBSIDIAN:
        return Obsidian.getHardness();
      default:
        return 0;
    }
  }

  @Override
  public String toString() {
    return getType().toString();
  }

  private void onQmarkDestroy(int clientId) {
    // Get Player and Lobby
    ServerPlayer itemOwner = ServerLogic.getPlayerList().getPlayer(clientId);
    if (itemOwner == null) {
      return;
    }
    Lobby lobby = itemOwner.getLobby();
    if (lobby == null) {
      return;
    }

    // Spawn Item
    Random random = new Random();
    int r = random.nextInt(100);
    // Less stars if there are more players
    float heartOrStar;
    switch (lobby.getPlayerAmount()) {
      case 1:
      case 2:
        heartOrStar = 70;
        break;
      case 3:
      case 4:
        heartOrStar = 73;
        break;
      default:
        heartOrStar = 76;
    }
    // if (random.nextInt(2) == 0) {
    //  r = 30;
    // } else {
    //  r = 90;
    // }
    if (0 <= r && r <= 25) {
      logger.debug("Spawning dynamite.");
      PacketSpawnItem packetSpawnItem =
          new PacketSpawnItem(
              ItemMaster.ItemTypes.DYNAMITE, new Vector3f(gridX, gridY, gridZ), clientId);
      packetSpawnItem.sendToLobby(lobby.getLobbyId());
    } else if (25 < r && r <= 45) {
      logger.debug("Spawning Steroids.");
      PacketSpawnItem packetSpawnItem =
          new PacketSpawnItem(
              ItemMaster.ItemTypes.STEROIDS, new Vector3f(gridX, gridY, gridZ), clientId);
      packetSpawnItem.sendToLobby(lobby.getLobbyId());
      itemOwner.ampUp();
    } else if (45 < r && r <= heartOrStar) {
      logger.debug("Spawning heart.");
      PacketSpawnItem packetSpawnItem =
          new PacketSpawnItem(
              ItemMaster.ItemTypes.HEART, new Vector3f(gridX, gridY, gridZ), clientId);
      packetSpawnItem.sendToLobby(lobby.getLobbyId());
    } else if (heartOrStar < r && r <= 85) {
      logger.debug("Spawning star.");
      for (ServerPlayer lobbyPlayer : lobby.getLobbyPlayers()) {
        if (lobbyPlayer != itemOwner) {
          lobbyPlayer.freeze();
        }
      }
      PacketSpawnItem packetSpawnItem =
          new PacketSpawnItem(
              ItemMaster.ItemTypes.STAR, new Vector3f(gridX, gridY, gridZ), clientId);
      packetSpawnItem.sendToLobby(lobby.getLobbyId());
    } else if (85 < r && r <= 100) {
      logger.debug("Spawning ice.");
      itemOwner.freeze();
      PacketSpawnItem packetSpawnItem =
          new PacketSpawnItem(
              ItemMaster.ItemTypes.ICE, new Vector3f(gridX, gridY, gridZ), clientId);
      packetSpawnItem.sendToLobby(lobby.getLobbyId());
    }
  }

  public void setGoldValue(int goldValue) {
    this.goldValue = goldValue;
  }
}
