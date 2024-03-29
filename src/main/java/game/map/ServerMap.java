package game.map;

import entities.blocks.BlockMaster;
import java.util.ArrayList;
import java.util.Random;
import net.ServerLogic;
import net.packets.block.PacketBlockDamage;

public class ServerMap extends GameMap<ServerBlock> {

  /**
   * Generate a new map for the Server.
   *
   * @param mapSize size of the map
   * @param seed random seed
   */
  public ServerMap(String mapSize, long seed) {
    super(mapSize, seed);
    blocks = new ServerBlock[width][height];
    generateMap();
    checkFallingBlocks();
  }

  /*/**
   * Create a random test map. Use this to develop a good mapping algorithm.
   *
   * @param args nothing
   */
  // public static void main(String[] args) {
  //  ServerMap testMap = new ServerMap(64, 100, System.currentTimeMillis());
  //  System.out.println(testMap);
  // }

  @Override
  void generateMap() {
    Random rng = new Random(seed);
    float[][] noiseMap = generateNoiseMap();

    /* Threshold function
     */
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if ((x == 0 || x == width - 1) || y == height - 1) {
          blocks[x][y] = new ServerBlock(BlockMaster.BlockTypes.OBSIDIAN, x, y);
        } else if (y == height - 2) {
          blocks[x][y] = new ServerBlock(BlockMaster.BlockTypes.GOLD, x, y);
          blocks[x][y].setGoldValue(135 + y * 5);
        } else if (noiseMap[x][y] < thresholds[0]) {
          blocks[x][y] = new ServerBlock(BlockMaster.BlockTypes.STONE, x, y); // Air
        } else {
          if (rng.nextFloat() < .07f) {
            blocks[x][y] =
                new ServerBlock(BlockMaster.BlockTypes.GOLD, x, y); // Gold: 1 in 40 chance
            blocks[x][y].setGoldValue(135 + y * 5);
          } else if (rng.nextFloat() < .05f) {
            blocks[x][y] =
                new ServerBlock(BlockMaster.BlockTypes.QMARK, x, y); // Item Block: 1 in 50 chance
          } else if (noiseMap[x][y] < thresholds[1]) {
            blocks[x][y] = new ServerBlock(BlockMaster.BlockTypes.DIRT, x, y); // Dirt
          } else {
            blocks[x][y] = new ServerBlock(BlockMaster.BlockTypes.AIR, x, y); // Stone
          }
        }
      }
    }
  }

  /**
   * Check and move falling blocks. This will send packets in the future. For now client side
   * handles the blocks themselves.
   */
  public void checkFallingBlocks() {
    boolean done;
    do {
      done = true;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (blocks[x][y].getType() == BlockMaster.BlockTypes.STONE
              && y + 1 < height
              && blocks[x][y + 1].getType() == BlockMaster.BlockTypes.AIR) {
            blocks[x][y + 1] = blocks[x][y];
            blocks[x][y] = new ServerBlock(BlockMaster.BlockTypes.AIR, x, y);
            done = false;
          }
        }
      }
    } while (!done);
  }

  /**
   * Deal damage to a block. Gets called from a client packet.
   *
   * @see net.packets.block.PacketBlockDamage
   * @param posX X position of the block
   * @param posY Y position of the block
   * @param damage damage to deal to the block
   */
  @Override
  public void damageBlock(int clientId, int posX, int posY, float damage) {

    // Validate if the block damage packet is not violating any rules
    if (!ServerLogic.getPlayerList().getPlayer(clientId).validateBlockDamage(posX, posY, damage)) {
      return;
    }

    if (blocks[posX][posY] != null) {
      blocks[posX][posY].damageBlock(clientId, damage);
      checkFallingBlocks();
      int lobId = ServerLogic.getLobbyForClient(clientId).getLobbyId();
      if (lobId > 0) {
        new PacketBlockDamage(clientId, posX, posY, damage).sendToLobby(lobId);
      }
    }
  }

  /**
   * Creates a String that describes every block of the map. This string can be sent over the
   * network protocol with {@link net.packets.map.PacketBroadcastMap}
   *
   * @return transferable string representation of the map
   */
  public String toPacketString() {
    StringBuilder sb = new StringBuilder();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        sb.append(blocks[x][y].getType().getId());
      }
      if (y < height - 1) {
        sb.append("║");
      }
    }
    return sb.toString();
  }

  /**
   * Generate a damage packet for each damaged block to update a newly generated map.
   *
   * @return an array list of damage packets to update the map
   */
  public ArrayList<PacketBlockDamage> getDamagePackets() {
    ArrayList<PacketBlockDamage> packets = new ArrayList<>();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        float damage = blocks[x][y].getBaseHardness() - blocks[x][y].getHardness();
        if (damage > 0) {
          packets.add(new PacketBlockDamage(0, x, y, damage));
        }
      }
    }
    return packets;
  }
}
