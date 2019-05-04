package net.packets.gamestatus;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketReady {
  @Test
  public void checkPacketServer() {
    PacketReady p = new PacketReady(1);
    Assert.assertEquals(1, p.getClientId());
  }

  @Test
  public void checkPacketClient() {
    PacketReady p = new PacketReady();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }

  @Test
  public void checkNotLoggedInt() {
    PacketReady p = new PacketReady();
    p.processData();
    Assert.assertEquals("ERRORS: Not logged in yet.", p.createErrorMessage());
  }
}
