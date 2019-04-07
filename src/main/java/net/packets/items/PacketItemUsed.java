package net.packets.items;

import entities.items.ServerItemState;
import net.packets.Packet;

public class PacketItemUsed extends Packet {

  private int itemId;

  public PacketItemUsed(int itemId) {
    super(PacketTypes.ITEM_USED);
    setData(Integer.toString(itemId));
  }

  public PacketItemUsed(String data) {
    super(PacketTypes.ITEM_USED);
    setData(data);
  }

  @Override
  public void validate() {
    try {
      itemId = Integer.parseInt(getData());
    } catch (NumberFormatException e) {
      addError("Invalid item id.");
    }
  }

  @Override
  public void processData() {
    ServerItemState.removeItemByItemId(itemId);
  }
}
