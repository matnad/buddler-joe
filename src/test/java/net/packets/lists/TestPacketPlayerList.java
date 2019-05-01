package net.packets.lists;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketPlayerList {
  @Test
  public void checkNoNamesInList() {
    PacketPlayerList p = new PacketPlayerList(null);
    Assert.assertEquals("ERRORS: There are no names in the list.", p.createErrorMessage());
  }

  @Test
  public void checkNotAscii() {
    PacketPlayerList p = new PacketPlayerList("test║player║ඥ");
    p.processData();
    Assert.assertEquals("ERRORS: Invalid characters, only extended ASCII.", p.createErrorMessage());
  }

  @Test
  public void checkCorrectPacketCreationClient() {
    PacketPlayerList p = new PacketPlayerList();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }

  @Test
  public void checkCorrectPacketCreationServer() {
    PacketPlayerList p = new PacketPlayerList(1);
    Assert.assertEquals(1, p.getClientId());
  }

  @Test
  public void checkCreatedCorrectCatalog() {
    PacketPlayerList p = new PacketPlayerList("OK║player1║player2");
    p.processData();
    Assert.assertEquals("player1", p.getCatalog().get(0));
  }
}
