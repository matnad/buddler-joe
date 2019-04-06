package entities.items;

import java.util.concurrent.CopyOnWriteArrayList;

public class ServerItemState {
  private static CopyOnWriteArrayList<ServerItem> serverItemsList;
  private static int itemId = 0;

  /** Method to keep a state of all items on the server and to keep track of their existence. */
  public ServerItemState() {
    this.serverItemsList = new CopyOnWriteArrayList<>();
  }

  /**
   * Method to add an item to the current state list on the server.
   *
   * @param item The item to be added to the state list.
   */
  public static void addItem(ServerItem item) {
    if (!serverItemsList.contains(item)) {
      item.setItemId(++itemId);
      serverItemsList.add(item);
    }
  }

  /**
   * Method to remove an item from the state. Is the case when an Item has been destroyed or used.
   *
   * @param item The Item to be removed from the list.
   */
  public static void removeItem(ServerItem item) {
    if (serverItemsList.contains(item)) {
      serverItemsList.remove(item);
    }
  }

  public static void removeItemByItemId(int itemId) {
    for (int i = 0; i < serverItemsList.size(); i++) {
      if (serverItemsList.get(i).getItemId() == itemId) {
        removeItem(serverItemsList.get(i));
      }
    }
  }
}
