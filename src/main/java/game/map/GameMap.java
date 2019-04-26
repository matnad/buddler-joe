package game.map;

import org.joml.SimplexNoise;
import org.joml.Vector2i;
import org.joml.Vector3f;

/**
 * Generic Abstract Class. Server and Client Map will extend this and use a different object for the
 * blocks variable. The client needs much more info about a block than the server, so we have two
 * completely different classes.
 *
 * @param <T> The class that is used to represent blocks.
 * @see entities.blocks.Block
 * @see ServerBlock
 */
public abstract class GameMap<T> {

  protected static final int dim = 6;
  protected static final int size = 3;
  /* Threshold function:
   * Values below first number will be STONE
   * Values between the first and second number will be DIRT BLOCKS
   * Values above the second number will be AIR
   */
  protected final float[] thresholds = {.28f, .8f};
  protected int width;
  protected int height;
  protected long seed;
  protected T[][] blocks;

  protected float[][] noiseMap;

  protected static final int terrainChunk = 8;

  /**
   * Generate a new map.
   *
   * @param width number of blocks on the horizontal
   * @param height number of blocks on the vertical = depth
   * @param seed random seed
   */
  public GameMap(int width, int height, long seed) {
    this.width = width;
    this.height = height;
    this.seed = (long) (seed % 1e6);
  }

  public static int getSize() {
    return size;
  }

  public static int getDim() {
    return dim;
  }

  public static int getTerrainChunk() {
    return terrainChunk;
  }

  abstract void generateMap();

  abstract void damageBlock(int clientId, int posX, int posY, float damage);

  /**
   * Generates a noise map for map generation.
   *
   * @return the noise map for the specified random generator
   */
  protected float[][] generateNoiseMap() {
    // Generate Noise here
    int radius = 4; // "Smoothing" of noise
    noiseMap = new float[width][height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        float dx = (x + seed - radius) / (float) radius;
        float dy = (y + seed - radius) / (float) radius;
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

  public Vector3f gridToWorld(Vector2i gridCoords) {
    return new Vector3f(gridCoords.x * dim + size, -gridCoords.y * dim - size, size);
  }

  public Vector2i worldToGrid(Vector3f worldCoords) {
    return new Vector2i(
        (int) Math.floor(worldCoords.x / dim), (int) Math.floor(-worldCoords.y / dim));
  }

  public long getSeed() {
    return seed;
  }

  public void setSeed(long seed) {
    this.seed = seed;
  }
}
