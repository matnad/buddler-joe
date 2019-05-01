package net.packets.loginlogout;

import net.ServerLogic;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketDisconnect {

  @Test
  public void checkCorrectDisconnectPacketServerNotConnected() {
    ServerLogic serverLogic = Mockito.mock(ServerLogic.class);
    PacketDisconnect p = new PacketDisconnect(1);
    p.processData();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }

  @Test
  public void checkCorrectDisconnectPacketClient() {
    PacketDisconnect p = new PacketDisconnect();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }
}
