package net.packets.gamestatus;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketHistory {
  @Test
  public void checkCorrectPacketSever() {
    PacketHistory p = new PacketHistory(1, "TestHistory");
    Assert.assertFalse(p.hasErrors());
  }

  @Test
  public void checkCorrectPacketClient() {
    PacketHistory p = new PacketHistory("TestHistory");
    Assert.assertFalse(p.hasErrors());
  }

  @Test
  public void checkDataNull() {
    PacketHistory p = new PacketHistory(null);
    p.processData();
    Assert.assertEquals("ERRORS: Data is null. No data has been found.", p.createErrorMessage());
  }

  @Test
  public void checkCorrectStringToCatalog() {
    PacketHistory p = new PacketHistory("OK║Lobbies Of Running Games:║Test1");
    p.processData();
    Assert.assertEquals("Test1", p.getCatalog().get(1));
  }
}
