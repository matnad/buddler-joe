package net.packets.lobby;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketLobbyOverview {
  @Test
  public void checkDataNull() {
    PacketLobbyOverview p = new PacketLobbyOverview(1, null);
    Assert.assertEquals("ERRORS: No Data available.", p.createErrorMessage());
  }

  @Test
  public void checkDataNotAscii() {
    PacketLobbyOverview p = new PacketLobbyOverview("ਠ");
    Assert.assertEquals("ERRORS: Invalid characters, only extended ASCII.", p.createErrorMessage());
  }

  @Test
  public void checkNoOppenLobbiesAvailable() {
    PacketLobbyOverview p = new PacketLobbyOverview("Test║No open Lobbies");
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }

  @Test
  public void checkDataFormatError() {
    PacketLobbyOverview p = new PacketLobbyOverview("Test║No open Lobbies");
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }
}
