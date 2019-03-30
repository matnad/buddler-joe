package game.stages;

import engine.io.InputHandler;
import engine.render.Loader;
import game.Game;
import gui.GuiTexture;
import gui.MenuButton;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class Credits {

    private static final float FADE_TIME = .5f;
    private static float fadeTimer;
    private static float currentAlpha;

    private static  GuiTexture background;
    private  static GuiTexture Credits;

    private static MenuButton back;

    public static void init(Loader loader) {

        currentAlpha = 1;

        // Background
        background =
                new GuiTexture(loader.loadTexture("mainMenuBackground"), new Vector2f(0, 0), new Vector2f(1, 1), 1);

        Credits =  new GuiTexture(loader.loadTexture("credits_placeholder"), new Vector2f(0, 0), new Vector2f(0.5f, 0.5f), 1);

        // Back
        back =
                new MenuButton(
                        loader, "back_placeholder", "back_placeholder", new Vector2f(0.75f, -0.75f), new Vector2f(.105521f, .128333f));


    }

    @SuppressWarnings("Duplicates")
    public static void update() {
        List<GuiTexture> guis = new ArrayList<>();
        //add textures here
        guis.add(background);
        guis.add(Credits);

        // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
        double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
        double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

        //add buttons here
        guis.add(back.getHoverTexture(x,y));

        if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
            Game.addActiveStage(Game.Stage.MAINMENU);
            Game.removeActiveStage(Game.Stage.CREDITS);
        }


        InputHandler.update();

        Game.window.update();

        Game.getGuiRenderer().render(guis);
    }
}
