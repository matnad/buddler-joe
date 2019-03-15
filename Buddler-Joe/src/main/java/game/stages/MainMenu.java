package game.stages;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.MasterRenderer;
import game.Game;
import gui.GuiTexture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class MainMenu {

    private static GuiTexture mainMenu;

    private static GuiTexture joinGame1;
    private static GuiTexture joinGame2;
    private static GuiTexture joinGame;


    private static List<GuiTexture> guis;

    public static void init(Loader loader) {


        //Main Menu
        mainMenu = new GuiTexture(loader.loadTexture("mainMenu"), new Vector2f(0, 0), new Vector2f(1, 1), 1);


        //Join Game
        joinGame1 = new GuiTexture(loader.loadTexture("joinGame1"), new Vector2f(0, 0), new Vector2f(.2f, .2f), 1);
        joinGame2 = new GuiTexture(loader.loadTexture("joinGame2"), new Vector2f(0, 0), new Vector2f(.2f, .2f), 1);
        joinGame = joinGame1;
    }


    public static void update() {
        guis = new ArrayList<>();
        guis.add(mainMenu);
        //ESC = Exit... we will add a menu later
        if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Game.window.stop();
        }

        InputHandler.update();

        double x = InputHandler.getMouseX()/Game.window.getWidth();
        double y = InputHandler.getMouseY()/Game.window.getHeight();

        if(x > .3f && x < .7f) {
            guis.add(joinGame2);
        } else {
            guis.add(joinGame1);
        }

//        for (GuiTexture gui : guis) {
//            System.out.println(gui);
//        }

        Game.window.update();

        Game.getGuiRenderer().render(guis);

    }
}
