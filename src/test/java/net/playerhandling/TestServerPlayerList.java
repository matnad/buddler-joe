package net.playerhandling;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class TestServerPlayerList {

    public static final Logger logger = LoggerFactory.getLogger(TestServerPlayerList.class);


    @Test
  public void checkAddPlayerNewPlayer() {
    ConcurrentHashMap<Integer, Player> testPlayerList = new ConcurrentHashMap<>();
    ServerPlayerList playerList = new ServerPlayerList();
    Player testPlayer = new Player("Testuser", 1);
    testPlayerList.put(testPlayer.getClientId(), testPlayer);
    playerList.addPlayer(testPlayer);
    Assert.assertEquals(testPlayerList, playerList.getPlayers());
  }

  @Test
  public void checkAddPlayerDouble() {
    ConcurrentHashMap<Integer, Player> testPlayerList = new ConcurrentHashMap<>();
    ServerPlayerList playerList = new ServerPlayerList();
    Player testPlayer = new Player("Testuser", 1);
    testPlayerList.put(testPlayer.getClientId(), testPlayer);
    playerList.addPlayer(testPlayer);
    playerList.addPlayer(testPlayer);
    Assert.assertEquals(testPlayerList, playerList.getPlayers());
  }

  @Test
  public void checkAddPlayerSameUsername() {
    ServerPlayerList playerList = new ServerPlayerList();
    Player testPlayer1 = new Player("Testuser", 1);
    Player testPlayer2 = new Player("Testuser", 2);
    playerList.addPlayer(testPlayer1);
    playerList.addPlayer(testPlayer2);
    Assert.assertEquals("Testuser_1", playerList.getUsername(testPlayer2.getClientId()));
  }

  @Test
  public void checkAddPlayerSameUsernameTwice() {
    ServerPlayerList playerList = new ServerPlayerList();
    Player testPlayer1 = new Player("Testuser", 1);
    Player testPlayer2 = new Player("Testuser_1", 2);
    Player testPlayer3 = new Player("Testuser", 3);
    playerList.addPlayer(testPlayer1);
    playerList.addPlayer(testPlayer2);
    playerList.addPlayer(testPlayer3);
    Assert.assertEquals("Testuser_2", playerList.getUsername(testPlayer3.getClientId()));
  }

  @Test
  public void checkReturnCorrectUsername() {
    ServerPlayerList playerList = new ServerPlayerList();
    Player testPlayer1 = new Player("Testuser", 1);
    playerList.addPlayer(testPlayer1);
    Assert.assertEquals("Testuser", playerList.getUsername(testPlayer1.getClientId()));
  }

  @Test
  public void checkReturnCorrectPlayer() {
    ServerPlayerList playerList = new ServerPlayerList();
    Player testPlayer1 = new Player("Testuser", 1);
    playerList.addPlayer(testPlayer1);
    Assert.assertEquals(testPlayer1, playerList.getPlayer(testPlayer1.getClientId()));
  }

  @Test
  public void checkRemovePlayerInPlayerList() {
    ConcurrentHashMap<Integer, Player> testPlayerList = new ConcurrentHashMap<>();
    ServerPlayerList playerList = new ServerPlayerList();
    Player testPlayer = new Player("Testuser", 1);
    playerList.addPlayer(testPlayer);
    playerList.removePlayer(testPlayer.getClientId());
    Assert.assertEquals(testPlayerList, playerList.getPlayers());
  }

  @Test
  public void checkRemovePlayerNotInPlayerList() {
    ConcurrentHashMap<Integer, Player> testPlayerList = new ConcurrentHashMap<>();
    ServerPlayerList playerList = new ServerPlayerList();
    Player testPlayer1 = new Player("Testuser", 1);
    Player testPlayer2 = new Player("Testuser2", 2);
    testPlayerList.put(testPlayer1.getClientId(), testPlayer1);
    playerList.addPlayer(testPlayer1);
    playerList.removePlayer(testPlayer2.getClientId());
    Assert.assertEquals(testPlayerList, playerList.getPlayers());
  }

  @Test
  public void checkIsClientIdInListException() {
    ServerPlayerList playerList = new ServerPlayerList();
    Assert.assertFalse(playerList.isClientIdInList(3));
  }

  @Test
  public void checkReturnCorrectIntInList() {
    ServerPlayerList playerList = new ServerPlayerList();
    Player testPlayer = new Player("Testuser", 1);
    playerList.addPlayer(testPlayer);
    Assert.assertTrue(playerList.isClientIdInList(testPlayer.getClientId()));
  }

    @Test
    public void checkReturnCorrectUsernameInList() {
        ServerPlayerList playerList = new ServerPlayerList();
        Player testPlayer = new Player("Testuser", 1);
        playerList.addPlayer(testPlayer);
        Assert.assertTrue(playerList.isUsernameInList(testPlayer.getUsername()));
    }

    @Test
    public void checkIsUsernameNotInList() {
        ServerPlayerList playerList = new ServerPlayerList();
        Assert.assertFalse(playerList.isUsernameInList("TestUser"));
    }

    @Test
    public void checkReturnCorrectStringOneUser() {
        ServerPlayerList playerList = new ServerPlayerList();
        Player testPlayer = new Player("Testuser", 1);
        playerList.addPlayer(testPlayer);
        Assert.assertEquals(playerList.toString(), "OK║Testuser║");
  }

    @Test
    public void checkReturnCorrectStringNoUser() {
        ServerPlayerList playerList = new ServerPlayerList();
        Assert.assertEquals(playerList.toString(), "No Players online");
    }

}
