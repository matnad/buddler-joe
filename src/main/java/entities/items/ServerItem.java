package entities.items;

import java.util.Objects;
import org.joml.Vector3f;

public class ServerItem {

  private int owner;
  private ItemMaster.ItemTypes type;
  private Long creationtime;
  private boolean exists;
  private Vector3f position;
  private int itemId;

  /**
   * Class to save important information of an Item on the server side to keep track of all items
   * and to have the state of the items saved.
   *
   * @param owner The owner of the item saved with the clientId.
   * @param type The type of the Item.
   * @param position The position of the Item placed by a client.
   */
  public ServerItem(int owner, ItemMaster.ItemTypes type, Vector3f position) {
    this.owner = owner;
    this.position = position;
    this.type = type;
    this.creationtime = System.currentTimeMillis();
    this.exists = true;
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public int getOwner() {
    return owner;
  }

  public ItemMaster.ItemTypes getType() {
    return type;
  }

  public boolean isExists() {
    return exists;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerItem that = (ServerItem) o;
    return owner == that.owner
        && exists == that.exists
        && itemId == that.itemId
        && type == that.type
        && Objects.equals(creationtime, that.creationtime)
        && Objects.equals(position, that.position);
  }
}
