package net.packets.items;

import net.ServerLogic;
import net.packets.Packet;

/**
 * Packet that gets send from the Client to the Server, to inform the Server that an Item has been
 * destroyed to delete it from the Item State on the Server. Packet-Code: ITMUS
 *
 * @author Joe's Buddler corp.
 */
public class PacketItemUsed extends Packet {

  private int itemId;

  /**
   * Constructor that is used by the Client to build the Packet upon destroying an Item.
   *
   * @param itemId The item Id of the item that has been destroyed.
   */
  public PacketItemUsed(int itemId) {
    super(PacketTypes.ITEM_USED);
    setData(Integer.toString(itemId));
  }

  /**
   * Constructor used by the Server to construct a PacketItemUsed and to validate the information.
   *
   * @param data The data received from the Client.
   */
  public PacketItemUsed(int clientId, String data) {
    super(PacketTypes.ITEM_USED);
    setData(data);
    setClientId(clientId);
    validate();
  }

  /** Validation method to check the data whether it is an Integer or not. */
  @Override
  public void validate() {
    try {
      itemId = Integer.parseInt(getData());
    } catch (NumberFormatException e) {
      addError("Invalid item id.");
    }
    if (ServerLogic.getLobbyForClient(getClientId()) == null) {
      addError("Client is not in a lobby.");
    }
  }

  /**
   * Method to process the itemId received and to delete the item out of the ItemServerState List.
   */
  @Override
  public void processData() {
    if (hasErrors()) {
      System.out.println(createErrorMessage());
    } else {
      ServerLogic.getLobbyForClient(getClientId()).getServerItemState().removeItemByItemId(itemId);
    }
  }
}
