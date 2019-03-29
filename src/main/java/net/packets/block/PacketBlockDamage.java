package net.packets.block;

import game.map.ServerMap;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;

public class PacketBlockDamage extends Packet {

  int blockX;
  int blockY;
  float damage;

  String[] dataArray;

  /**
   * Created by the client to send the damage done to a block to the server.
   *
   * @param blockX X position of the damaged block
   * @param blockY Y position of the damaged block
   * @param damage damage done to the block
   */
  public PacketBlockDamage(int blockX, int blockY, float damage) {
    super(PacketTypes.BLOCK_DAMAGE);
    this.blockX = blockX;
    this.blockY = blockY;
    this.damage = damage;
    setData(blockX + "║" + blockY + "║" + damage);
    // No need to validate. No user input
  }

  /**
   * Server recieves packet, validates it and is then ready to pass it to the ServerMap.
   *
   * @param clientId clientId who sent the packet
   * @param data contains position of the block and damage dealt to the block
   */
  public PacketBlockDamage(int clientId, String data) {
    super(PacketTypes.BLOCK_DAMAGE);
    setClientId(clientId);
    setData(data);
    dataArray = data.split("║");
    validate(); // Validate and assign in one step
  }

  @Override
  public void validate() {
    if (dataArray.length != 3) {
      addError("Invalid data.");
      return;
    }
    try {
      blockX = Integer.parseInt(dataArray[0]);
      blockY = Integer.parseInt(dataArray[1]);
    } catch (NumberFormatException e) {
      addError("Invalid position data.");
    }
    try {
      damage = Float.parseFloat(dataArray[2]);
    } catch (NumberFormatException e) {
      addError("Invalid damage data.");
    }
  }

  @Override
  public void processData() {
    Lobby lobby = ServerLogic.getLobbyForClient(getClientId());
    ServerMap map = null;
    if (lobby == null) {
      addError("Client is not in a lobby.");
    } else {
      map = lobby.getMap();
    }
    if (map == null) {
      addError("No map found for lobby.");
    }
    if (!hasErrors()) {
      map.damageBlock(blockX, blockY, damage);
    } else {
      // log errors.
    }
  }
}
