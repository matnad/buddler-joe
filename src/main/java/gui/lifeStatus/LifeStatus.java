package gui.lifeStatus;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontmeshcreator.FontType;
import engine.render.fontrendering.TextMaster;
import game.Game;
import gui.GuiTexture;
import net.packets.chat.PacketChatMessageToServer;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;


public class LifeStatus {



    private GuiTexture hearts;


    /**
     * Initialize Chat, only needs to be called once on game init.
     *
     * @param loader main loader
     */
    public LifeStatus(Loader loader) {

        // Load the background image of the chat and set rendering parameters
        hearts = new GuiTexture(
                        loader.loadTexture("heart"),
                        new Vector2f(-.95f, 0.05f),
                        new Vector2f(.05f, .07f), 1);

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

