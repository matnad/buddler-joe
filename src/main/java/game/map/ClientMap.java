package game.map;

import entities.blocks.AirBlock;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import java.util.Random;
import org.joml.Vector3f;

public class ClientMap extends Map<Block> {

  /**
   * Generate a map for the client and generate the blocks in the world.
   *
   * @param width number of blocks on the horizontal
   * @param height number of blocks on the vertical = depth
   * @param seed random seed
   */
  public ClientMap(int width, int height, long seed) {
    super(width, height, seed);
    blocks = new Block[width][height];
    generateMap();
    checkFallingBlocks();
  }

  @Override
  void generateMap() {
    Random rng = new Random(seed);
    float[][] noiseMap = generateNoiseMap(rng);

    // Threshold function and random gold/item blocks can replace stone/dirt blocks
    int size = 3; // Block scale factor
    int dim = 2 * size; // Block dimension
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        float posX = x * dim + 3;
        float posY = -y * dim - size;
        if (noiseMap[x][y] < thresholds[0]) {
          blocks[x][y] = new AirBlock();
        } else {
          if ((int) (noiseMap[x][y] * 100) % 40 == 0) {
            // Gold Block: 1 in 40 chance
            blocks[x][y] =
                BlockMaster.generateBlock(
                    BlockMaster.BlockTypes.GOLD, new Vector3f(posX, posY, (float) size));
          } else if ((int) (noiseMap[x][y] * 100) % 50 == 0) {
            // Item Block: 1 in 50 chance
            blocks[x][y] =
                BlockMaster.generateBlock(
                    BlockMaster.BlockTypes.GRASS, new Vector3f(posX, posY, (float) size));
          } else if (noiseMap[x][y] < thresholds[1]) {
            blocks[x][y] =
                BlockMaster.generateBlock(
                    BlockMaster.BlockTypes.DIRT, new Vector3f(posX, posY, (float) size));
          } else {
            blocks[x][y] =
                BlockMaster.generateBlock(
                    BlockMaster.BlockTypes.STONE, new Vector3f(posX, posY, (float) size));
          }
        }
      }
    }
  }

  /**
   * This will proably be done by the server exclusively and done via packets on client side. So
   * this is temporary.
   */
  public void checkFallingBlocks() {
    boolean done;
    do {
      done = true;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          Block b = blocks[x][y];
          if (b == null) {
            continue;
          }
          if (b.getType() == BlockMaster.BlockTypes.STONE
              && y + 1 < height
              && (blocks[x][y + 1].getType() == BlockMaster.BlockTypes.AIR
                  || blocks[x][y + 1].isDestroyed())) {
            // Stone block can fall
            b.setMoveTo(new Vector3f(b.getPosition().x, -(y + 1) * 6 - 3, b.getPosition().z));
            blocks[x][y + 1] = b;
            blocks[x][y] = new AirBlock();
            done = false;
          }
        }
      }
    } while (!done);
  }
}
