package entities.items;

import java.util.HashMap;

public class ItemServerState {
  private static HashMap<Integer, Item> itemState;

  public ItemServerState() {
    this.itemState = new HashMap<>();
  }

  public static void addItem(Item item) {
    itemState.put(item.getKey(), item);
  }

  public static void removeItem(int key) {
    itemState.remove(key);
  }
}
