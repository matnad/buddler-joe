package net.packets.lobby;

import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.playerhandling.ServerPlayer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketJoinLobby {

  @Test
  public void checkDataNotAscii() {
    PacketJoinLobby p = new PacketJoinLobby("ਠ");
    Assert.assertEquals("ERRORS: Invalid characters, only extended ASCII.", p.createErrorMessage());
  }

  @Test
  public void checkLobbyDoesNotExist() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PacketJoinLobby p = new PacketJoinLobby(1, "TestLobby");
    p.processData();
    Assert.assertEquals("ERRORS: Chosen lobby does not exist.", p.createErrorMessage());
  }

  @Test
  public void checkNotLoggedIn() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    Lobby lobby = new Lobby("TestLobby", 1, "l");
    ServerLogic.getLobbyList().addLobby(lobby);
    PacketJoinLobby p = new PacketJoinLobby(1, "TestLobby");
    p.processData();
    Assert.assertEquals("ERRORS: Not logged in yet.", p.createErrorMessage());
  }

  @Test
  public void checkAlreadyInALobby() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    Lobby lobby = new Lobby("TestLobby", 1, "l");
    ServerLogic.getLobbyList().addLobby(lobby);
    ServerPlayer player = new ServerPlayer("TestPlayer", 1);
    player.setCurLobbyId(1);
    ServerLogic.getPlayerList().addPlayer(player);
    PacketJoinLobby p = new PacketJoinLobby(1, "TestLobby");
    p.processData();
    Assert.assertEquals(
        "ERRORS: Already in a lobby, leave current lobby first.", p.createErrorMessage());
  }
}
