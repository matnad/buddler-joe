package net.lobbyhandling;

import entities.items.ItemMaster;
import entities.items.ServerItem;
import java.util.concurrent.CopyOnWriteArrayList;
import org.joml.Vector3f;
import org.junit.Assert;
import org.junit.Test;

public class TestServerItemState {

  @Test
  public void checkAddItemCorrect() {
    ServerItemState itemState = new ServerItemState();
    CopyOnWriteArrayList<ServerItem> serverItemsList = new CopyOnWriteArrayList<>();
    ServerItem testItem1 = new ServerItem(1, ItemMaster.ItemTypes.HEART, new Vector3f(1, 2, 3));
    ServerItem testItem2 = new ServerItem(2, ItemMaster.ItemTypes.ICE, new Vector3f(3, 7, 9));
    serverItemsList.add(testItem1);
    serverItemsList.add(testItem2);
    itemState.addItem(testItem1);
    itemState.addItem(testItem2);
    Assert.assertEquals(itemState.getServerItemsList(),(serverItemsList));
  }

  @Test
  public void checkAddItemIncorrect() {
    ServerItemState itemState = new ServerItemState();
    CopyOnWriteArrayList<ServerItem> serverItemsList = new CopyOnWriteArrayList<>();
    ServerItem testItem1 = new ServerItem(1, ItemMaster.ItemTypes.HEART, new Vector3f(1, 2, 3));
    ServerItem testItem2 = new ServerItem(2, ItemMaster.ItemTypes.ICE, new Vector3f(3, 7, 9));
    serverItemsList.add(testItem1);
    serverItemsList.add(testItem2);
    itemState.addItem(testItem1);
    Assert.assertNotEquals(itemState.getServerItemsList(),(serverItemsList));
  }

  @Test
  public void checkAddItemDouble() {
    ServerItemState itemState = new ServerItemState();
    CopyOnWriteArrayList<ServerItem> serverItemsList = new CopyOnWriteArrayList<>();
    ServerItem testItem1 = new ServerItem(1, ItemMaster.ItemTypes.HEART, new Vector3f(1, 2, 3));
    serverItemsList.add(testItem1);
    itemState.addItem(testItem1);
    itemState.addItem(testItem1);
    Assert.assertEquals(itemState.getServerItemsList(),(serverItemsList));
  }

  @Test
  public void checkRemoveItemCorrect() {
    ServerItemState itemState = new ServerItemState();
    CopyOnWriteArrayList<ServerItem> serverItemsList = new CopyOnWriteArrayList<>();
    ServerItem testItem1 = new ServerItem(1, ItemMaster.ItemTypes.HEART, new Vector3f(1, 2, 3));
    ServerItem testItem2 = new ServerItem(2, ItemMaster.ItemTypes.ICE, new Vector3f(3, 7, 9));
    serverItemsList.add(testItem2);
    itemState.addItem(testItem1);
    itemState.addItem(testItem2);
    itemState.removeItemByItemId(testItem1.getItemId());
    Assert.assertEquals(itemState.getServerItemsList(),(serverItemsList));
  }

  @Test
  public void checkRemoveItemItemNotInList() {
    ServerItemState itemState = new ServerItemState();
    CopyOnWriteArrayList<ServerItem> serverItemsList = new CopyOnWriteArrayList<>();
    ServerItem testItem1 = new ServerItem(1, ItemMaster.ItemTypes.HEART, new Vector3f(1, 2, 3));
    ServerItem testItem2 = new ServerItem(2, ItemMaster.ItemTypes.ICE, new Vector3f(3, 7, 9));
    serverItemsList.add(testItem2);
    itemState.addItem(testItem2);
    itemState.removeItemByItemId(testItem1.getItemId());
    Assert.assertEquals(itemState.getServerItemsList(),(serverItemsList));
  }
}
