package entities.items;

import bin.Game;
import engine.render.Loader;
import entities.blocks.Block;
import org.joml.Vector3f;

import java.util.*;

/**
 * Create and manage items
 * Only ever create items using this class
 */
public class ItemMaster {

    //Organize Items in lists that can be accessed by their type
    private static Map<ItemTypes, List<Item>> itemLists = new HashMap<>();
    //Keep a list with all items
    private static List<Item> items = new ArrayList<>();

    public enum ItemTypes {
        DYNAMITE(1),
        TORCH(2);

        private int itemId;
        ItemTypes(int textureId) {
            this.itemId = textureId;
        }

        public int getItemId() {
            return itemId;
        }
    }

    /**
     * Init is called once while loading the game.
     * Calls the init method of all the items to load their textures (and maybe more).
     *
     * @param loader main loader
     */
    public static void init(Loader loader) {
        Dynamite.init(loader);
        Torch.init(loader);
    }

    /**
     * ONLY USE THIS METHOD TO GENERATE ITEMS!
     *
     * Generates an item of the chosen type and adds it to all relevant lists.
     * Keeps track of the item and cleans it up when destroyed.
     *
     * @param type type of the item as described in {@link ItemMaster.ItemTypes}
     * @param position 3D coordinate to place the item
     */
    public static Item generateItem(ItemTypes type, Vector3f position, Object... args) {
        Item item;
        switch (type) {
            case DYNAMITE:
                item = new Dynamite(position);
                break;
            case TORCH:
                item = new Torch(position);
                break;
            default:
                item = null;
                break;
        }
        if (item != null) {
            addToItemList(item);
        }
        return item;
    }

    /**
     * Called every frame to update all items.
     * If an item is flagged as destroyed, remove that item from all relevant lists (and clean out empty lists).
     */
    public static void update() {
        //Remove destroyed items from the list and update entities
        Iterator<Map.Entry<ItemTypes, List<Item>>> mapIterator = itemLists.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List<Item> list = mapIterator.next().getValue();
            /*
            For loop allows new items being added as part of the item update method (like destroying a block
            with dynamite could spawn another dynamite). Otherwise we get a concurrent modification error.
             */
            for (int i = 0; i < list.size(); i++) {
                Item item = list.get(i);
                if (item.isDestroyed()) {
                    //Remove item from list and entities
                    Game.removeEntity(item);
                    list.remove(i);
                    items.remove(item);
                    //Clean up list if empty
                    if(list.isEmpty()) {
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
     * Adds them to the Item Master's personal lists and to the Entity render's list.
     *
     * @param item freshly generated item
     */
    private static void addToItemList(Item item) {
        //Get the list with the type of the block, if the list is absent, create it
        List<Item> list = itemLists.computeIfAbsent(item.getType(), k -> new ArrayList<>());

        //If the item is not destroyed, add it to the Game to be rendered
        if (!item.isDestroyed()) {
            //Add block to its type-specific list
            list.add(item);
            //Add to type-unspecific list
            items.add(item);
            //Add to render list
            Game.addEntity(item);
        }
    }


}
