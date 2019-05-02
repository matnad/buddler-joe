package game.map;

import static org.lwjgl.BufferUtils.createByteBuffer;

import engine.models.TexturedModel;
import engine.render.Loader;
import engine.textures.TerrainTexture;
import engine.textures.TerrainTexturePack;
import entities.Entity;
import entities.Player;
import entities.blocks.AirBlock;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import entities.blocks.DirtBlock;
import entities.blocks.StoneBlock;
import game.Game;
import game.NetPlayerMaster;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrains.TerrainFlat;

public class ClientMap extends GameMap<Block> {

  private static final Logger logger = LoggerFactory.getLogger(ClientMap.class);

  private boolean local;
  private String[] lobbyMap;

  private int terrainRows;
  private int terrainCols;

  /**
   * Generate a map for the client and generate the blocks in the world.
   *
   * @param mapSize size of map
   * @param seed random seed
   */
  public ClientMap(String mapSize, long seed) {
    super(mapSize, seed);
    // Create empty map, the real map will be loaded from the server
    width = 0;
    height = 0;
    local = true;
    blocks = new Block[width][height];
    generateMap();
    checkFallingBlocks(true);
  }

  @Override
  void generateMap() {
    float[][] noiseMap = generateNoiseMap();

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
          blocks[x][y].remove();
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

    //logger.debug("Reloading map: " + Arrays.toString(lobbyMap));

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

    // Create procedurally generated hills
    Random rnd = new Random(seed); // seed is broadcast by the server
    int depth = 6; // how many rows of hills
    int[][] stack = new int[width][depth]; // we store the full stack to do some simple random walks
    for (int i = 0; i < width; i++) {
      float posX = i * dim + 3;
      stack[i][0] = 1;
      for (int j = 0; j < depth; j++) {
        if (j > 0) {
          // Simple random walk algorithm that is inclined to follow the slope of the last 2
          // blocks. Will more likely generate something resembling hills.
          stack[i][j] = stack[i][j - 1];
          if (i >= 2) {
            int slope = stack[i - 1][j] - stack[i - 2][j];
            if (slope > 0 && rnd.nextFloat() < .6f) {
              stack[i][j]++;
            } else if (slope == 0 && rnd.nextFloat() < .35f) {
              stack[i][j]++;
            } else if (slope < 0 && rnd.nextFloat() < .2f) {
              stack[i][j]++;
            }
          } else if (rnd.nextFloat() < .4f) {
            stack[i][j]++;
          }

          if (j == depth - 1 && stack[i][j] == 1) {
            stack[i][j]++;
          }
        }

        float posY = -size;
        for (int k = 0; k < stack[i][j]; k++) {
          TexturedModel model = DirtBlock.getBlockModel();
          // First row needs to blend in with the map
          if (j == 0 && blocks[i][0].getType() == BlockMaster.BlockTypes.STONE) {
            model = StoneBlock.getBlockModel();
          } else if (j > 0) {
            // The lower the blocks, the more likely they are stone instead of dirt
            if ((1 - ((float) k / depth)) - .3f > rnd.nextFloat()) {
              model = StoneBlock.getBlockModel();
            }
          }
          // Add the block as entity. This is not managed by the blockmaster since it is just
          // decoration
          Game.addEntity(
              new Entity(model, new Vector3f(posX, posY, -size - 0.02f - j * dim), 0, 0, 0, 3));
          posY += dim;
        }
      }
    }
  }

  /**
   * Create a buffered RGB image of a game map chunk.
   *
   * <p>Every block is 1 pixel. Stone is red, gold is green, obsidian is blue and dirt is black.
   *
   * @param startCol column rank of the map chunk (0 for the first chunk, 1 for the second, etc)
   * @param startRow row rank of the map chunk (0 for the first chunk, 1 for the second, etc)
   * @return a BufferedImage representation of the map
   */
  private BufferedImage getMapImage(int startRow, int startCol) {
    int[] pixels = new int[terrainChunk * terrainChunk];

    if (startRow + terrainChunk > height || startCol + terrainChunk > width) {
      throw new IllegalArgumentException(
          "startRow " + startRow + " or startCol " + startCol + " are out of Map Bounds.");
    }

    for (int i = 0; i < terrainChunk; i++) {
      for (int j = 0; j < terrainChunk; j++) {
        switch (blocks[startCol * terrainChunk + i][startRow * terrainChunk + j].getType()) {
          case STONE:
            pixels[j * terrainChunk + i] = Color.RED.getRGB();
            break;
          case GOLD:
            pixels[j * terrainChunk + i] = Color.GREEN.getRGB();
            break;
          case OBSIDIAN:
            pixels[j * terrainChunk + i] = Color.BLUE.getRGB();
            break;
          default:
            break;
        }
      }
    }
    BufferedImage pixelImage =
        new BufferedImage(terrainChunk, terrainChunk, BufferedImage.TYPE_INT_RGB);
    pixelImage.setRGB(0, 0, terrainChunk, terrainChunk, pixels, 0, terrainChunk);
    return pixelImage;
  }

  /**
   * Return a byte buffer of a specified game map chunk, representing 3 values for each block of the
   * map. Those values correspond to the block types.
   *
   * @param startCol column rank of the map chunk (0 for the first chunk, 1 for the second, etc)
   * @param startRow row rank of the map chunk (0 for the first chunk, 1 for the second, etc)
   * @return ByteBuffer than can be directly loaded into openGL
   */
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

  /**
   * Generate a 2D array with all map terrain chunks for this map.
   *
   * <p>Textures are specified here.
   *
   * @param loader main Loader
   * @return a 2D array containing the full terrain for the map
   */
  public TerrainFlat[][] generateTerrains(Loader loader) {

    // Verify Map Dimensions
    if (width % terrainChunk != 0 || height % terrainChunk != 0) {
      throw new IllegalStateException(
          "Fatal Error: Map dimensions must be multiples of "
              + terrainChunk
              + ". Restart the Game and create a new Lobby.");
    }

    // Prepare Textures
    TerrainTexture dirt = new TerrainTexture(loader.loadTexture("Erde512x512"));
    TerrainTexture stone = new TerrainTexture(loader.loadTexture("Stein512x512"));
    TerrainTexture gold = new TerrainTexture(loader.loadTexture("GoldBraun512x512"));
    TerrainTexture obsidian = new TerrainTexture(loader.loadTexture("red"));

    TerrainTexturePack texturePack = new TerrainTexturePack(dirt, stone, gold, obsidian);

    // Generate Terrains
    terrainRows = height / terrainChunk;
    terrainCols = width / terrainChunk;
    TerrainFlat[][] terrains = new TerrainFlat[terrainCols][terrainRows];
    for (int i = 0; i < terrainCols; i++) {
      for (int j = 0; j < terrainRows; j++) {
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(this, j, i));
        terrains[i][j] = new TerrainFlat(i, -j, loader, texturePack, blendMap);
      }
    }
    return terrains;
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

  /**
   * Calculate the block where the player spawns on this map so that all players are spaced evenly.
   * The order is according to clientId (ascending).
   *
   * @param player player to get spawn block for
   * @return The x coordinate of the block in grid coords
   */
  private int getSpawnBlockForPlayer(Player player) {
    List<Integer> ids = new ArrayList<>(NetPlayerMaster.getIds());
    Collections.sort(ids);

    int pos = ids.size();
    int players = ids.size() + 1;

    for (int i = 0; i < ids.size(); i++) {
      if (player.getClientId() < ids.get(i)) {
        pos = i;
        break;
      }
    }

    if (pos >= players || width < players) {
      // Invalid spawn data, spawn on block 1/1
      return width / 2;
    }

    return width / (players + 1) * (pos + 1);
  }

  /**
   * Get world coordinates for the player to spawn on the map given the static NetPlayerMaster.
   *
   * @param player player to get spawn location for
   * @return world coordinates for spawn position
   */
  public Vector3f getSpawnPositionForPlayer(Player player) {
    int block = getSpawnBlockForPlayer(player);
    return new Vector3f(dim * block + size, 1, 3);
  }

  public int getTerrainRows() {
    return terrainRows;
  }

  public int getTerrainCols() {
    return terrainCols;
  }
}
