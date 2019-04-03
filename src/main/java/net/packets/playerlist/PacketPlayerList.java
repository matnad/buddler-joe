package net.packets.playerlist;

import entities.items.ItemMaster;
import net.ServerLogic;
import net.packets.Packet;

public class PacketPlayerList extends Packet {

  private String[] dataArray;

  /**
   * Constructor if the packet arrives on the server side to be processed and sent to the player.
   *
   * @param clientId The client id of the player who requested the playerlist
   */
  public PacketPlayerList(int clientId) {
    super(PacketTypes.PLAYERLIST);
    setClientId(clientId);
    setData(ServerLogic.getPlayerList().toString());
  }

  /**
   * Constructor to be used by the client to process the data which arrived by the Server.
   *
   * @param data The data from the server.
   */
  public PacketPlayerList(String data) {
    super(PacketTypes.PLAYERLIST);
    setData(data);
    dataArray = data.split("║");
  }

  /** Constructor to be used by the client to request the playerlist. */
  public PacketPlayerList() {
    super(PacketTypes.PLAYERLIST);
  }

  @Override
  public void validate() {
    for (int i = 0; i < dataArray.length; i++) {
      if (!isExtendedAscii(dataArray[i])) {
        break;
      }
    }
  }

  @Override
  public void processData() {
    if (getClientId() > 0) {
      // Server side
      this.sendToClient(getClientId());
    } else {
      if (!hasErrors()) {
        System.out.println("-------------------------------------");
        System.out.println("Players on the Server \"" + dataArray[0] + "\":");
        for (int i = 1; i < dataArray.length; i++) {
          System.out.println(dataArray[i]);
        }
        System.out.println("-------------------------------------");
      } else {
          System.out.println(getData());
      }
    }
  }
}
