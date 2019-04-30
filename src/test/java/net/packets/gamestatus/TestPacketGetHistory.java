package net.packets.gamestatus;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketGetHistory {
  @Test
  public void checkCorrectClientId() {
    PacketGetHistory p = new PacketGetHistory(1);
    Assert.assertEquals(1, p.getClientId());
  }

  @Test
  public void checkNotCorrectlyLoggedIn() {
    PacketGetHistory p = new PacketGetHistory();
    p.processData();
    Assert.assertEquals("ERRORS: Not loggedin yet.", p.createErrorMessage());
  }
}
