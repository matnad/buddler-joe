package net.lobbyhandling;

import java.util.concurrent.CopyOnWriteArrayList;

import net.ServerLogic;
import net.playerhandling.ServerPlayer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

public class TestLobby {

  @Test
  public void checkAddPlayerOk() {
    CopyOnWriteArrayList<ServerPlayer> lobbyPlayer = new CopyOnWriteArrayList<>();
    Lobby testLobbby = new Lobby("TestLobby", 1, "small");
    ServerPlayer testPlayer = new ServerPlayer("TestPlayer", 1);
    testLobbby.setStatus("open");
    lobbyPlayer.add(testPlayer);
    testLobbby.addPlayer(testPlayer);
    Assert.assertEquals(lobbyPlayer, testLobbby.getLobbyPlayers());
  }

  @Test
  public void checkAddPlayerAlreadyInLobby() {
    CopyOnWriteArrayList<ServerPlayer> lobbyPlayer = new CopyOnWriteArrayList<>();
    Lobby testLobbby = new Lobby("TestLobby", 1, "small");
    ServerPlayer testPlayer = new ServerPlayer("TestPlayer", 1);
    lobbyPlayer.add(testPlayer);
    testLobbby.addPlayer(testPlayer);
    testLobbby.addPlayer(testPlayer);
    Assert.assertEquals(lobbyPlayer, testLobbby.getLobbyPlayers());
  }

  @Test
  public void checkAddPlayerNotOpen() {
    CopyOnWriteArrayList<ServerPlayer> lobbyPlayer = new CopyOnWriteArrayList<>();
    Lobby testLobbby = new Lobby("TestLobby", 1, "small");
    ServerPlayer testPlayer = new ServerPlayer("TestPlayer", 1);
    testLobbby.setStatus("finished");
    lobbyPlayer.add(testPlayer);
    testLobbby.addPlayer(testPlayer);
    Assert.assertNotEquals(lobbyPlayer, testLobbby.getLobbyPlayers());
  }

  @Test
  public void checkRemovePlayer() {
    CopyOnWriteArrayList<ServerPlayer> lobbyPlayer = new CopyOnWriteArrayList<>();
    Lobby testLobbby = new Lobby("TestLobby", 1, "small");
    ServerPlayer testPlayer = new ServerPlayer("TestPlayer", 1);
    testLobbby.addPlayer(testPlayer);
    testLobbby.removePlayer(testPlayer.getClientId());
    Assert.assertEquals(lobbyPlayer, testLobbby.getLobbyPlayers());
  }

  @Test
  public void checkGetPlayerNames() {
    Lobby testLobbby = new Lobby("TestLobby", 1, "small");
    ServerPlayer testPlayer = new ServerPlayer("TestPlayer", 1);
    testLobbby.addPlayer(testPlayer);
    Assert.assertEquals("TestPlayer║", testLobbby.getPlayerNames());
  }

  @Test
  public void checkAllPlayersNotReady() {
    Lobby testLobby = new Lobby("TestLobby", 1, "small");
    ServerPlayer testPlayer1 = new ServerPlayer("TestPlayer1", 1);
    ServerPlayer testPlayer2 = new ServerPlayer("TestPlayer2", 2);
    testLobby.addPlayer(testPlayer1);
    testLobby.addPlayer(testPlayer2);
    testPlayer1.setReady(true);
    Assert.assertFalse(testLobby.allPlayersReady());
  }

  @Test
  public void checkGetPlayerNamesIdsReady() {
    Lobby testLobby = new Lobby("TestLobby", 1, "small");
    ServerPlayer testPlayer1 = new ServerPlayer("TestPlayer1", 1);
    ServerPlayer testPlayer2 = new ServerPlayer("TestPlayer2", 2);
    testLobby.addPlayer(testPlayer1);
    testLobby.addPlayer(testPlayer2);
    testPlayer1.setReady(true);
    testPlayer2.setReady(true);
    Assert.assertEquals(
        1 + "║" + "TestPlayer1" + "║" + true + "║" + 2 + "║" + "TestPlayer2" + "║" + true + "║",
        testLobby.getPlayerNamesIdsReadies());
  }
}
