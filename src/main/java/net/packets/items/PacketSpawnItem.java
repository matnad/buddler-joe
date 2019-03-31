package net.packets.items;

import entities.items.Dynamite;
import entities.items.Item;
import entities.items.ItemMaster;
import entities.items.Torch;
import game.Game;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;
import net.packets.block.PacketBlockDamage;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketSpawnItem extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketBlockDamage.class);
  private int owner;
  private Vector3f position;
  private int type;

  private String[] dataArray;

  /**
   * Created by the client to tell the server he spawned an item.
   *
   * @param type item type according to {@link ItemMaster.ItemTypes}
   * @param position position of the item
   */
  public PacketSpawnItem(ItemMaster.ItemTypes type, Vector3f position) {
    super(Packet.PacketTypes.SPAWN_ITEM);
    setData("0║" + type.getItemId() + "║" + position.x + "║" + position.y + "║" + position.z);
    // No need to validate. No user input
  }

  /**
   * Server recieves packet, validates it and is then ready to pass it to the ServerMap.
   *
   * @param clientId clientId who sent the packet
   * @param data contains position of the block and damage dealt to the block
   */
  public PacketSpawnItem(int clientId, String data) {
    super(PacketTypes.SPAWN_ITEM);
    setClientId(clientId);
    position = new Vector3f();
    dataArray = data.split("║");
    dataArray[0] = "" + clientId;
    validate(); // Validate and assign in one step
    setData(clientId + "║" + type + "║" + position.x + "║" + position.y + "║" + position.z);
  }

  /**
   * Server recieves packet, validates it and is then ready to pass it to the ServerMap.
   *
   * @param data contains position of the block and damage dealt to the block
   */
  public PacketSpawnItem(String data) {
    super(Packet.PacketTypes.SPAWN_ITEM);
    setData(data);
    dataArray = data.split("║");
    validate(); // Validate and assign in one step
  }

  @Override
  public void validate() {
    if (dataArray.length != 5) {
      addError("Invalid item data.");
      return;
    }
    try {
      owner = Integer.parseInt(dataArray[0]);
    } catch (NumberFormatException e) {
      addError("Invalid item owner.");
    }
    try {
      position =
          new Vector3f(
              Float.parseFloat(dataArray[2]),
              Float.parseFloat(dataArray[3]),
              Float.parseFloat(dataArray[4]));
    } catch (NumberFormatException e) {
      addError("Invalid item position data.");
    }
    try {
      type = Integer.parseInt(dataArray[1]);
    } catch (NumberFormatException e) {
      addError("Invalid item type variable.");
    }
    if (type < 1) {
      addError("Invalid item type.");
    }
  }

  @Override
  public void processData() {
    ItemMaster.ItemTypes itemType = ItemMaster.ItemTypes.getItemById(type);
    if (itemType == null) {
      addError("Invalid item id.");
    }

    if (getClientId() > 0) {
      // Server side
      Lobby lobby = ServerLogic.getLobbyForClient(getClientId());
      if (lobby == null) {
        addError("Client is not in a lobby.");
      }

      if (!hasErrors()) {
        this.sendToLobby(lobby.getLobbyId());
      } else {
        logger.error(
            "Validation errors while sending Spawn Item Packet to server. " + createErrorMessage());
      }
    } else {
      // Client side
      if (!hasErrors()) {
        if (owner == Game.getActivePlayer().getClientId()) {
          return;
        }
        Item item = ItemMaster.generateItem(itemType, position);
        item.setOwned(false);
        if (item instanceof Torch) {
          ((Torch) item).checkForBlock();
        } else if (item instanceof Dynamite) {
          ((Dynamite) item).setActive(true);
        }
      } else {
        logger.error(
            "Validation errors while sending Spawn Item Packet to client. " + createErrorMessage());
      }
    }
  }
}
