package game;

import java.io.Serializable;

/**
 * Main class to save the user settings. Used in various classes and data can be accessed through getters/setters
 */

public class Settings implements Serializable {

    /**
     * Important user settings to be accessed by various methods.
     */

    private int WIDTH = 1280;
    private int HEIGHT = 800;
    private boolean fullscreen = false;

    //TODO: (Viktor) Spielername und Ip

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public void setWIDTH(int WIDTH) {
        this.WIDTH = WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public void setHEIGHT(int HEIGHT) {
        this.HEIGHT = HEIGHT;
    }

}
