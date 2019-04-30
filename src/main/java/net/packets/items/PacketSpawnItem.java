package net.packets.items;

import entities.items.Dynamite;
import entities.items.Heart;
import entities.items.Ice;
import entities.items.Item;
import entities.items.ItemMaster;
import entities.items.ServerItem;
import entities.items.Star;
import entities.items.Torch;
import game.Game;
import game.map.GameMap;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketSpawnItem extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketSpawnItem.class);
  private int owner;
  private Vector3f position;
  private String type;

  private String[] dataArray;

  /**
   * Created by the client to tell the server he spawned an item. Contains a dummy variable for
   * owner (will be set by the server).
   *
   * @param type item type according to {@link ItemMaster.ItemTypes}
   * @param position position of the item
   */
  public PacketSpawnItem(ItemMaster.ItemTypes type, Vector3f position) {
    super(Packet.PacketTypes.SPAWN_ITEM);
    setData(
        "0║" + type.getItemId() + "║" + position.x + "║" + position.y + "║" + position.z + "║0");
    // No need to validate. No user input
  }

  /**
   * Constructor to be used when a questionmark block gets destroyed to spawn an item and have
   * certain effects on certain players.
   *
   * @param type type of the item to be spawned
   * @param position position of the item to be spawned
   * @param clientId The client who destroyed the questionmark block
   */
  public PacketSpawnItem(ItemMaster.ItemTypes type, Vector3f position, int clientId) {
    super(Packet.PacketTypes.SPAWN_ITEM);
    ServerItem serverItem = new ServerItem(clientId, type, position);
    setClientId(clientId);
    try {
      ServerLogic.getLobbyForClient(getClientId()).getServerItemState().addItem(serverItem);
    } catch (NullPointerException e) {
      addError("Client not in a lobby.");
    }
    setData(
        clientId
            + "║"
            + type.getItemId()
            + "║"
            + position.x
            + "║"
            + position.y
            + "║"
            + position.z
            + "║"
            + serverItem.getItemId());
    // No need to validate. No user input
  }

  /**
   * Server receives packet, validates it and is then ready to broadcast it to the lobby. Will pass
   * on the same data but set the owner of the id equal to the owner of the packet.
   *
   * @param clientId clientId who sent the packet
   * @param data string that contains type and position of item
   */
  public PacketSpawnItem(int clientId, String data) {
    super(PacketTypes.SPAWN_ITEM);
    setClientId(clientId);
    dataArray = data.split("║");
    dataArray[0] = "" + clientId;
    validate(); // Validate and assign in one step
    if (hasErrors()) {
      return;
    }
    ServerItem serverItem =
        new ServerItem(clientId, ItemMaster.ItemTypes.getItemTypeById(type), position);
    try {
      ServerLogic.getLobbyForClient(getClientId()).getServerItemState().addItem(serverItem);
    } catch (NullPointerException e) {
      addError("Not in a lobby.");
    }
    setData(
        clientId
            + "║"
            + type
            + "║"
            + position.x
            + "║"
            + position.y
            + "║"
            + position.z
            + "║"
            + serverItem.getItemId());
  }

  /**
   * Client receives packet and creates an item owned by someone else.
   *
   * @param data string that contains owner, type and position of an item
   */
  public PacketSpawnItem(String data) {
    super(Packet.PacketTypes.SPAWN_ITEM);
    setData(data);
    dataArray = data.split("║");
    validate(); // Validate and assign in one step
  }

  /**
   * Validation of the data on the package. Which checks:
   *
   * <p>Whether the data array has the right length
   *
   * <p>Whether the owner and itemId are Integers
   *
   * <p>Whether the coordinates aer floats while also changing the grid coordinates in real map
   * coordinates
   *
   * <p>Whether the item Type is ascended ascii.
   */
  @Override
  public void validate() {
    if (dataArray.length < 5) {
      addError("Invalid item data.");
      return;
    }
    type = dataArray[1];
    if (!isExtendedAscii(type)) {
      addError("Invalid item type.");
      return;
    }
    try {
      owner = Integer.parseInt(dataArray[0]);
    } catch (NumberFormatException e) {
      addError("Invalid item owner.");
    }
    try {
      if (ItemMaster.ItemTypes.getItemTypeById(type) == ItemMaster.ItemTypes.TORCH) {
        position =
            new Vector3f(
                Float.parseFloat(dataArray[2]),
                -(Float.parseFloat(dataArray[3])),
                Float.parseFloat(dataArray[4]));
      } else {
        position =
            new Vector3f(
                Float.parseFloat(dataArray[2]) * GameMap.getDim() + GameMap.getDim() / 2,
                -(Float.parseFloat(dataArray[3])) * GameMap.getDim() - GameMap.getDim() / 2,
                Float.parseFloat(dataArray[4]));
      }
    } catch (NumberFormatException e) {
      addError("Invalid item position data.");
    }
  }

  /**
   * Server and client logic for item spawning.
   *
   * <p>The server will check the type of the item and if the player is in a lobby. Then the item
   * will be broadcast.
   *
   * <p>The client will do nothing if he owns the item (the item should already be spawned in that
   * case). If the client doesn't own the item, he will create it at the specified position with the
   * ownership flag set to false. Item specific flags / actions can be triggered from here as well.
   */
  @Override
  public void processData() {
    ItemMaster.ItemTypes itemType = ItemMaster.ItemTypes.getItemTypeById(type);
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
        if (itemType == ItemMaster.ItemTypes.TORCH
            && owner == Game.getActivePlayer().getClientId()) {
          return;
        }
        Item item = ItemMaster.generateItem(itemType, position);
        if (item instanceof Torch) {
          ((Torch) item).checkForBlock(); // Attach to a block if placed on one.
        } else if (item instanceof Dynamite) {
          if (owner == Game.getActivePlayer().getClientId()) {
            item.setOwned(true);
          }
          ((Dynamite) item).setActive(true); // Start ticking
        } else if (item instanceof Heart) {
          if (owner == Game.getActivePlayer().getClientId()) {
            item.setOwned(true);
          }
        } else if (item instanceof Ice) {
          if (owner == Game.getActivePlayer().getClientId()) {
            item.setOwned(true);
          }
        } else if (item instanceof Star) {
          if (owner == Game.getActivePlayer().getClientId()) {
            item.setOwned(true);
          }
        }
      } else {
        logger.error(
            "Validation errors while sending Spawn Item Packet to client. " + createErrorMessage());
      }
    }
  }
}
