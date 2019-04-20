package game;

public class HighscoreEntry {
  private String username;
  private String time;

  public HighscoreEntry(String username, String time) {
    this.username = username;
    this.time = time;
  }

  public String getTime() {
    return time;
  }

  public String getUsername() {
    return username;
  }
}
