package net.packets.items;

import entities.items.ItemMaster;
import net.ServerLogic;
import org.joml.Vector3f;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketSpawnItem {

  @Test
  public void checkInvalidItemData() {
    PacketSpawnItem spawnItem = new PacketSpawnItem("Test1");
    Assert.assertEquals("ERRORS: Invalid item data.", spawnItem.createErrorMessage());
  }

  @Test
  public void checkInvalidItemType() {
    PacketSpawnItem spawnItem = new PacketSpawnItem("Test║ঊ║test║test║test");
    Assert.assertEquals(
        "ERRORS: Invalid characters, only extended ASCII. Invalid item type.",
        spawnItem.createErrorMessage());
  }

  @Test
  public void checkInvalidItemOwner() {
    PacketSpawnItem spawnItem = new PacketSpawnItem("Test║test║test║test║test");
    Assert.assertEquals(
        "ERRORS: Invalid item owner. Invalid item position data.", spawnItem.createErrorMessage());
  }

  @Test
  public void checkInvalidItemPosition() {
    PacketSpawnItem spawnItem = new PacketSpawnItem("1║test║abc║1.0f║1.0f");
    Assert.assertEquals("ERRORS: Invalid item position data.", spawnItem.createErrorMessage());
  }

  @Test
  public void checkValidData() {
    PacketSpawnItem spawnItem = new PacketSpawnItem("1║STAR║1.0f║1.0f║1.0f");
    Assert.assertEquals("ERRORS: ", spawnItem.createErrorMessage());
  }

    @Test
    public void checkValidDataTorch() {
        PacketSpawnItem spawnItem = new PacketSpawnItem("1║TRCH║1.0f║1.0f║1.0f");
        Assert.assertEquals("ERRORS: ", spawnItem.createErrorMessage());
    }

    @Test
    public void checkClientCreatesValidPacket() {
      PacketSpawnItem packetSpawnItem = new PacketSpawnItem(ItemMaster.ItemTypes.STAR, new Vector3f(1,2,3));
      Assert.assertEquals("0║STAR║1.0║2.0║3.0║0", packetSpawnItem.getData());
    }

    @Test
    public void checkQmarkBlockCreatesValidPacket() {
        ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
        PacketSpawnItem packetSpawnItem = new PacketSpawnItem(ItemMaster.ItemTypes.STAR, new Vector3f(1,2,3), 3);
        Assert.assertEquals("3║STAR║1.0║2.0║3.0║0", packetSpawnItem.getData());
    }

    @Test
    public void checkServerBroadcastCreatesValidPacket() {
        ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
        PacketSpawnItem packetSpawnItem = new PacketSpawnItem(1, "1║STAR║2.0║2.0║2.0");
        Assert.assertEquals("1║STAR║15.0║-15.0║2.0║0", packetSpawnItem.getData());
    }
}
