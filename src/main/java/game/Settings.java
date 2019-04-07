package game;

import engine.io.Window;
import java.io.Serializable;

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
  private String ip = "127.0.0.1";

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

  public Window getWindow() {
    return new Window(width, height, 60, "Buddler Joe");
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }
}
