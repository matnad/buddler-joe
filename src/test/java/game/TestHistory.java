package game;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestHistory {

  @Test
  public void checkAddOpenGame() {
    History history = new History();
    ConcurrentHashMap<Integer, String> open = new ConcurrentHashMap<>();
    history.openAdd(1, "TestLobby");
    open.put(1, "TestLobby");
    Assert.assertEquals(open, history.getOpen());
  }

  @Test
  public void checkAddRunningGame() {
    History history = new History();
    ConcurrentHashMap<Integer, String> open = new ConcurrentHashMap<>();
    history.runningAdd(1, "TestLobby");
    open.put(1, "TestLobby");
    Assert.assertEquals(open, history.getRunning());
  }

  @Test
  public void checkArchiveGame() {
    History history = new History();
    CopyOnWriteArrayList<String> finished = new CopyOnWriteArrayList<>();
    history.archive("TestLobby");
    finished.add("TestLobby");
    Assert.assertEquals(finished, history.getFinished());
  }

  @Test
  public void checkRemoveOpenGame() {
    History history = new History();
    ConcurrentHashMap<Integer, String> open = new ConcurrentHashMap<>();
    history.openAdd(1, "TestLobby");
    history.openRemove(1);
    Assert.assertEquals(open, history.getOpen());
  }

  @Test
  public void checkRemoveRunningGame() {
    History history = new History();
    ConcurrentHashMap<Integer, String> open = new ConcurrentHashMap<>();
    history.runningAdd(1, "TestLobby");
    history.runningRemove(1);
    Assert.assertEquals(open, history.getRunning());
  }

  @Test
  public void checkGetComplicatedHistory() {
    History history = new History();
    history.clearArchive();
    history.runningAdd(1, "TestLobby");
    history.runningAdd(2, "TestLobby2");
    history.runningAdd(3, "TestLobby3");
    history.runningRemove(1);
    history.openAdd(1, "TestLobby1");
    history.openAdd(2, "TestLobby2");
    history.openAdd(3, "TestLobby3");
    history.openAdd(4, "TestLobby4");
    history.openRemove(3);
    history.openRemove(1);
    history.archive("TestLobby6");
    history.archive("TestLobby3");
    history.archive("TestLobby2");

    Assert.assertEquals(
        "Open Lobbies:║TestLobby2║TestLobby4║Lobbies Of Running Games:║TestLobby2║TestLobby3║Old Games:║TestLobby6║TestLobby3║TestLobby2║",
        history.getStory());
  }
}
