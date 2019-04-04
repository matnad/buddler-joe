package net.packets.items;

import entities.items.Item;
import entities.items.ItemMaster;
import entities.items.ItemServerState;
import net.packets.Packet;
import org.joml.Vector3f;

public class PacketItemGenerated extends Packet {

    private String[] dataArray;
    private int itemKey;
    private Vector3f position;

    /**
     * Constructor to be called by the client when the item gets created.
     * @param item the item which got placed by the client.
     * @param position position of the item which was placed by the client.
     */

  public PacketItemGenerated(Item item, Vector3f position) {
    super(PacketTypes.ITEM_GENERATED);
    setData(item.getKey() + "║" + item.getType().getItemId() + "║" + position.x + "║" + position.y + "║" + position.z);
  }

  public PacketItemGenerated(int clientId, String data) {
      super(PacketTypes.ITEM_GENERATED);
      dataArray = data.split("║");
      setClientId(clientId);
      validate();
  }



  @Override
  public void validate() {
      if (dataArray.length != 5) {
          addError("Invalid item data.");
          return;
      }
      try {
          itemKey = Integer.parseInt(dataArray[0]);
      } catch (NumberFormatException e) {
          addError("Invalid item key.");
      }
      try {
          position =
                  new Vector3f(
                          Float.parseFloat(dataArray[2]),
                          Float.parseFloat(dataArray[3]),
                          Float.parseFloat(dataArray[4]));
      } catch (NumberFormatException e) {
          addError("Invalid position data.");
      }
      isExtendedAscii(dataArray[1]);
  }

  @Override
  public void processData() {
        if(getClientId() > 0) {
            ItemMaster.ItemTypes itemType = ItemMaster.ItemTypes.getItemTypeById(dataArray[1]);
            ItemServerState.addItem(ItemMaster.generateItem(itemType, position));
        }
  }
}
