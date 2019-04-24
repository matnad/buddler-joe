package net.lobbyhandling;

import entities.items.ServerItem;

import java.util.concurrent.CopyOnWriteArrayList;

public class ServerItemState {
  private CopyOnWriteArrayList<ServerItem> serverItemsList;
  private int itemId = 0;

  /** Method to keep a state of all items on the server and to keep track of their existence. */
  public ServerItemState() {
    serverItemsList = new CopyOnWriteArrayList<>();
  }

  /**
   * Method to add an item to the current state list on the server.
   *
   * @param item The item to be added to the state list.
   */
  public void addItem(ServerItem item) {
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
  public void removeItem(ServerItem item) {
    serverItemsList.remove(item);
  }

  /**
   * Method to remove an item form the server state with the itemId as identifier. Is the case when
   * an item is set as destroyed by the owner.
   *
   * @param itemId The item Id of the item which has been destroyed.
   */
  public void removeItemByItemId(int itemId) {
    for (int i = 0; i < serverItemsList.size(); i++) {
      if (serverItemsList.get(i).getItemId() == itemId) {
        removeItem(serverItemsList.get(i));
      }
    }
  }

  public CopyOnWriteArrayList<ServerItem> getServerItemsList() {
    return serverItemsList;
  }
}
