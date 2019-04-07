package game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class to Manage a Overview of all current and past GameRounds.
 *
 * @author Sebastian Schlachter
 */
@SuppressWarnings("Duplicates")
public class History {
  private static CopyOnWriteArrayList<String> finished = new CopyOnWriteArrayList<>();
  private static ConcurrentHashMap<Integer, String> open = new ConcurrentHashMap<>();
  private static ConcurrentHashMap<Integer, String> running = new ConcurrentHashMap<>();

  /**
   * Returns a String that contains a Listing of all open and running lobbies and also all rounds
   * that are finished. The character "║" is set at the positions were a new Line should start. It
   * can later be Split at the positions of "║", after that every part can be directly printed as a
   * new Line.
   *
   * @return all lobbies, sorted by categories
   */
  public static String getStory() {
    String res = "Open Lobbies:║";
    for (Map.Entry<Integer, String> entry : open.entrySet()) {
      String value = entry.getValue();
      res = res + value + "║";
    }
    if (open.size() == 0) {
      res = res + "none║";
    }
    res = res + "Lobbies Of Running Games:║";
    for (Map.Entry<Integer, String> entry : running.entrySet()) {
      String value = entry.getValue();
      res = res + value + "║";
    }
    if (running.size() == 0) {
      res = res + "none║";
    }
    res = res + "Old Games:║";
    for (int i = 0; i < finished.size(); i++) {
      res = res + finished.get(i) + "║";
    }
    if (finished.size() == 0) {
      res = res + "none║";
    }
    return res;
  }

  public static void openRemove(int lobbyId) {
    open.remove(lobbyId);
  }

  public static void openAdd(int lobbyId, String lobbyName) {
    open.put(lobbyId, lobbyName);
  }

  public static void runningRemove(int lobbyId) {
    running.remove(lobbyId);
  }

  public static void runningAdd(int lobbyId, String lobbyName) {
    running.put(lobbyId, lobbyName);
  }

  public static void archive(String data) {
    finished.add(data);
  }
}
