package net.packets.gamestatus;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketReady {
  @Test
  public void checkPacketServer() {
    PacketReady p = new PacketReady(1);
    Assert.assertEquals(1, p.getClientId());
  }
}
