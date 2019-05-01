package net.packets.lists;

import game.Game;
import java.util.concurrent.CopyOnWriteArrayList;
import net.ServerLogic;
import net.packets.Packet;

public class PacketPlayerList extends Packet {

  private String[] playerList;
  CopyOnWriteArrayList<String> catalog = new CopyOnWriteArrayList<>();

  /**
   * Constructor if the packet arrives on the server side to be processed and sent to the player.
   *
   * @param clientId The client id of the player who requested the playerlist
   */
  public PacketPlayerList(int clientId) {
    super(PacketTypes.PLAYERLIST);
    setClientId(clientId);
    try {
    setData(ServerLogic.getPlayerList().toString());
    } catch (NullPointerException e) {
      addError("Not a server.");
    }
  }

  /**
   * Constructor to be used by the client to process the data which arrived by the Server.
   *
   * @param data The data from the server.
   */
  public PacketPlayerList(String data) {
    super(PacketTypes.PLAYERLIST);
    setData(data);
    try {
    playerList = data.split("║");
    } catch (NullPointerException e) {
      addError("There are no names in the list.");
    }
    validate();
  }

  /** Constructor to be used by the client to request the playerlist. */
  public PacketPlayerList() {
    super(PacketTypes.PLAYERLIST);
  }

  /** Validate whether the PlayerList only consists of extended Ascii. */
  @Override
  public void validate() {
    if(hasErrors()) {
      return;
    }
    for (int i = 0; i < playerList.length; i++) {
      if (!isExtendedAscii(playerList[i])) {
        return;
      }
    }
  }

  /**
   * Process the data.
   *
   * <p>On the server side send the data to the correct client.
   *
   * <p>On the client side add the playerlist to the catalog on the Game class to be displayed in
   * the PlayerList screen..
   */
  @Override
  @SuppressWarnings("Duplicates")
  public void processData() {
    if (getClientId() > 0) {
      // Server side
      this.sendToClient(getClientId());
    } else {
      // Client side
      if (hasErrors()) {
        logger.info(createErrorMessage());
      } else if (playerList[0].equals("OK")) {
        // logger.info(playerList[0]);
        for (int i = 1; i < playerList.length; i++) {
          catalog.add(playerList[i]);
        }
        Game.setPlayerList(catalog);
      } else {
        catalog.add(playerList[0]);
        Game.setPlayerList(catalog);
      }
    }
  }

  public CopyOnWriteArrayList<String> getCatalog() {
    return catalog;
  }
}
