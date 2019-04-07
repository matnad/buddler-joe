package net.highscore;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerHighscore implements Serializable {

  /**
   * Global Highscore kept by the Server to save the best results of all players. Implements
   * serializable to be able to save it and open it next time the server will be started.
   */
  private static class Standing {

    /**
     * Private class to save a standing in the ArrayList.
     *
     * <p>Consists of a time variable which is the new best time in the top ten and the username of
     * the player.
     */
    float time;

    String username;

    public Standing(float time, String username) {
      this.time = time;
      this.username = username;
    }

    @Override
    public String toString() {
      return username + " : " + time;
    }
  }

  private static ArrayList<Standing> highscore;

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
  public static void addPlayer(float time, String username) {
    if (highscore.size() == 0) {
      highscore.add(new Standing(time, username));
    } else {
      for (int i = 0; i < 10; i++) {
        if (highscore.get(i).time < time) {
          highscore.add(i, new Standing(time, username));
        }
      }
    }
  }

  /**
   * Return the Highscore as a String to be sent to a client.
   *
   * @return The Highscore as a String
   */
  public static String getHighscoreAsString() {
    if (highscore.size() == 0) {
      return "There is no Highscore yet.";
    }
    return String.join("║", highscore.toString());
  }
}
