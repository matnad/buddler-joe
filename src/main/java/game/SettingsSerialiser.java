package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to serialise the settings of the player to save them for another purpose. Contains the main
 * serialising methods.
 */
public class SettingsSerialiser {

  public static final Logger logger = LoggerFactory.getLogger(SettingsSerialiser.class);
  private static final String path =
      System.getProperty("user.home") + File.separator + ".buddlerjoe";
  private static final String filename = "settings.ser";

  public SettingsSerialiser() {}

  /**
   * Method to serialise the settings and to save them at the chosen path.
   *
   * @param settings settings instance to serialize
   */
  public void serialiseSettings(Settings settings) {
    try {
      File buddlerSettingsDir = new File(path);
      if (buddlerSettingsDir.exists()) {
        //logger.info("Settings directory found.");
      } else if (buddlerSettingsDir.mkdirs()) {
        logger.info(buddlerSettingsDir + " was created");
      } else {
        logger.warn(buddlerSettingsDir + " was not created");
        return;
      }
      File buddlerSettingsFile = new File(path + File.separator + filename);
      FileOutputStream fileOut = new FileOutputStream(buddlerSettingsFile);
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
      File buddlerSettingsFile = new File(path + File.separator + filename);
      FileInputStream fileIn = new FileInputStream(buddlerSettingsFile);
      ObjectInputStream in = new ObjectInputStream(fileIn);
      Settings settings = (Settings) in.readObject();
      in.close();
      fileIn.close();
      if (settings == null) {
        settings = new Settings();
      }
      return settings;
    } catch (IOException i) {
      logger.warn("No settings file found. Stating with default settings.");
      return new Settings();
    } catch (ClassNotFoundException c) {
      logger.error("Settings Class not found.");
      return new Settings();
    }
  }
}
