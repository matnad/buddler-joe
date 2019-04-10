package net.packets.lists;

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

  /** Validate whether the PlayerList only consists of extended Ascii. */
  @Override
  public void validate() {
    if (dataArray[1] == null) {
      addError("There are no names in the list.");
    }
    for (int i = 0; i < dataArray.length; i++) {
      if (!isExtendedAscii(dataArray[i])) {
        break;
      }
    }
  }

  /**
   * Process the data.
   *
   * <p>On the server side send the data to the correct client.
   *
   * <p>On the client side print out the playerList to the player.
   */
  @Override
  public void processData() {
    if (getClientId() > 0) {
      // Server side
      this.sendToClient(getClientId());
    } else {
      // Client side
      if (!hasErrors()) {
        System.out.println("-------------------------------------");
        System.out.println("Players on the Server: \"");
        for (int i = 0; i < dataArray.length; i++) {
          System.out.println(dataArray[i]);
        }
        System.out.println("-------------------------------------");
      } else {
        System.out.println(getData());
      }
    }
  }
}
