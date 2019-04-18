package game.map;

import entities.blocks.AirBlock;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class ClientMap extends GameMap<Block> {

  private boolean local;
  private String[] lobbyMap;

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
    float[][] noiseMap = generateNoiseMap(seed);

    // Threshold function and random gold/item blocks can replace stone/dirt blocks
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        float posX = x * dim + 3;
        float posY = -y * dim - size;
        if (noiseMap[x][y] < thresholds[0]) {
          blocks[x][y] =
              BlockMaster.generateBlock(
                  BlockMaster.BlockTypes.AIR, new Vector3f(posX, posY, (float) size), x, y);
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
        if ((x == 0 || x == width - 1) || y == height - 1) {
          blocks[x][y] =
                  BlockMaster.generateBlock(
                          BlockMaster.BlockTypes.OBSIDIAN, new Vector3f(posX, posY, (float) size), x, y);
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
    blocks[blockX][blockY].increaseDamage(clientId, damage);
  }

  /**
   * REWORK IN PROGRESS!
   *
   * <p>Replace map with a new map. Only use from Packet {@link net.packets.map.PacketBroadcastMap}.
   * Map must be validated by packet. This will guarantee Integers only and correct lengths.
   */
  public void reloadMap() {
    // Kill old map
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        blocks[x][y].remove();
      }
    }

    // Create new map
    width = lobbyMap[0].length();
    height = lobbyMap.length;
    blocks = new Block[width][height];
    for (int y = 0; y < lobbyMap.length; y++) {
      char[] line = lobbyMap[y].toCharArray();
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
  }

  /**
   * Determine the light level in percent based on the passed depth in world coordinates.
   *
   * @param playerPosY depth of the player (or object) to calculate the light for
   * @return light level in percent [0, 1]
   */
  public float getLightLevel(float playerPosY) {
    float pctLevel = 1 - (-playerPosY / (height * dim));
    return (float) Math.max(0.15, (float) Math.pow(pctLevel, 4));
  }

  public boolean isLocal() {
    return local;
  }

  /**
   * Returns a list with all empty blocks in the specified range.
   *
   * <p>Used to calculate a free position to put the player after being crushed by a stone.
   *
   * @param maxGridDepth Only consider blocks above or on this level
   * @param ignoreCol ignore this column. Placing above the falling stone can cause issues and looks
   *     bad
   * @return A list with all potential empty blocks in the specified range
   */
  public CopyOnWriteArrayList<AirBlock> getAirBlocks(int maxGridDepth, int ignoreCol) {
    CopyOnWriteArrayList<AirBlock> airBlocks = new CopyOnWriteArrayList<>();
    for (int y = 0; y < Math.min(maxGridDepth + 1, height); y++) {
      for (int x = 0; x < width; x++) {
        if (x == ignoreCol) {
          continue;
        }
        if (blocks[x][y].getType() == BlockMaster.BlockTypes.AIR) {
          airBlocks.add((AirBlock) blocks[x][y]);
        }
      }
    }
    return airBlocks;
  }

  /**
   * Replace a block with an empty (Air) block. Essentialy deleting it for the map grid.
   *
   * @param gridPos the position (grid coordinates) to delete
   */
  public void replaceWithAirBlock(Vector2i gridPos) {
    blocks[gridPos.x][gridPos.y] = new AirBlock(gridPos.x, gridPos.y);
  }

  public void setLobbyMap(String[] lobbyMap) {
    this.lobbyMap = lobbyMap;
    this.local = false;
  }
}
