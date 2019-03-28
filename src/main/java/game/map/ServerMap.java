package game.map;

import entities.blocks.BlockMaster;
import java.util.Random;

public class ServerMap extends Map<ServerBlock> {

  /**
   * Generate a new map for the Server.
   *
   * @param width number of blocks on the horizontal
   * @param height number of blocks on the vertical = depth
   * @param seed random seed
   */
  public ServerMap(int width, int height, long seed) {
    super(width, height, seed);
    blocks = new ServerBlock[width][height];
    generateMap();
    checkFallingBlocks();
  }

  @Override
  void generateMap() {
    Random rng = new Random(seed);
    float[][] noiseMap = generateNoiseMap(rng);

    /* Threshold function
     */
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (noiseMap[x][y] < thresholds[0]) {
          blocks[x][y] = new ServerBlock(BlockMaster.BlockTypes.AIR); // Air
        } else {
          if ((int) (noiseMap[x][y] * 100) % 40 == 0) {
            blocks[x][y] = new ServerBlock(BlockMaster.BlockTypes.GOLD); // Gold: 1 in 40 chance
          } else if ((int) (noiseMap[x][y] * 100) % 50 == 0) {
            blocks[x][y] =
                new ServerBlock(BlockMaster.BlockTypes.GRASS); // Item Block: 1 in 50 chance
          } else if (noiseMap[x][y] < thresholds[1]) {
            blocks[x][y] = new ServerBlock(BlockMaster.BlockTypes.DIRT); // Dirt
          } else {
            blocks[x][y] = new ServerBlock(BlockMaster.BlockTypes.STONE); // Stone
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
          if (blocks[x][y] == null) {
            continue;
          }
          if (blocks[x][y].getType() == BlockMaster.BlockTypes.STONE
              && y + 1 < height
              && blocks[x][y + 1] == null) {
            blocks[x][y + 1] = blocks[x][y];
            blocks[x][y] = null;
            done = false;
          }
        }
      }
    } while (!done);
  }

  /**
   * Create a random test map. Use this to develop a good mapping algorithm.
   *
   * @param args nothing
   */
  public static void main(String[] args) {
    ServerMap testMap = new ServerMap(30, 30, System.currentTimeMillis());
    System.out.println(testMap);
  }
}
