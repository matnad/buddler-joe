package net.playerhandling;

import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServerPlayerList {

  public static final Logger logger = LoggerFactory.getLogger(TestServerPlayerList.class);

  @Test
  public void checkAddPlayerNewPlayer() {
    ConcurrentHashMap<Integer, ServerPlayer> testPlayerList = new ConcurrentHashMap<>();
    ServerPlayerList playerList = new ServerPlayerList();
    ServerPlayer testPlayer = new ServerPlayer("Testuser", 1);
    testPlayerList.put(testPlayer.getClientId(), testPlayer);
    playerList.addPlayer(testPlayer);
    Assert.assertEquals(testPlayerList, playerList.getPlayers());
  }

  @Test
  public void checkAddPlayerDouble() {
    ConcurrentHashMap<Integer, ServerPlayer> testPlayerList = new ConcurrentHashMap<>();
    ServerPlayerList playerList = new ServerPlayerList();
    ServerPlayer testPlayer = new ServerPlayer("Testuser", 1);
    testPlayerList.put(testPlayer.getClientId(), testPlayer);
    playerList.addPlayer(testPlayer);
    playerList.addPlayer(testPlayer);
    Assert.assertEquals(testPlayerList, playerList.getPlayers());
  }

  @Test
  public void checkAddPlayerSameUsername() {
    ServerPlayerList playerList = new ServerPlayerList();
    ServerPlayer testPlayer1 = new ServerPlayer("Testuser", 1);
    ServerPlayer testPlayer2 = new ServerPlayer("Testuser", 2);
    playerList.addPlayer(testPlayer1);
    playerList.addPlayer(testPlayer2);
    Assert.assertEquals("Testuser_1", playerList.getUsername(testPlayer2.getClientId()));
  }

  @Test
  public void checkAddPlayerSameUsernameTwice() {
    ServerPlayerList playerList = new ServerPlayerList();
    ServerPlayer testPlayer1 = new ServerPlayer("Testuser", 1);
    ServerPlayer testPlayer2 = new ServerPlayer("Testuser_1", 2);
    ServerPlayer testPlayer3 = new ServerPlayer("Testuser", 3);
    playerList.addPlayer(testPlayer1);
    playerList.addPlayer(testPlayer2);
    playerList.addPlayer(testPlayer3);
    Assert.assertEquals("Testuser_2", playerList.getUsername(testPlayer3.getClientId()));
  }

  @Test
  public void checkReturnCorrectUsername() {
    ServerPlayerList playerList = new ServerPlayerList();
    ServerPlayer testPlayer1 = new ServerPlayer("Testuser", 1);
    playerList.addPlayer(testPlayer1);
    Assert.assertEquals("Testuser", playerList.getUsername(testPlayer1.getClientId()));
  }

  @Test
  public void checkReturnCorrectPlayer() {
    ServerPlayerList playerList = new ServerPlayerList();
    ServerPlayer testPlayer1 = new ServerPlayer("Testuser", 1);
    playerList.addPlayer(testPlayer1);
    Assert.assertEquals(testPlayer1, playerList.getPlayer(testPlayer1.getClientId()));
  }

  @Test
  public void checkRemovePlayerInPlayerList() {
    ConcurrentHashMap<Integer, ServerPlayer> testPlayerList = new ConcurrentHashMap<>();
    ServerPlayerList playerList = new ServerPlayerList();
    ServerPlayer testPlayer = new ServerPlayer("Testuser", 1);
    playerList.addPlayer(testPlayer);
    playerList.removePlayer(testPlayer.getClientId());
    Assert.assertEquals(testPlayerList, playerList.getPlayers());
  }

  @Test
  public void checkRemovePlayerNotInPlayerList() {
    ConcurrentHashMap<Integer, ServerPlayer> testPlayerList = new ConcurrentHashMap<>();
    ServerPlayerList playerList = new ServerPlayerList();
    ServerPlayer testPlayer1 = new ServerPlayer("Testuser", 1);
    ServerPlayer testPlayer2 = new ServerPlayer("Testuser2", 2);
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
    ServerPlayer testPlayer = new ServerPlayer("Testuser", 1);
    playerList.addPlayer(testPlayer);
    Assert.assertTrue(playerList.isClientIdInList(testPlayer.getClientId()));
  }

  @Test
  public void checkReturnCorrectUsernameInList() {
    ServerPlayerList playerList = new ServerPlayerList();
    ServerPlayer testPlayer = new ServerPlayer("Testuser", 1);
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
    ServerPlayer testPlayer = new ServerPlayer("Testuser", 1);
    playerList.addPlayer(testPlayer);
    Assert.assertEquals(playerList.toString(), "OK║Testuser║");
  }

  @Test
  public void checkReturnCorrectStringNoUser() {
    ServerPlayerList playerList = new ServerPlayerList();
    Assert.assertEquals(playerList.toString(), "No Players online");
  }
}
