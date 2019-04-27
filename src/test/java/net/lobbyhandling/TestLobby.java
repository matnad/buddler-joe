package net.lobbyhandling;

import java.util.concurrent.CopyOnWriteArrayList;
import net.playerhandling.ServerPlayer;
import org.junit.Assert;
import org.junit.Test;

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
  public void checkRemovePlayerOk() {
    CopyOnWriteArrayList<ServerPlayer> lobbyPlayer = new CopyOnWriteArrayList<>();
    Lobby testLobbby = new Lobby("TestLobby", 1, "small");
    ServerPlayer testPlayer = new ServerPlayer("TestPlayer", 1);
    testLobbby.addPlayer(testPlayer);
    testLobbby.removePlayer(testPlayer.getClientId());
    Assert.assertEquals(lobbyPlayer, testLobbby.getLobbyPlayers());
  }

  @Test
  public void checkGetPlayerNames() {
    CopyOnWriteArrayList<ServerPlayer> lobbyPlayer = new CopyOnWriteArrayList<>();
    Lobby testLobbby = new Lobby("TestLobby", 1, "small");
    ServerPlayer testPlayer = new ServerPlayer("TestPlayer", 1);
    testLobbby.addPlayer(testPlayer);
    Assert.assertEquals("TestPlayer║", testLobbby.getPlayerNames());
  }
}
