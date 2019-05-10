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

  @Test
  public void checkGetFreshNameStandard() {
    Lobby testLobby = new Lobby("TestLobby", 1, "small");
    Assert.assertEquals("TestLobby1", testLobby.getFreshName(testLobby.getLobbyName()));
  }

  @Test
  public void checkGetFreshName_NameIsAlready16() {
    Lobby testLobby = new Lobby("TestLobbyCabaret", 1, "small");
    Assert.assertEquals("TestLobbyCabare1", testLobby.getFreshName(testLobby.getLobbyName()));
  }

  @Test
  public void checkGetFreshName_increasing() {
    Lobby testLobby = new Lobby("TestLobbyCabar20", 1, "small");
    Assert.assertEquals("TestLobbyCabar21", testLobby.getFreshName(testLobby.getLobbyName()));
  }

  @Test
  public void checkGetFreshName_cutting() {
    Lobby testLobby = new Lobby("TestLobbyCabare9", 1, "small");
    Assert.assertEquals("TestLobbyCabar10", testLobby.getFreshName(testLobby.getLobbyName()));
  }

  @Test
  public void checkGetFreshName_allNumbers() {
    Lobby testLobby = new Lobby("1234567891011121", 1, "small");
    Assert.assertEquals("1234567891011122", testLobby.getFreshName(testLobby.getLobbyName()));
  }

  @Test
  public void checkGetFreshName_unconventionalOne() {
    Lobby testLobby = new Lobby("TestLobbyCab0001", 1, "small");
    Assert.assertEquals("TestLobbyCab2", testLobby.getFreshName(testLobby.getLobbyName()));
  }

  @Test
  public void checkGetFreshName_short() {
    Lobby testLobby = new Lobby("a", 1, "small");
    Assert.assertEquals("a100", testLobby.getFreshName(testLobby.getLobbyName()));
  }

  @Test
  public void checkGetFreshName_zeros() {
    Lobby testLobby = new Lobby("0000", 1, "small");
    Assert.assertEquals("1000", testLobby.getFreshName(testLobby.getLobbyName()));
  }
}
