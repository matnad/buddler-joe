package entities.items;

import engine.render.Loader;
import game.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;

/** Create and manage items. Only ever create items using this class */
@SuppressWarnings("Duplicates")
public class ItemMaster {

  // Organize Items in lists that can be accessed by their type
  private static final Map<ItemTypes, List<Item>> itemLists = new HashMap<>();
  // Keep a list with all items
  private static final List<Item> items = new ArrayList<>();

  /**
   * Init is called once while loading the game. Calls the init method of all the items to load
   * their textures (and maybe more).
   *
   * @param loader main loader
   */
  public static void init(Loader loader) {
    Dynamite.init(loader);
    Torch.init(loader);
    Heart.init(loader);
    Ice.init(loader);
    Star.init(loader);
  }

  /**
   * ONLY USE THIS METHOD TO GENERATE ITEMS.
   *
   * <p>Generates an item of the chosen type and adds it to all relevant lists. Keeps track of the
   * item and cleans it up when destroyed.
   *
   * @param type type of the item as described in {@link ItemMaster.ItemTypes}
   * @param position 3D coordinate to place the item
   * @return the generated item
   */
  public static Item generateItem(ItemTypes type, Vector3f position) {
    Item item;
    switch (type) {
      case DYNAMITE:
        item = new Dynamite(position);
        break;
      case TORCH:
        item = new Torch(position);
        break;
      case HEART:
        item = new Heart(position);
        break;
      case STAR:
        item = new Star(position);
        break;
      case ICE:
        item = new Ice(position);
        break;
      default:
        item = null;
        break;
    }
    addToItemList(item);
    return item;
  }

  /**
   * Called every frame to update all items. If an item is flagged as destroyed, remove that item
   * from all relevant lists (and clean out empty lists).
   */
  public static void update() {
    // Remove destroyed items from the list and update entities
    Iterator<Map.Entry<ItemTypes, List<Item>>> mapIterator = itemLists.entrySet().iterator();
    while (mapIterator.hasNext()) {
      List<Item> list = mapIterator.next().getValue();
      /*
      For loop allows new items being added as part of the item update method (like
      destroying a block
      with dynamite could spawn another dynamite). Otherwise we get a concurrent
      modification error.
       */
      for (int i = 0; i < list.size(); i++) {
        Item item = list.get(i);
        if (item.isDestroyed()) {
          // Remove item from list and entities
          Game.removeEntity(item);
          //noinspection SuspiciousListRemoveInLoop this is correct
          list.remove(i);
          items.remove(item);
          // Clean up list if empty
          if (list.isEmpty()) {
            mapIterator.remove();
          }
        } else {
          item.update();
        }
      }
    }
  }

  /**
   * Don't call this method directly. It is used by the Item Master to add new blocks to the game.
   *
   * <p>Adds them to the Item Master's personal lists and to the Entity render's list.
   *
   * @param item freshly generated item
   */
  private static void addToItemList(Item item) {
    // Get the list with the type of the block, if the list is absent, create it
    List<Item> list = itemLists.computeIfAbsent(item.getType(), k -> new ArrayList<>());

    // If the item is not destroyed, add it to the Game to be rendered
    if (!item.isDestroyed()) {
      // Add block to its type-specific list
      list.add(item);
      // Add to type-unspecific list
      items.add(item);
      // Add to render list
      Game.addEntity(item);
    }
  }

  public enum ItemTypes {
    DYNAMITE("DYNM"),
    TORCH("TRCH"),
    HEART("HART"),
    ICE("ICEE"),
    STAR("STAR");

    private final String itemId;

    ItemTypes(String itemId) {
      this.itemId = itemId;
    }

    public String getItemId() {
      return itemId;
    }

    /**
     * Get an ItemType by its id. Id is used to transmit item type via network protocol.
     *
     * @param id id of the item type
     * @return item type associated with the id
     */
    public static ItemTypes getItemTypeById(String id) {
      for (ItemTypes itemType : ItemTypes.values()) {
        if (itemType.itemId.equals(id)) {
          return itemType;
        }
      }
      return null;
    }
  }
}
