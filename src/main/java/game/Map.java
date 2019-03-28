package game;

import java.util.Random;

public class Map {

  private int width;
  private int height;
  private long seed;

  private int[][] blocks;
  private float[][] hardness;

  private static final String[] blockRepresentation = {"▔", "█", "█", "█", "█"};
  private static final String[] blockAnsiCodes = {
    "\u001B[35;1m", "\u001B[31;1m", "\u001B[37m", "\u001B[33m", "\u001B[34m"
  };

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

    blocks = new int[width][height];
    hardness = new float[width][height];

    generateMap();
  }

  private void generateMap() {
    Random rng = new Random(seed);

    // Generate Noise here (now its purely random)
    float[][] noiseMap = new float[width][height];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        noiseMap[x][y] = rng.nextFloat();
      }
    }

    // Threshold function
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (noiseMap[x][y] < .3f) {
          blocks[x][y] = 0; // Air
        } else if (noiseMap[x][y] < .75f) {
          blocks[x][y] = 1; // Dirt
        } else {
          blocks[x][y] = 2; // Stone
        }
      }
    }

    // Gold and ? Boxes
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        // System.out.println((int) (noiseMap[x][y] * 100));
        if ((int) (noiseMap[x][y] * 100) % 40 == 0) {
          blocks[x][y] = 3; // Gold: 1 in 40 chance
        } else if ((int) (noiseMap[x][y] * 100) % 50 == 0) {
          blocks[x][y] = 4; // Item Block: 1 in 50 chance
        }
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder map = new StringBuilder();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        map.append(blockAnsiCodes[blocks[x][y]]);
        map.append(blockRepresentation[blocks[x][y]]);
      }
      map.append("\n");
    }
    map.append("\u001B[0m");
    return map.toString();
  }

  public static void main(String[] args) {
    Map testMap = new Map(30, 30, System.currentTimeMillis());
    System.out.println(testMap);
  }
}
