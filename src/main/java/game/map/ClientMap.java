package game.map;

import entities.blocks.AirBlock;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import java.util.Random;
import org.joml.Vector3f;

public class ClientMap extends Map<Block> {

  private boolean local;

  /**
   * Generate a map for the client and generate the blocks in the world.
   *
   * @param width number of blocks on the horizontal
   * @param height number of blocks on the vertical = depth
   * @param seed random seed
   */
  public ClientMap(int width, int height, long seed) {
    super(width, height, seed);
    local = true;
    blocks = new Block[width][height];
    generateMap();
    checkFallingBlocks(true);
  }

  @Override
  void generateMap() {
    Random rng = new Random(seed);
    float[][] noiseMap = generateNoiseMap(rng);

    // Threshold function and random gold/item blocks can replace stone/dirt blocks
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        float posX = x * dim + 3;
        float posY = -y * dim - size;
        if (noiseMap[x][y] < thresholds[0]) {
          blocks[x][y] = new AirBlock(x, y);
        } else {
          if ((int) (noiseMap[x][y] * 100) % 40 == 0) {
            // Gold Block: 1 in 40 chance
            blocks[x][y] =
                BlockMaster.generateBlock(
                    BlockMaster.BlockTypes.GOLD, new Vector3f(posX, posY, (float) size), x, y);
          } else if ((int) (noiseMap[x][y] * 100) % 50 == 0) {
            // Item Block: 1 in 50 chance
            blocks[x][y] =
                BlockMaster.generateBlock(
                    BlockMaster.BlockTypes.GRASS, new Vector3f(posX, posY, (float) size), x, y);
          } else if (noiseMap[x][y] < thresholds[1]) {
            blocks[x][y] =
                BlockMaster.generateBlock(
                    BlockMaster.BlockTypes.DIRT, new Vector3f(posX, posY, (float) size), x, y);
          } else {
            blocks[x][y] =
                BlockMaster.generateBlock(
                    BlockMaster.BlockTypes.STONE, new Vector3f(posX, posY, (float) size), x, y);
          }
        }
      }
    }
  }

  public void checkFallingBlocks() {
    checkFallingBlocks(false);
  }

  /**
   * This will proably be done by the server exclusively and done via packets on client side. So
   * this is temporary.
   *
   * <p>Add a random delay to falling blocks but make sure blocks below other blocks will always
   * fall first.
   */
  private void checkFallingBlocks(boolean instantUpdate) {
    boolean done;
    do {
      done = true;
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          Block b = blocks[x][y];
          if (b == null) {
            continue;
          }
          if (b.getType() == BlockMaster.BlockTypes.STONE
              && y + 1 < height
              && (blocks[x][y + 1].getType() == BlockMaster.BlockTypes.AIR
                  || blocks[x][y + 1].isDestroyed())) {
            // Stone block can fall, set a minimum delay of .5 seconds but randomize the actual
            Vector3f newPos = new Vector3f(b.getPosition().x, -(y + 1) * 6 - 3, b.getPosition().z);
            if (!instantUpdate) {
              // delay
              long seed = (long) (b.getPosition().x + b.getPosition().y);
              float moveDelay = Math.max(.5f, (float) ((new Random(seed).nextGaussian() + 1) * 2));
              // Never fall sooner than a block below, otherwise blocks could clip eachother
              if (y + 2 < height && blocks[x][y + 2].getType() == BlockMaster.BlockTypes.STONE) {
                moveDelay = Math.max(moveDelay, blocks[x][y + 2].getMoveDelay());
              }
              // Queue the movement for the block. Move updating is done in the BlockMaster update.
              b.setMoveTo(newPos, moveDelay);
            } else {
              b.setMoveTo(newPos, 0);
              b.setPosition(newPos);
            }
            // Update the grid
            blocks[x][y + 1] = b;
            b.setGridY(b.getGridY() + 1);
            blocks[x][y] = new AirBlock(x, y);
            done = false;
          }
        }
      }
    } while (!done);
  }

  @Override
  public void damageBlock(int clientId, int blockX, int blockY, float damage) {
    blocks[blockX][blockY].increaseDamage(damage);
  }

  /**
   * Replace map with a new map. Only use from Packet {@link net.packets.map.PacketBroadcastMap}.
   * Map must be validated by packet. This will guarantee Integers only and correct lengths.
   *
   * @param mapArray mapArray from {@link net.packets.map.PacketBroadcastMap}.
   */
  public void reloadMap(String[] mapArray) {
    // Kill old map
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        blocks[x][y].remove();
      }
    }

    // Create new map
    width = mapArray[0].length();
    height = mapArray.length;
    blocks = new Block[width][height];
    for (int y = 0; y < mapArray.length; y++) {
      char[] line = mapArray[y].toCharArray();
      for (int x = 0; x < line.length; x++) {
        int type = Character.getNumericValue(line[x]);
        float posX = x * dim + 3;
        float posY = -y * dim - size;
        blocks[x][y] =
            BlockMaster.generateBlock(
                BlockMaster.BlockTypes.getBlockTypeById(type),
                new Vector3f(posX, posY, (float) size),
                x,
                y);
      }
    }
    local = false;
  }

  public float getLightLevel(float playerPosY) {
    float pctLevel =  1 - (-playerPosY / (height * dim));
    return (float) Math.max(0.1, (float) Math.pow(pctLevel, 4));
  }

  public boolean isLocal() {
    return local;
  }
}
