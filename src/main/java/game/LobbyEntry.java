package game;

/**
 * Represents one Entry in the table, that gets presented to the client, in the ChooseLobby Menu.
 *
 * @author Sebastian Schlachter
 */
public class LobbyEntry {

  private String name;
  private int players;
  private String size;

  public LobbyEntry(String name, String n, String size) {
    this.name = name;
    this.players = Integer.parseInt(n);
    this.size = size;
  }

  public int getPlayers() {
    return players;
  }

  public String getSize() {
    return size;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return name;
  }
}
