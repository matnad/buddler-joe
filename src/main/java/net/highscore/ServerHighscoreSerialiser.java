package net.highscore;

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
public class ServerHighscoreSerialiser {

  public static final Logger logger = LoggerFactory.getLogger(ServerHighscoreSerialiser.class);
  private static final String path =
      System.getProperty("user.home") + File.separator + ".buddlerjoe";
  private static final String filename = "highscore.ser";

  public ServerHighscoreSerialiser() {}

  /**
   * Method to serialise the settings and to save them at the chosen path.
   *
   * @param highscore highscore object to serialise
   */
  public static void serialiseServerHighscore(ServerHighscore highscore) {
    try {
      File buddlerDir = new File(path);
      if (buddlerDir.exists()) {
        //logger.info("Settings directory found.");
      } else if (buddlerDir.mkdirs()) {
        logger.info(buddlerDir + " was created");
      } else {
        logger.warn(buddlerDir + " was not created");
        return;
      }
      File buddlerFile = new File(path + File.separator + filename);
      FileOutputStream fileOut = new FileOutputStream(buddlerFile);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(highscore);
      out.close();
      fileOut.close();
    } catch (IOException i) {
      logger.error("Error saving Highscore file (IO exception).");
    }
  }

  /**
   * Method to deserialise the settings to make them available and to read them again.
   *
   * @return The settings, which have been serialised previously.
   */
  public static ServerHighscore readServerHighscore() {

    try {
      File buddlerFile = new File(path + File.separator + filename);
      FileInputStream fileIn = new FileInputStream(buddlerFile);
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
      return new ServerHighscore();
    }
  }
}
