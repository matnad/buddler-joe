package game.map;

import static org.lwjgl.BufferUtils.createByteBuffer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import javax.imageio.ImageIO;
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
    this.seed = seed;
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
   * Generates a noise map for map generation. TODO (Sanja): Implement map generation algorithm
   *
   * @param seed the seed
   * @return the noise map for the specified random generator
   */
  protected float[][] generateNoiseMap(long seed) {
    // Generate Noise here
    seed %= 1e6;
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

  public BufferedImage getMapImage(int startRow, int startCol) {
    int[] pixels = new int[terrainChunk * terrainChunk];

    if (startRow + terrainChunk > height || startCol + terrainChunk > width) {
      throw new IllegalArgumentException(
          "startRow " + startRow + " or startCol " + startCol + " are out of Map Bounds.");
    }

    for (int i = startRow; i < terrainChunk; i++) {
      for (int j = startCol; j < terrainChunk; j++) {
        if (noiseMap[i][j] < thresholds[0]) {
          pixels[i * terrainChunk + j] = Color.RED.getRGB();
        } else if (noiseMap[i][j] < thresholds[1]) {
          pixels[i * terrainChunk + j] = Color.BLUE.getRGB();
        } else {
          pixels[i * terrainChunk + j] = Color.GREEN.getRGB();
        }
      }
    }
    BufferedImage pixelImage = new BufferedImage(terrainChunk, terrainChunk, BufferedImage.TYPE_INT_RGB);
    pixelImage.setRGB(0, 0, terrainChunk, terrainChunk, pixels, 0, terrainChunk);
    return pixelImage;
  }

  public ByteBuffer getMapImageByteBuffer(int startRow, int startCol) {
    BufferedImage img = getMapImage(startRow, startCol);

    ByteBuffer buffer = null;

    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      ImageIO.write(img, "png", os);
      InputStream source = new ByteArrayInputStream(os.toByteArray());
      ReadableByteChannel rbc = Channels.newChannel(Objects.requireNonNull(source));
      buffer = createByteBuffer(8 * 1024);
      while (true) {
        int bytes = rbc.read(buffer);
        if (bytes == -1) {
          break;
        }
        if (buffer.remaining() == 0) {
          buffer = util.IoUtil.resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (buffer == null) {
      return null;
    }

    buffer.flip();
    return buffer.slice();
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
}
