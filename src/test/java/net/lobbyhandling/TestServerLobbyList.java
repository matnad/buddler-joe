package net.lobbyhandling;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServerLobbyList {

  public static final Logger logger = LoggerFactory.getLogger(TestServerLobbyList.class);

  @Test
  public void checkAddLobby() {
    ServerLobbyList lobbyList = new ServerLobbyList();
    ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
    Lobby testLobby1 = new Lobby("test1", 1, "mid");
    Lobby testLobby2 = new Lobby("test2", 2, "large");
    lobbies.put(testLobby1.getLobbyId(), testLobby1);
    lobbies.put(testLobby2.getLobbyId(), testLobby2);
    lobbyList.addLobby(testLobby1);
    lobbyList.addLobby(testLobby2);
    Assert.assertTrue(lobbies.equals(lobbyList.getLobbies()));
  }

  @Test
  public void checkAddLobbyDouble() {
    ServerLobbyList lobbyList = new ServerLobbyList();
    ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
    Lobby testLobby1 = new Lobby("test1", 1, "mid");
    lobbies.put(testLobby1.getLobbyId(), testLobby1);
    lobbyList.addLobby(testLobby1);
    lobbyList.addLobby(testLobby1);
    Assert.assertTrue(lobbies.equals(lobbyList.getLobbies()));
  }

  @Test
  public void checkNotAddLobby() {
    ServerLobbyList lobbyList = new ServerLobbyList();
    ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
    Lobby testLobby1 = new Lobby("test1", 1, "mid");
    Lobby testLobby2 = new Lobby("test2", 2, "large");
    lobbies.put(testLobby1.getLobbyId(), testLobby1);
    lobbyList.addLobby(testLobby1);
    lobbyList.addLobby(testLobby2);
    Assert.assertFalse(lobbies.equals(lobbyList.getLobbies()));
  }

  @Test
  public void checkAddLobbyDoubleName() {
    ServerLobbyList lobbyList = new ServerLobbyList();
    ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
    Lobby testLobby1 = new Lobby("test1", 1, "mid");
    Lobby testLobby2 = new Lobby("test1", 2, "large");
    lobbies.put(testLobby1.getLobbyId(), testLobby1);
    lobbyList.addLobby(testLobby1);
    lobbyList.addLobby(testLobby2);
    Assert.assertTrue(lobbies.equals(lobbyList.getLobbies()));
  }

  @Test
  public void checkRemoveLobbyExists() {
    ServerLobbyList lobbyList = new ServerLobbyList();
    ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
    Lobby testLobby1 = new Lobby("test1", 1, "mid");
    Lobby testLobby2 = new Lobby("test2", 2, "large");
    lobbies.put(testLobby2.getLobbyId(), testLobby2);
    lobbyList.addLobby(testLobby1);
    lobbyList.addLobby(testLobby2);
    lobbyList.removeLobby(testLobby1.getLobbyId());
    Assert.assertTrue(lobbies.equals(lobbyList.getLobbies()));
  }

  @Test
  public void checkRemoveLobbyNotExists() {
    ServerLobbyList lobbyList = new ServerLobbyList();
    ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
    Lobby testLobby1 = new Lobby("test1", 1, "mid");
    Lobby testLobby2 = new Lobby("test2", 2, "large");
    lobbies.put(testLobby1.getLobbyId(), testLobby1);
    lobbies.put(testLobby2.getLobbyId(), testLobby2);
    lobbyList.addLobby(testLobby1);
    lobbyList.addLobby(testLobby2);
    Lobby testLobby3 = new Lobby("test3", 2, "large");
    lobbyList.removeLobby(testLobby3.getLobbyId());
    Assert.assertTrue(lobbies.equals(lobbyList.getLobbies()));
  }

  @Test
  public void checkGetName() {
    ServerLobbyList lobbyList = new ServerLobbyList();
    Lobby testLobby1 = new Lobby("test1", 1, "mid");
    lobbyList.addLobby(testLobby1);
    Assert.assertTrue(testLobby1.getLobbyName().equals(lobbyList.getName(testLobby1.getLobbyId())));
  }

  @Test
  public void checkGetLobby() {
    ServerLobbyList lobbyList = new ServerLobbyList();
    Lobby testLobby1 = new Lobby("test1", 1, "mid");
    lobbyList.addLobby(testLobby1);
    Assert.assertTrue(testLobby1.equals(lobbyList.getLobby(testLobby1.getLobbyId())));
  }

  @Test
  public void checkGetLobbyId() {
    ServerLobbyList lobbyList = new ServerLobbyList();
    ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
    Lobby testLobby1 = new Lobby("test1", 1, "mid");
    lobbies.put(testLobby1.getLobbyId(), testLobby1);
    lobbyList.addLobby(testLobby1);
    Assert.assertTrue(testLobby1.getLobbyId() == lobbyList.getLobbyId(testLobby1.getLobbyName()));
  }

  @Test
  public void checkGetTopTen() {
    ServerLobbyList lobbyList = new ServerLobbyList();
    Lobby testLobby1 = new Lobby("test1", 1, "mid");
    lobbyList.addLobby(testLobby1);
    Lobby testLobby2 = new Lobby("test2", 2, "large");
    lobbyList.addLobby(testLobby2);
    Lobby testLobby3 = new Lobby("test3", 3, "large");
    lobbyList.addLobby(testLobby3);
    Lobby testLobby4 = new Lobby("test4", 4, "large");
    lobbyList.addLobby(testLobby4);
    Lobby testLobby5 = new Lobby("test5", 5, "large");
    lobbyList.addLobby(testLobby5);
    Assert.assertEquals(
        "5║test1║0║mid║test2║0║large║test3║0║large║test4║0║large║test5║0║large",
        lobbyList.getTopTen());
  }

  @Test
  public void checkGetLobbiesInGame() {
    ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
    Lobby testLobby1 = new Lobby("test1", 1, "mid");
    Lobby testLobby2 = new Lobby("test2", 1, "mid");
    testLobby1.setInGame(true);
    lobbies.put(testLobby1.getLobbyId(), testLobby1);
    lobbies.put(testLobby2.getLobbyId(), testLobby2);
    ServerLobbyList lobbyList = new ServerLobbyList();
    lobbyList.addLobby(testLobby1);
    StringBuilder s = new StringBuilder();
    s.append(testLobby1.toString()).append("║");
    Assert.assertTrue(s.toString().equals(lobbyList.getLobbiesInGame()));
  }

}
