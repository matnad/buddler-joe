package net.highscore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class to serialise the settings of the player to save them for another purpose. Contains the main
 * serialising methods.
 */
public class ServerHighscoreSerialiser {

    public ServerHighscoreSerialiser() {}

    /** Method to serialise the settings and to save them at the chosen path. */
    public void serialiseServerHighscore(ServerHighscore highscore) {
        try {
            FileOutputStream fileOut = new FileOutputStream("/src/main/java/net/highscore/ServerHighscore.ser");
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
    public ServerHighscore readServerHighscore() {
        try {
            FileInputStream fileIn = new FileInputStream("/src/main/java/net/highscore/ServerHighscore.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            ServerHighscore settings = (ServerHighscore) in.readObject();
            in.close();
            fileIn.close();
            return settings;
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Highscore not found");
            c.printStackTrace();
            return null;
        }
    }
}

