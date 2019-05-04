package game;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Assert;
import org.junit.Test;

public class TestHistory {

  @Test
  public void checkAddOpenGame() {
    History history = new History();
    ConcurrentHashMap<Integer, String> open = new ConcurrentHashMap<>();
    History.openAdd(1, "TestLobby");
    open.put(1, "TestLobby");
    Assert.assertEquals(open, History.getOpen());
  }

  @Test
  public void checkAddRunningGame() {
    History history = new History();
    ConcurrentHashMap<Integer, String> open = new ConcurrentHashMap<>();
    History.runningAdd(1, "TestLobby");
    open.put(1, "TestLobby");
    Assert.assertEquals(open, History.getRunning());
  }

  @Test
  public void checkArchiveGame() {
    History history = new History();
    CopyOnWriteArrayList<String> finished = new CopyOnWriteArrayList<>();
    History.archive("TestLobby");
    finished.add("TestLobby");
    Assert.assertEquals(finished, History.getFinished());
  }

  @Test
  public void checkRemoveOpenGame() {
    History history = new History();
    ConcurrentHashMap<Integer, String> open = new ConcurrentHashMap<>();
    History.openAdd(1, "TestLobby");
    History.openRemove(1);
    Assert.assertEquals(open, History.getOpen());
  }

  @Test
  public void checkRemoveRunningGame() {
    History history = new History();
    ConcurrentHashMap<Integer, String> open = new ConcurrentHashMap<>();
    History.runningAdd(1, "TestLobby");
    History.runningRemove(1);
    Assert.assertEquals(open, History.getRunning());
  }

  @Test
  public void checkGetComplicatedHistory() {
    History history = new History();
    History.clearArchive();
    History.runningAdd(1, "TestLobby");
    History.runningAdd(2, "TestLobby2");
    History.runningAdd(3, "TestLobby3");
    History.runningRemove(1);
    History.openAdd(1, "TestLobby1");
    History.openAdd(2, "TestLobby2");
    History.openAdd(3, "TestLobby3");
    History.openAdd(4, "TestLobby4");
    History.openRemove(3);
    History.openRemove(1);
    History.archive("TestLobby6");
    History.archive("TestLobby3");
    History.archive("TestLobby2");

    Assert.assertEquals(
        "Open Lobbies:║TestLobby2║TestLobby4║"
            + "Lobbies Of Running Games:║TestLobby2║TestLobby3║Old Games:"
            + "║TestLobby6║TestLobby3║TestLobby2║",
        History.getStory());
  }
}
