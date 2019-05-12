package game;

import gui.tutorial.Tutorial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Main class to save the user settings. Used in various classes and data can be accessed through
 * getters/setters
 */
public class Settings implements Serializable {

  /** Important user settings to be accessed by various methods. */
  private int width = 1920 / 4 * 3;

  private int height = 1080 / 4 * 3;
  private boolean fullscreen = false;
  private String username = "Joe Buddler";
  private String ip = "game.buddlerjoe.ch";

  private ArrayList<Tutorial.Topics> completedTutorials = new ArrayList<>();

  /**
   * Adds a tutorial topic to the list of completed topics so it will not come up again.
   *
   * @param topic topic to mark as completed
   */
  public synchronized void addCompletedTutorial(Tutorial.Topics topic) {
    if (!completedTutorials.contains(topic)) {
      completedTutorials.add(topic);
      serialise();
    }
  }

  public synchronized ArrayList<Tutorial.Topics> getCompletedTutorials() {
    return completedTutorials;
  }

  /** Restart the turorial. */
  public synchronized void resetTutorial() {
    completedTutorials.clear();
    serialise();
  }

  public synchronized boolean isFullscreen() {
    return fullscreen;
  }

  public synchronized void setFullscreen(boolean fullscreen) {
    this.fullscreen = fullscreen;
    serialise();
  }

  public synchronized int getWidth() {
    return width;
  }

  public synchronized void setWidth(int width) {
    this.width = width;
    serialise();
  }

  public synchronized int getHeight() {
    return height;
  }

  public synchronized void setHeight(int height) {
    this.height = height;
    serialise();
  }

  public synchronized String getUsername() {
    return username;
  }

  public synchronized void setUsername(String username) {
    this.username = username;
    serialise();
  }

  public synchronized String getIp() {
    return ip;
  }

  public synchronized void setIp(String ip) {
    this.ip = ip;
    serialise();
  }

  private void serialise() {
    // TODO: Serialiser to static? No real reason to call it from game.Game
    Game.getSettingsSerialiser().serialiseSettings(this);
  }
}
