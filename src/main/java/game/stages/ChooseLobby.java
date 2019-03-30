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

public class ChooseLobby {

    private static final float FADE_TIME = .5f;
    private static float fadeTimer;
    private static float currentAlpha;

    private static  GuiTexture background;
    private  static GuiTexture lobbyOverview;
    private static GuiTexture buddlerJoe;

    private static MenuButton back;
    private static MenuButton join;


    public static void init(Loader loader) {

        currentAlpha = 1;

        // Background
        background =
                new GuiTexture(loader.loadTexture("mainMenuBackground"), new Vector2f(0, 0), new Vector2f(1, 1), 1);

        buddlerJoe =
                new GuiTexture(loader.loadTexture("buddlerjoe"), new Vector2f(-0.730208f, -0.32963f), new Vector2f(0.181771f, 0.67963f), 1);

        lobbyOverview =  new GuiTexture(loader.loadTexture("lobbyOverview"), new Vector2f(0, -0.040741f), new Vector2f(0.554167f, 0.757804f), 1);

        // Back
        back =
                new MenuButton(
                        loader, "back_placeholder", "back_placeholder", new Vector2f(0.75f, -0.75f), new Vector2f(.105521f, .128333f));

        // Join
        join =
                new MenuButton(
                        loader, "join_placeholder", "join_placeholder", new Vector2f(0.3f, -0.3f), new Vector2f(.105521f, .128333f));

    }

    @SuppressWarnings("Duplicates")
    public static void update() {
        List<GuiTexture> guis = new ArrayList<>();
        //add textures here
        guis.add(background);
        guis.add(lobbyOverview);
        guis.add(buddlerJoe);

        // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
        double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
        double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

        //add buttons here
        guis.add(back.getHoverTexture(x,y));
        guis.add(join.getHoverTexture(x,y));

        if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && back.isHover(x, y)) {
            Game.addActiveStage(Game.Stage.MAINMENU);
            Game.removeActiveStage(Game.Stage.CHOOSELOBBY);
        }else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && join.isHover(x, y)) {
            //TODO trigger joining
        }


        InputHandler.update();

        Game.window.update();

        Game.getGuiRenderer().render(guis);
    }
}
