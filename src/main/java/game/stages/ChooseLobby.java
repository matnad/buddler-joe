package game.stages;

import engine.io.InputHandler;
import engine.render.Loader;
import game.Game;
import gui.GuiTexture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class ChooseLobby {

    private static final float FADE_TIME = .5f;
    private static float fadeTimer;
    private static float currentAlpha;

    private static  GuiTexture background;
    private  static GuiTexture lobbyOverview;

    public static void init(Loader loader) {

        currentAlpha = 1;

        // Background
        background =
                new GuiTexture(loader.loadTexture("mainMenuBackground"), new Vector2f(0, 0), new Vector2f(1, 1), 1);

        lobbyOverview =  new GuiTexture(loader.loadTexture("lobbyOverview"), new Vector2f(0, 0), new Vector2f(0.5f, 0.5f), 1);

    }

    @SuppressWarnings("Duplicates")
    public static void update() {
        List<GuiTexture> guis = new ArrayList<>();
        //add textures here
        guis.add(background);
        guis.add(lobbyOverview);

        // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
        double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
        double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

        //add buttons here


        InputHandler.update();

        Game.window.update();

        Game.getGuiRenderer().render(guis);
    }
}
