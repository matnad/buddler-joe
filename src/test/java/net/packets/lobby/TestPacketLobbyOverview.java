package net.packets.lobby;

import game.Game;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

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

  @Test
  public void checkEverythingIsFine() {
    Game game = Mockito.spy(Game.class);
    PacketLobbyOverview p = new PacketLobbyOverview("OK║No open Lobbies");
    p.processData();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }
}
