package net.highscore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringJoiner;

public class ServerHighscore implements Serializable {

  private ArrayList<Standing> highscore;

  /** Contructor to be called to create a new Highscore if none has been created before. */
  public ServerHighscore() {
    this.highscore = new ArrayList<>();
  }

  /**
   * Gets called after each game to check, whether the player is better than any player in the list.
   * If the new winner was faster than all the others, he gets saved in the highscore, else he gets
   * not added.
   *
   * @param time Finishing time of the player until win.
   * @param username Username of the winner.
   */
  public void addPlayer(long time, String username) {
    if (highscore.size() == 0) {
      highscore.add(new Standing(time, username));
    } else {
      int i;
      for (i = 0; i < Math.min(11, highscore.size()); i++) {
        if (highscore.get(i).time > time) {
          highscore.add(i, new Standing(time, username));
          return;
        }
      }
      // Not faster than any other time, but room on the highscore list
      if (i < 10 && highscore.get(highscore.size() - 1).time < time) {
        highscore.add(new Standing(time, username));
      }
    }
  }

  /**
   * Return the Highscore as a String to be sent to a client.
   *
   * @return The Highscore as a String
   */
  public String getHighscoreAsString() {

    if (highscore.size() == 0) {
      return "There is no Highscore yet.";
    }
    // return String.join("║", highscore.toString());

    StringJoiner sj = new StringJoiner("║");
    for (int i = 0; i < Math.min(10, highscore.size()); i++) {
      sj.add(highscore.get(i).toString());
    }
    return sj.toString();
  }

  @Override
  public String toString() {
    return getHighscoreAsString();
  }

  /**
   * Global Highscore kept by the Server to save the best results of all players. Implements
   * serializable to be able to save it and open it next time the server will be started.
   */
  private static class Standing implements Serializable {

    /**
     * Private class to save a standing in the ArrayList.
     *
     * <p>Consists of a time variable which is the new best time in the top ten and the username of
     * the player.
     */
    long time;

    String username;

    public Standing(long time, String username) {
      this.time = time;
      this.username = username;
    }

    @Override
    public String toString() {
      return username + "║" + util.Util.milisToString(time);
    }
  }
}
