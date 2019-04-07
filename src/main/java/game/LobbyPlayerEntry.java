package game;

/**
 * Represents one Entry in the table, that gets presented to the client, in the InLobby Menu.
 *
 * @author Sebastian Schlachter
 */
public class LobbyPlayerEntry {

  private String name;
  private boolean ready;

  public LobbyPlayerEntry(String name, boolean ready) {
    this.name = name;
    this.ready = ready;
  }

  public boolean isReady() {
    return ready;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return name;
  }
}
