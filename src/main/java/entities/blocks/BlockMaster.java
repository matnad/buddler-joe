package entities.blocks;

import engine.render.Loader;
import game.Game;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.joml.Vector3f;

/** Create and manage blocks. Only ever create blocks using this class */
public class BlockMaster {
  // Organize Blocks in lists that can be accessed by their type
  private static final Map<BlockTypes, CopyOnWriteArrayList<Block>> blockLists =
      new ConcurrentHashMap<>();
  // Keep a list with just blocks
  private static final List<Block> blocks = new CopyOnWriteArrayList<>();
  // List of debris (small blocks)

  /**
   * Init is called once while loading the game. Pre-loads the block texture atlas
   *
   * @param loader main loader
   */
  public static void init(Loader loader) {
    Block.loadBlockModels(loader);
    DirtBlock.init(loader);
    GoldBlock.init(loader);
    StoneBlock.init(loader);
    GrassBlock.init(loader);
    // QmarkBlock.init(loader);
  }

  /**
   * ONLY USE THIS METHOD TO GENERATE BLOCKS.
   *
   * <p>Generates a block of the chosen type and adds it to all relevant lists. Keeps track of the
   * block and cleans it up when destroyed.
   *
   * @param type type of the block as described in {@link BlockTypes}
   * @param position 3D coordinate to place the block
   * @param gridX X coordinate for the block (map grid)
   * @param gridY Y coordinate for the block (map grid)
   * @return the created block
   */
  public static Block generateBlock(BlockTypes type, Vector3f position, int gridX, int gridY) {
    Block block;
    switch (type) {
      case GRASS:
        block = new GrassBlock(position, gridX, gridY);
        break;
      case DIRT:
        block = new DirtBlock(position, gridX, gridY);
        break;
      case GOLD:
        block = new GoldBlock(position, gridX, gridY);
        break;
      case STONE:
        block = new StoneBlock(position, gridX, gridY);
        break;
      case AIR:
        block = new AirBlock(gridX, gridY);
        break;
        // case QMARK:
        // block = new QmarkBlock(position, gridX, gridY);
        // break;
      default:
        block = null;
        break;
    }

    if (block != null) {
      addBlockToList(block);
    }

    return block;
  }

  /**
   * Called every frame to update if a block has been destroyed. If so, remove that block from all
   * relevant lists (and clean out empty lists).
   */
  public static void update() {
    // Remove destroyed blocks from the list and update entities
    Iterator<Map.Entry<BlockTypes, CopyOnWriteArrayList<Block>>> mapIterator =
        blockLists.entrySet().iterator();
    while (mapIterator.hasNext()) {
      List<Block> list = mapIterator.next().getValue();
      Iterator<Block> iterator = list.iterator();
      while (iterator.hasNext()) {
        Block block = iterator.next();
        if (block.isDestroyed()) {
          // Remove block from list and entities
          Game.removeEntity(block);
          list.remove(iterator);
          blocks.remove(block);
          // Clean up list if empty
          if (list.isEmpty()) {
            mapIterator.remove();
          }
        } else {
          // Check is the block is queued to move
          if (block.getPosition() != block.getMoveTo()) {
            if (!block.canMove()) {
              // Block is waiting to move. Update the delay
              block.decreaseMoveDelay((float) Game.window.getFrameTimeSeconds());
              // "Shake"/jiggle the block while it is waiting to move
              block.shake();
            } else {
              // Reset Orientation
              block.setRotX(0);
              // Slowly move the block
              block.accelerate((float) Game.window.getFrameTimeSeconds());
              if (block.getPosition().distance(block.getMoveStartPos()) > block.getMoveDistance()) {
                block.setPosition(block.getMoveTo());
              } else {
                Vector3f dir = new Vector3f(block.getMoveTo()).sub(block.getMoveStartPos());
                block.increasePosition(dir.normalize().mul(block.getSpeed()));
              }
            }
          }
        }
      }
    }
  }

  /**
   * Don't call this method directly. It is used by the Block Master to add new blocks to the game.
   *
   * <p>Adds them to the Blockmasters personal lists and to the Entity render's list.
   *
   * @param block freshly generated block
   */
  private static void addBlockToList(Block block) {
    // Get the list with the type of the block, if the list is absent, create it
    List<Block> list =
        blockLists.computeIfAbsent(block.getType(), k -> new CopyOnWriteArrayList<>());

    // If the block is not destroyed, add it to the Game to be rendered
    if (!block.isDestroyed()) {
      // Add block to its type-specific list
      list.add(block);
      // Add to type-unspecific list
      blocks.add(block);
      // Add to render list
      Game.addEntity(block);
    }
  }

  public static List<Block> getBlocks() {
    return blocks;
  }

  // public static Map<BlockTypes, List<Block>> getBlockLists() {
  //  return blockLists;
  // }

  /**
   * Easy access to block types by their name.
   *
   * <p>Includes representation with color and symbol for the console.
   */
  public enum BlockTypes {
    GRASS(4, 4, "\u001B[34m█\u001B[0m"),
    DIRT(31, 1, "\u001B[31;1m█\u001B[0m"),
    GOLD(30, 3, "\u001B[33m█\u001B[0m"),
    STONE(11, 2, "\u001B[37m█\u001B[0m"),
    AIR(0, 0, "\u001B[35;1m█\u001B[0m");
    // QMARK(5,5,"\u001B[34m█\u001B[0m");

    private final int textureId;
    private final String repr;
    private final int id;

    BlockTypes(int textureId, int id, String repr) {
      this.textureId = textureId;
      this.repr = repr;
      this.id = id;
    }

    public int getTextureId() {
      return textureId;
    }

    public int getId() {
      return id;
    }

    /**
     * Returns the Block Type associated with the given ID or AIR if the ID is not found.
     *
     * @param id id of block type
     * @return the block type associated with that id
     */
    public static BlockTypes getBlockTypeById(int id) {
      for (BlockTypes blockType : BlockTypes.values()) {
        if (blockType.id == id) {
          return blockType;
        }
      }
      return AIR;
    }

    @Override
    public String toString() {
      return repr;
    }
  }
}
