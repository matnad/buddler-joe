package net.packets.loginlogout;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketDisconnect {

  @Test
  public void checkCorrectDisconnectPacketServerNotConnected() {
    PacketDisconnect p = new PacketDisconnect(11);
    p.processData();
    Assert.assertEquals("ERRORS: Not Connected to the Server.", p.createErrorMessage());
  }

  @Test
  public void checkCorrectDisconnectPacketClient() {
    PacketDisconnect p = new PacketDisconnect();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }
}
