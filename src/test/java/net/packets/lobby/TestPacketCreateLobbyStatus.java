package net.packets.lobby;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketCreateLobbyStatus {
  @Test
  public void checkNoStatusAttachedClient() {
    PacketCreateLobbyStatus p = new PacketCreateLobbyStatus(null);
    Assert.assertEquals("ERRORS: No Status found.", p.createErrorMessage());
  }

  @Test
  public void checkNoStatusAttachedServer() {
    PacketCreateLobbyStatus p = new PacketCreateLobbyStatus(1, null);
    Assert.assertEquals("ERRORS: No Status found.", p.createErrorMessage());
  }

  @Test
  public void checkStatusNotAscii() {
    PacketCreateLobbyStatus p = new PacketCreateLobbyStatus(1, "ਠ");
    Assert.assertEquals("ERRORS: Invalid characters, only extended ASCII.", p.createErrorMessage());
  }

    @Test
    public void checkGameNotCreated() {
        PacketCreateLobbyStatus p = new PacketCreateLobbyStatus("NOTOK");
        p.processData();
        Assert.assertEquals("ERRORS: ", p.createErrorMessage());
    }
}
