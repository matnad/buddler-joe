package entities.items;

import bin.Game;
import engine.render.Loader;
import entities.blocks.Block;
import org.joml.Vector3f;

import java.util.*;

public class ItemMaster {

    //Organize Items in lists that can be accessed by their type
    private static Map<ItemTypes, List<Item>> itemLists = new HashMap<>();
    //Keep a list with all items
    private static List<Item> items = new ArrayList<>();

    public enum ItemTypes {
        DYNAMITE(1),
        ICEBLOCK(2);

        private int itemId;
        ItemTypes(int textureId) {
            this.itemId = textureId;
        }

        public int getItemId() {
            return itemId;
        }
    }

    public static void init(Loader loader) {
        Dynamite.init(loader);
    }

    public static Item generateItem(ItemTypes type, Vector3f position) {
        Item item;
        switch (type) {
            case DYNAMITE:
                item = new Dynamite(position);
                break;
//            case ICEBLOCK:
//                item = new DirtBlock(position);
//                break;
            default:
                item = null;
                break;
        }
        if (item != null) {
            addToItemList(item);
        }
        return item;
    }

    public static void update() {
        //Remove destroyed items from the list and update entities
        Iterator<Map.Entry<ItemTypes, List<Item>>> mapIterator = itemLists.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List<Item> list = mapIterator.next().getValue();
            Iterator<Item> iterator = list.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                if (item.isDestroyed()) {
                    //Remove item from list and entities
                    Game.removeEntity(item);
                    iterator.remove();
                    items.remove(item);
                    //Clean up list if empty
                    if(list.isEmpty()) {
                        mapIterator.remove();
                    }
                }
            }
        }

        //Update all active items
        for (Item item : items) {
            item.update();
        }
    }

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
