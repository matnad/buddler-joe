package net.highscore;

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
public class ServerHighscoreSerialiser {

  public static final Logger logger = LoggerFactory.getLogger(ServerHighscoreSerialiser.class);

  private static final String HIGHSCORE_FILEPATH = "src/main/resources/config/ServerHighscore.ser";

  public ServerHighscoreSerialiser() {}

  /**
   * Method to serialise the settings and to save them at the chosen path.
   *
   * @param highscore highscore object to serialise
   */
  public static void serialiseServerHighscore(ServerHighscore highscore) {
    try {
      FileOutputStream fileOut = new FileOutputStream(HIGHSCORE_FILEPATH);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(highscore);
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
  public static ServerHighscore readServerHighscore() {
    try {
      FileInputStream fileIn = new FileInputStream(HIGHSCORE_FILEPATH);
      ObjectInputStream in = new ObjectInputStream(fileIn);
      ServerHighscore highscore = (ServerHighscore) in.readObject();
      in.close();
      fileIn.close();
      return highscore;
    } catch (IOException i) {
      logger.warn("Server Highscore File not found. Creating a new Highscore file.");
      return new ServerHighscore();
    } catch (ClassNotFoundException c) {
      logger.error("Server Highscore Class not found.");
      c.printStackTrace();
      return null;
    }
  }
}
