package game;

/**
 * Represents one Entry in the table, that gets presented to the client, in the ChooseLobby Menu.
 *
 * @author Sebastian Schlachter
 */
public class LobbyEntry {

  private String name;
  private int players;

  public LobbyEntry(String name, String n) {
    this.name = name;
    this.players = Integer.parseInt(n);
  }

  public int getPlayers() {
    return players;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return name;
  }
}
