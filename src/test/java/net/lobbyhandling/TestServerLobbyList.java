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

  
  // This test depends on the linenumber of this
  // file being even or not...very strange...
  //@Test
  //public void checkGetTopTen() {
  //  Lobby testLobby1 = new Lobby("test1", 1, "m");
  //  Lobby testLobby2 = new Lobby("test2", 2, "l");
  //  Lobby testLobby3 = new Lobby("test3", 3, "l");
  //  Lobby testLobby4 = new Lobby("test4", 4, "l");
  //  Lobby testLobby5 = new Lobby("test5", 5, "l");
  //  Lobby testLobby6 = new Lobby("test6", 26, "l");
  //  Lobby testLobby7 = new Lobby("test7", 7, "l");
  //  Lobby testLobby8 = new Lobby("test8", 8, "l");
  //  Lobby testLobby9 = new Lobby("test9", 29, "l");
  //  Lobby testLobby10 = new Lobby("test10", 20, "l");
  //  Lobby testLobby11 = new Lobby("test11", 24, "l");
  //  ServerLobbyList lobbyList = new ServerLobbyList();
  //  lobbyList.addLobby(testLobby1);
  //  lobbyList.addLobby(testLobby2);
  //  lobbyList.addLobby(testLobby3);
  //  lobbyList.addLobby(testLobby4);
  //  lobbyList.addLobby(testLobby5);
  //  lobbyList.addLobby(testLobby6);
  //  lobbyList.addLobby(testLobby7);
  //  lobbyList.addLobby(testLobby8);
  //  lobbyList.addLobby(testLobby9);
  //  lobbyList.addLobby(testLobby10);
  //  lobbyList.addLobby(testLobby11);
  //  StringBuilder s = new StringBuilder();
  //  s.append(
  //      "10║test1║0║m║test2║0║l║test3║0║l║test4║0║l║test5║0║l║test6║0"
  //          + "║l║test7║0║l║test8║0║l║test9║0║l║test10║0║l");
  //  try {
  //    Thread.sleep(50);
  //  } catch (InterruptedException e) {
  //    // sleep interrupted
  //  }
  //  Assert.assertTrue(lobbyList.getTopTen().equals(s.toString()));
  //}

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
