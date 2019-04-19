package gui.lifeStatus;

import engine.render.Loader;
import gui.GuiTexture;
import org.joml.Vector2f;


public class LifeStatus {


    private GuiTexture hearts;


    /**
     *
     *
     * @param loader main loader
     */
    public LifeStatus(Loader loader) {

        // Loads the heart to screen and set rendering parameters
        hearts = new GuiTexture(
                        loader.loadTexture("heart"),
                        new Vector2f(-.95f, .88f),
                        new Vector2f(.04f, .07f), 1);

    }

    /**
     * <p>Called every frame. Reads chat input and toggles chat window text input handler
     */
    public void checkInputs() {

    }

    public GuiTexture getLifeStatusGui() {
        return hearts;
    }
}

