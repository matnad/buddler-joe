package util;

import bin.Game;
import engine.io.InputHandler;
import entities.Entity;
import entities.blocks.Block;
import entities.items.Dynamite;

import static org.lwjgl.glfw.GLFW.*;

public class MousePlacer {

    private static Entity entity;


    public static void move() {

        if(!InputHandler.isPickerMode())
            return;

        //entity.updateBoundingBox();

        entity.setPosition(InputHandler.getWallIntersection());
        //Placing the item
        if(!doesCollide() && InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1)) {
            InputHandler.setPickerMode(false);
            if(entity instanceof Dynamite)
                ((Dynamite) entity).setActive(true);
        }
    }

    private static boolean doesCollide() {
        for (Block block : Game.getBlocks()) {
            if(block.collidesWith(entity)) {
                return true;
            }
        }
        return false;
    }


    public static Entity getEntity() {
        return entity;
    }

    public static void setEntity(Entity entity) {
        MousePlacer.entity = entity;
        InputHandler.setPickerMode(true);
    }
}
