package net.packets.lobby;

import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.playerhandling.ServerPlayer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketLeaveLobby {
  @Test
  public void checkNotLoggedIn() {
    PacketLeaveLobby p = new PacketLeaveLobby();
    p.processData();
    Assert.assertEquals("ERRORS: Not logged in yet.", p.createErrorMessage());
  }

  @Test
  public void checkNotInALobby() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    ServerPlayer testPlayer = new ServerPlayer("TestPlayer", 1);
    ServerLogic.getPlayerList().addPlayer(testPlayer);
    PacketLeaveLobby p = new PacketLeaveLobby(1);
    p.processData();
    Assert.assertEquals("ERRORS: You are not in a lobby.", p.createErrorMessage());
  }

  @Test
  public void checkEverythingWorks() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    Lobby testLobby = new Lobby("TestLobby", 1, "l");
    ServerPlayer testPlayer = new ServerPlayer("TestPlayer", 1);
    testPlayer.setCurLobbyId(testLobby.getLobbyId());
    testLobby.addPlayer(testPlayer);
    ServerLogic.getLobbyList().addLobby(testLobby);
    ServerLogic.getPlayerList().addPlayer(testPlayer);
    PacketLeaveLobby p = new PacketLeaveLobby(1);
    p.processData();
    Assert.assertEquals(
        "", ServerLogic.getLobbyList().getLobby(testLobby.getLobbyId()).getPlayerNames());
  }
}
