package net.packets.block;

import game.Game;
import game.map.ClientMap;
import game.map.ServerMap;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketBlockDamage extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketBlockDamage.class);
  private int blockX;
  private int blockY;
  private float damage;
  private String[] dataArray;

  /**
   * Created by the client to send the damage done to a block to the server. Coordinates are in map
   * grid format, not world coordinates.
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
   * Server receives packet, validates it and is then ready to pass it to the ServerMap.
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

  /**
   * Client receives packet, validates it and is then ready to pass it to the ClientMap.
   *
   * @param data contains position of the block and damage dealt to the block
   */
  public PacketBlockDamage(String data) {
    super(PacketTypes.BLOCK_DAMAGE);
    setData(data);
    dataArray = data.split("║");
    validate(); // Validate and assign in one step
  }

  /** Validate if the packet contains properly formatted numbers and is of the right size. */
  @Override
  public void validate() {
    if (dataArray.length != 3) {
      addError("Invalid data.");
      return;
    }
    checkBlockPosInfo();
    try {
      damage = Float.parseFloat(dataArray[2]);
    } catch (NumberFormatException e) {
      addError("Invalid damage data.");
    }
  }

  /**
   * Passes the coordinates and data to the map for processing and further action. Will make sure
   * there is a map and will validate the coordinates to lie within the map.
   */
  @Override
  public void processData() {
    if (getClientId() > 0) {
      // Server side
      Lobby lobby = ServerLogic.getLobbyForClient(getClientId());
      ServerMap map = null;
      if (lobby == null) {
        addError("Client is not in a lobby.");
      } else {
        map = lobby.getMap();
      }
      if (map == null) {
        addError("No map found for lobby.");

      } else if (blockX > map.getWidth() - 1 || blockY > map.getHeight() - 1) {
        addError("Block lies outside of server map range.");
      }

      if (!hasErrors()) {
        map.damageBlock(getClientId(), blockX, blockY, damage);
      } else {
        logger.error("Errors while sending Block Damage Packet to Server. " + createErrorMessage());
      }
    } else {
      // Client side
      ClientMap map = Game.getMap();
      if (map == null) {
        addError("No map found on the client side.");
      } else if (blockX > map.getWidth() - 1 || blockY > map.getHeight() - 1) {
        addError("Block lies outside of client map range.");
      }
      if (!hasErrors()) {
        Game.getMap().damageBlock(0, blockX, blockY, damage);
      } else {
        logger.error("Errors while sending Block Damage Packet to Client. " + createErrorMessage());
      }
    }
  }
}
