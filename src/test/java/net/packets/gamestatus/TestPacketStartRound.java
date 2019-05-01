package net.packets.gamestatus;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketStartRound {
  @Test
  public void checkPacketServer() {
    PacketStartRound p = new PacketStartRound(1);
    Assert.assertEquals(1, p.getClientId());
  }

  @Test
  public void checkPacketClient() {
    PacketStartRound p = new PacketStartRound();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }

  @Test
  public void checkNotInGame() {
    PacketStartRound p = new PacketStartRound();
    p.processData();
    Assert.assertEquals("ERRORS: No map available.", p.createErrorMessage());
  }
}
