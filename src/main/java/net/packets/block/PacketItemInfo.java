package net.packets.block;

import net.packets.Packet;

public class PacketItemInfo extends Packet {

  private int blockX;
  private int blockY;
  private String item;
  private String[] dataArray;

  /**
   * Created by the server to pass it to the client to update the client over the identity of an
   * item.
   *
   * @param blockX X position of the Item
   * @param blockY Y position of the Item
   * @param item item name.
   */
  public PacketItemInfo(int blockX, int blockY, String item) {
    super(PacketTypes.BLOCK_DAMAGE);
    this.blockX = blockX;
    this.blockY = blockY;
    this.item = item;
    setData(blockX + "║" + blockY + "║" + item);
    // No need to validate. No user input
  }

  /**
   * Received by the player to be added to his UI and to let him know about the Item.
   *
   * @param data the position and identity of the Item.
   */
  public PacketItemInfo(String data) {
    super(PacketTypes.ITEM_INFO);
    setData(data);
    dataArray = data.split("║");
    validate(); // Validate and assign in one step
  }

  /** Check whether there is enough data and whether the item position is valid. */
  @Override
  public void validate() {
    if (dataArray.length != 3) {
      addError("Invalid data.");
      return;
    }
    checkBlockPosInfo();
    isExtendedAscii(dataArray[2]);
  }

  //TODO: (ALL) write a meaningful processData Method to process the information on the client side.

  @Override
  public void processData() {}
}
