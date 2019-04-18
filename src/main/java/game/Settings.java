package game;

import engine.io.Window;
import java.io.Serializable;

/**
 * Main class to save the user settings. Used in various classes and data can be accessed through
 * getters/setters
 */
public class Settings implements Serializable {

  private SettingsSerialiser settingsSerialiser = new SettingsSerialiser();

  /** Important user settings to be accessed by various methods. */
  private int width = 1920 / 4 * 3;

  private int height = 1080 / 4 * 3;
  private boolean fullscreen = false;
  private String username = "Joe Buddler";
  private String ip = "buddlerjoe.ch";

  public synchronized boolean isFullscreen() {
    return fullscreen;
  }

  public synchronized void setFullscreen(boolean fullscreen) {
    this.fullscreen = fullscreen;
    settingsSerialiser.serialiseSettings(this);
  }

  public synchronized int getWidth() {
    return width;
  }

  public synchronized void setWidth(int width) {
    this.width = width;
    settingsSerialiser.serialiseSettings(this);
  }

  public synchronized int getHeight() {
    return height;
  }

  public synchronized void setHeight(int height) {
    this.height = height;
    settingsSerialiser.serialiseSettings(this);
  }

  public synchronized String getUsername() {
    return username;
  }

  public synchronized void setUsername(String username) {
    this.username = username;
    settingsSerialiser.serialiseSettings(this);
  }

  public synchronized String getIp() {
    return ip;
  }

  public synchronized void setIp(String ip) {
    this.ip = ip;
    settingsSerialiser.serialiseSettings(this);
  }
}
