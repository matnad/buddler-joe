package game.stages;

import engine.io.InputHandler;
import engine.render.Loader;
import game.Game;
import gui.GuiTexture;
import gui.MenuButton;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class GameMenu {

    private static GuiTexture gameMenu;

    private static MenuButton exitGame;


    private static List<GuiTexture> guis;


    public static void init(Loader loader) {


        //Main Menu
        gameMenu = new GuiTexture(loader.loadTexture("gameMenu"), new Vector2f(0, 0), new Vector2f(.5f, .5f/1.5f), 1);

        //Exit Game
        exitGame = new MenuButton(loader, "exitGame1", "exitGame2", new Vector2f(0, 0), new Vector2f(.4f, .4f/3));
    }


    public static void update() {
        guis = new ArrayList<>();
        guis.add(gameMenu);

        //OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
        double x = 2*(InputHandler.getMouseX()/Game.window.getWidth())-1;
        double y = 1-2*(InputHandler.getMouseY()/Game.window.getHeight());

        if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Game.addActiveStage(Game.Stage.PLAYING);
            Game.removeActiveStage(Game.Stage.GAMEMENU);
        } else if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && exitGame.isHover(x, y)) {
            Game.addActiveStage(Game.Stage.MAINMENU);
            Game.removeActiveStage(Game.Stage.PLAYING);
            Game.removeActiveStage(Game.Stage.GAMEMENU);
        }

        InputHandler.update();

        guis.add(exitGame.getHoverTexture(x,y));

        Game.window.update();

        Game.getGuiRenderer().render(guis);
    }
}
