package util;

import engine.io.InputHandler;
import entities.Entity;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import entities.items.Dynamite;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

/**
 * Uses Ray Casting in {@link InputHandler} to place an entity with the mouse cursor.
 * Checks if the item collides with a block to make sure the item is placed in an empty space.
 *
 */
public class MousePlacer {

    private static Entity entity;


    /**
     * Run every frame while placer mode is on.
     * Updates position of the entity to be placed and detects the intent to place an item.
     * If successful, it places the item and disables placer mode.
     */
    public static void update() {

        if(!InputHandler.isPlacerMode())
            return;

        entity.setPosition(InputHandler.getWallIntersection());
        //Placing the item
        if(!doesCollide() && InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1)) {
            InputHandler.setPlacerMode(false);
            if(entity instanceof Dynamite)
                ((Dynamite) entity).setActive(true);
            MousePlacer.entity = null;
        }
    }

    /**
     * Check if the entity collides with a block (other entities might be added here or in another method in
     * the future)
     *
     * Returns true if the entity is not in an empty space
     */
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

    /**
     * This is how you activate the mouse placer. Pass en entity to this method.
     *
     * @param entity entity to place
     */
    public static void placeEntity(Entity entity) {
        //Set entity if valid and if the placer is currently not used
        if (entity == null || InputHandler.isPlacerMode()) {
            //Can't place nothing or placer is already active
            return;
        }
        MousePlacer.entity = entity;
        InputHandler.setPlacerMode(true);
    }
}
