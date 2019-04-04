package game.map;

import java.util.Random;
import org.joml.SimplexNoise;

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

  protected static final int dim = 6;
  protected static final int size = 3;

  protected T[][] blocks;

  /* Threshold function:
   * Values below first number will be STONE
   * Values between the first and second number will be DIRT BLOCKS
   * Values above the second number will be AIR
   */
  protected final float[] thresholds = {.28f, .8f};

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
    // Generate Noise here
    int radius = 4; // "Smoothing" of noise
    float[][] noiseMap = new float[width][height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        float dx = (x - radius) / (float) radius;
        float dy = (y - radius) / (float) radius;
        noiseMap[x][y] = (SimplexNoise.noise(dx, dy) + 1) / 2;
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

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public static int getSize() {
    return size;
  }

  public static int getDim() {
    return dim;
  }
}
