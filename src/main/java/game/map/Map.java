package game.map;

import java.util.Random;

/**
 * Generic Abstract Class. Server and Client Map will extend this and use a different object for the
 * blocks variable. The client needs much more info about a block than the server, so we have two
 * completely different classes.
 *
 * @param <T> The class that is used to represent blocks.
 * @see entities.blocks.Block
 * @see ServerBlock
 */
public abstract class Map<T> {

  protected int width;
  protected int height;
  protected long seed;

  protected T[][] blocks;

  /* Threshold function:
   * Values below first number will be AIR
   * Values between the first and second number will be DIRT BLOCKS
   * Values above the second number will be STONE BLOCKS
   */
  protected final float[] thresholds = {0.3f, 0.75f};

  /**
   * Generate a new map.
   *
   * @param width number of blocks on the horizontal
   * @param height number of blocks on the vertical = depth
   * @param seed random seed
   */
  public Map(int width, int height, long seed) {
    this.width = width;
    this.height = height;
    this.seed = seed;
  }

  abstract void generateMap();

  abstract void damageBlock(int clientId, int posX, int posY, float damage);

  /**
   * Generates a noise map for map generation. TODO (Sanja): Implement map generation algorithm
   *
   * @param rng a random generator
   * @return the noise map for the specified random generator
   */
  protected float[][] generateNoiseMap(Random rng) {
    // Generate Noise here (now its purely random)
    float[][] noiseMap = new float[width][height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        noiseMap[x][y] = rng.nextFloat();
      }
    }
    return noiseMap;
  }

  @Override
  public String toString() {
    StringBuilder map = new StringBuilder();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        map.append(blocks[x][y].toString());
      }
      map.append("\n");
    }
    return map.toString();
  }
}
