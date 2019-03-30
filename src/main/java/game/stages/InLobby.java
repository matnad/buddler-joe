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

public class InLobby {
    private static final float FADE_TIME = .5f;
    private static float fadeTimer;
    private static float currentAlpha;

    private static GuiTexture background;
    private  static GuiTexture inLobby;


    private static MenuButton leave;
    private static MenuButton send;
    private static MenuButton ready;

    @SuppressWarnings("Duplicates")
    public static void init(Loader loader) {

        currentAlpha = 1;
        float rWidht = .035521f;
        float rHIght = .048333f;

        // Background
        background =
                new GuiTexture(loader.loadTexture("mainMenuBackground"), new Vector2f(0, 0), new Vector2f(1, 1), 1);

        inLobby =  new GuiTexture(loader.loadTexture("options_placeholder"), new Vector2f(0, 0), new Vector2f(0.5f, 0.5f), 1);


        leave =
                new MenuButton(
                        loader, "leave_placeholder", "leave_placeholder", new Vector2f(0, 0.1f), new Vector2f(rWidht, rHIght));

        send =
                new MenuButton(
                        loader, "send_placeholder", "send_placeholder", new Vector2f(0, 0), new Vector2f(rWidht, rHIght));

        ready =
                new MenuButton(
                        loader, "ready_placeholder", "ready_placeholder", new Vector2f(0, -0.1f), new Vector2f(rWidht, rHIght));



    }

    @SuppressWarnings("Duplicates")
    public static void update() {
        List<GuiTexture> guis = new ArrayList<>();
        //add textures here
        guis.add(background);
        guis.add(inLobby);

        // OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
        double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
        double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

        //add buttons here
        guis.add(leave.getHoverTexture(x,y));
        guis.add(send.getHoverTexture(x,y));
        guis.add(ready.getHoverTexture(x,y));


        if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && leave.isHover(x, y)) {
            //TODO trigger Lobbyleave
        }else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && send.isHover(x, y)) {
            //TODO trigger chat send message
        }else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && ready.isHover(x, y)) {
            //TODO trigger Playerready
        }

        InputHandler.update();

        Game.window.update();

        Game.getGuiRenderer().render(guis);
    }
}
