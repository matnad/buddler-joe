package util;

import engine.io.InputHandler;
import entities.Entity;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import entities.items.Dynamite;

import static org.lwjgl.glfw.GLFW.*;

public class MousePlacer {

    private static Entity entity;


    public static void update() {

        if(!InputHandler.isPlacerMode())
            return;

        //entity.updateBoundingBox();

        entity.setPosition(InputHandler.getWallIntersection());
        //Placing the item
        if(!doesCollide() && InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1)) {
            InputHandler.setPlacerMode(false);
            if(entity instanceof Dynamite)
                ((Dynamite) entity).setActive(true);
            MousePlacer.entity = null;
        }
    }

    private static boolean doesCollide() {
        for (Block block : BlockMaster.getBlocks()) {
            if(block.collidesWith(entity)) {
                return true;
            }
        }
        return false;
    }


    public static Entity getEntity() {
        return entity;
    }

    public static void placeEntity(Entity entity) {
        //Set entity if valid and if the placer is currently not used
        if (entity == null || InputHandler.isPlacerMode()) {
            System.out.println("cant place");
            return;
        }
        MousePlacer.entity = entity;
        InputHandler.setPlacerMode(true);
    }
}
