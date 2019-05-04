package net.packets.pingpong;

import net.ServerLogic;
import net.playerhandling.PingManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketPong {
  @Test
  public void checkPongNull() {
    PacketPong p = new PacketPong(null);
    Assert.assertEquals("ERRORS: Empty message", p.createErrorMessage());
  }

  @Test
  public void checkPongNotInt() {
    PacketPong p = new PacketPong(1, "Test");
    p.processData();
    Assert.assertEquals("ERRORS: Not an Integer.", p.createErrorMessage());
  }

  @Test
  public void checkPongCalculateCorrectTimeClient() {
    PingManager pingManager = Mockito.spy(PingManager.class);
    PacketPong p = new PacketPong("1");
    p.processData();
    Assert.assertEquals(0, pingManager.getPing());
  }

  @Test
  public void checkPongCalculateCorrectTimeServer() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PingManager pingManager = Mockito.spy(PingManager.class);
    PacketPong p = new PacketPong(1, "1");
    p.processData();
    Assert.assertEquals(0, pingManager.getPing());
  }
}
