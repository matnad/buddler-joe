package net.packets.lobby;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketCreateLobby {

  @Test
  public void checkStringNullClient() {
    PacketCreateLobby p = new PacketCreateLobby(null);
    Assert.assertEquals("ERRORS: There is no String attached.", p.createErrorMessage());
  }

  @Test
  public void checkStringNullServer() {
    PacketCreateLobby p = new PacketCreateLobby(1, null);
    Assert.assertEquals("ERRORS: There is no String attached.", p.createErrorMessage());
  }

  @Test
  public void checkNoMapsizeAttached() {
    PacketCreateLobby p = new PacketCreateLobby(1, "TestString");
    Assert.assertEquals("ERRORS: No mapsize found.", p.createErrorMessage());
  }

  @Test
  public void checkWrongMapsizeAttached() {
    PacketCreateLobby p = new PacketCreateLobby(1, "TestString║Test");
    Assert.assertEquals("ERRORS: Illegal mapsize.", p.createErrorMessage());
  }

  @Test
  public void checkTooLongLobbynameAttached() {
    PacketCreateLobby p =
        new PacketCreateLobby(1, "TestStringTooLongBecauseMoreThan14Characters║l");
    Assert.assertEquals(
        "ERRORS: Lobbyname to long. Maximum is 16 Characters.", p.createErrorMessage());
  }

  @Test
  public void checkTooShortLobbynameAttached() {
    PacketCreateLobby p = new PacketCreateLobby(1, "aTE║l");
    Assert.assertEquals(
        "ERRORS: Lobbyname to short. Minimum is 4 Characters.", p.createErrorMessage());
  }

  @Test
  public void checkNotAsciiCharacter() {
    PacketCreateLobby p = new PacketCreateLobby("੧੧੧੧║l");
    Assert.assertEquals("ERRORS: Invalid characters, only extended ASCII.", p.createErrorMessage());
  }

  @Test
  public void checkNotLoggedIntoLobby() {
    PacketCreateLobby p = new PacketCreateLobby("TestLobby║l");
    p.processData();
    Assert.assertEquals("ERRORS: Not logged in yet. Not logged in yet.", p.createErrorMessage());
  }
}
