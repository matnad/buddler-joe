package game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class to serialise the settings of the player to save them for another purpose. Contains the main
 * serialising methods.
 */
public class SettingsSerialiser {

  public SettingsSerialiser() {}

  /** Method to serialise the settings and to save them at the chosen path. */
  public void serialiseSettings(Settings settings) {
    try {
      FileOutputStream fileOut = new FileOutputStream("/src/main/java/game/Settings.ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(settings);
      out.close();
      fileOut.close();
      return;
    } catch (IOException i) {
      i.printStackTrace();
    }
  }

  /**
   * Method to deserialise the settings to make them available and to read them again.
   *
   * @return The settings, which have been serialised previously.
   */
  public Settings readSettings() {
    try {
      FileInputStream fileIn = new FileInputStream("/config/Settings.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      Settings settings = (Settings) in.readObject();
      in.close();
      fileIn.close();
      return settings;
    } catch (IOException i) {
      i.printStackTrace();
      return null;
    } catch (ClassNotFoundException c) {
      System.out.println("Settings not found");
      c.printStackTrace();
      return null;
    }
  }
}
