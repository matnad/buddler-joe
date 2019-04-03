package game;

import java.io.Serializable;

import util.RandomName;


/**
 * Main class to save the user settings. Used in various classes and data can be accessed through
 * getters/setters
 */
public class Settings implements Serializable {

  /** Important user settings to be accessed by various methods. */
  private int width = 1280;

  private int height = 800;
  private boolean fullscreen = false;
  private String username = RandomName.getRandomName();

  // TODO: (Viktor) Ip

  public boolean isFullscreen() {
    return fullscreen;
  }

  public void setFullscreen(boolean fullscreen) {
    this.fullscreen = fullscreen;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
