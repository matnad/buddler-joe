package net.packets.items;

import entities.items.Item;
import entities.items.ItemMaster;
import entities.items.ItemServerState;
import net.packets.Packet;
import org.joml.Vector3f;

public class PacketItemUsed extends Packet {
  private String[] dataArray;
  private int itemKey;
  private Vector3f position;
  private Item item;

  /**
   * Constructor to be called by the client when the item gets used.
   *
   * @param item the item which got used by the client.
   */
  public PacketItemUsed(Item item) {
    super(PacketTypes.ITEM_USED);
    this.item = item;
    validate();
    setData(dataArray[0]);
  }

  public PacketItemUsed(int clientId, String data) {
    super(PacketTypes.ITEM_USED);
    dataArray = data.split("║");
    setClientId(clientId);
    validate();
  }

  @Override
  public void validate() {
    if (getClientId() > 0) {
      if (dataArray.length != 1) {
        addError("Invalid item data.");
        return;
      }
      try {
        itemKey = Integer.parseInt(dataArray[0]);
      } catch (NumberFormatException e) {
        addError("Non-valid item key.");
      }
    } else {
      try {
        dataArray[0] = Integer.toString(item.getKey());
      } catch (NumberFormatException e) {
        addError("Non-valid item key.");
      }
    }
  }

  @Override
  public void processData() {
    if (getClientId() > 0) {
      ItemServerState.removeItem(itemKey);
    }
  }
}
