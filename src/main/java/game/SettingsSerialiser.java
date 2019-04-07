package game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import net.StartServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to serialise the settings of the player to save them for another purpose. Contains the main
 * serialising methods.
 */
public class SettingsSerialiser {

  public static final Logger logger = LoggerFactory.getLogger(SettingsSerialiser.class);

  public SettingsSerialiser() {}

  /**
   * Method to serialise the settings and to save them at the chosen path.
   *
   * @param settings settings instance to serialize
   */
  public void serialiseSettings(Settings settings) {
    try {
      FileOutputStream fileOut = new FileOutputStream("src/main/resources/config/Settings.ser");
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
      FileInputStream fileIn = new FileInputStream("src/main/resources/config/Settings.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      Settings settings = (Settings) in.readObject();
      in.close();
      fileIn.close();
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
