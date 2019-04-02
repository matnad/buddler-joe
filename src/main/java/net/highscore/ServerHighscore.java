package net.highscore;

import java.io.Serializable;
import java.util.HashMap;

public class ServerHighscore implements Serializable {

  private HashMap<String, Float> highscore;

  public ServerHighscore() {
    highscore = new HashMap<String, Float>();
  }

  /**
   * Method to add a player to the Highscore. Is used each time after a game.
   *
   * @param username Name of the first finishing player.
   * @param time time the user had for the game
   */
  public void addPlayer(String username, float time) {
    // for(String s : HashMa){

    // }
  }

  public HashMap<String, Float> getHighscore() {
    return highscore;
  }

  public void setHighscore(HashMap<String, Float> highscore) {
    this.highscore = highscore;
  }
}
