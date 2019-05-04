package net.packets.pingpong;

import net.ServerLogic;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketPing {
  @Test
  public void checkDataNull() {
    PacketPing p = new PacketPing(null);
    p.processData();
    Assert.assertEquals("ERRORS: Empty message", p.createErrorMessage());
  }

  @Test
  public void checkDataNoDigit() {
    PacketPing p = new PacketPing(1, "TEST");
    p.processData();
    Assert.assertEquals("ERRORS: Invalid ping number", p.createErrorMessage());
  }

  @Test
  public void checkPingPacketWithoutErrorsServer() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PacketPing p = new PacketPing(1, "1");
    p.processData();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }

  @Test
  public void checkPingPacketWithoutErrorsClient() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PacketPing p = new PacketPing("1");
    p.processData();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }
}
