package net.lobbyhandling;

import java.util.concurrent.CopyOnWriteArrayList;
import net.playerhandling.Player;
import org.junit.Assert;
import org.junit.Test;

public class TestLobby {

  @Test
  public void checkAddPlayerOk() {
    CopyOnWriteArrayList<Player> lobbyPlayer = new CopyOnWriteArrayList<>();
    Lobby testLobbby = new Lobby("TestLobby", 1, "small");
    Player testPlayer = new Player("TestPlayer", 1);
    testLobbby.setStatus("open");
    lobbyPlayer.add(testPlayer);
    testLobbby.addPlayer(testPlayer);
    Assert.assertEquals(lobbyPlayer, testLobbby.getLobbyPlayers());
  }

  @Test
  public void checkAddPlayerNotOpen() {
    CopyOnWriteArrayList<Player> lobbyPlayer = new CopyOnWriteArrayList<>();
    Lobby testLobbby = new Lobby("TestLobby", 1, "small");
    Player testPlayer = new Player("TestPlayer", 1);
    testLobbby.setStatus("finished");
    lobbyPlayer.add(testPlayer);
    testLobbby.addPlayer(testPlayer);
    Assert.assertNotEquals(lobbyPlayer, testLobbby.getLobbyPlayers());
  }

  @Test
  public void checkRemovePlayerOk() {
    CopyOnWriteArrayList<Player> lobbyPlayer = new CopyOnWriteArrayList<>();
    Lobby testLobbby = new Lobby("TestLobby", 1, "small");
    Player testPlayer = new Player("TestPlayer", 1);
    testLobbby.addPlayer(testPlayer);
    testLobbby.removePlayer(testPlayer.getClientId());
    Assert.assertEquals(lobbyPlayer, testLobbby.getLobbyPlayers());
  }

  @Test
  public void checkGetPlayerNames() {
    CopyOnWriteArrayList<Player> lobbyPlayer = new CopyOnWriteArrayList<>();
    Lobby testLobbby = new Lobby("TestLobby", 1, "small");
    Player testPlayer = new Player("TestPlayer", 1);
    testLobbby.addPlayer(testPlayer);
    Assert.assertEquals("TestPlayer║", testLobbby.getPlayerNames());
  }
}
