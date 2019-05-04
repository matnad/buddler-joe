package net.packets.loginlogout;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketDisconnect {

  @Test
  public void checkCorrectDisconnectPacketClient() {
    PacketDisconnect p = new PacketDisconnect();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }
}
