package game;

public class HighscoreEntry {
    private String username;
    private long time;

    public HighscoreEntry(String username, long time) {
        this.username = username;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }
}
