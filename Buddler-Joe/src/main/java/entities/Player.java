package entities;

import game.Game;
import collision.BoundingBox;
import engine.io.InputHandler;
import engine.models.TexturedModel;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import entities.items.ItemMaster;
import org.joml.Vector3f;
import util.MousePlacer;

import java.util.ArrayList;
import java.util.List;

import static entities.items.ItemMaster.ItemTypes.DYNAMITE;
import static entities.items.ItemMaster.ItemTypes.TORCH;
import static game.Game.Stage.PLAYING;
import static org.lwjgl.glfw.GLFW.*;

/**
 * NOTE: This is derived from NetPlayer, but doesn't really use anything from NetPlayer.
 * We will redesign the Hierarchy of Players and NetPlayers soon as we develop the net package.
 * Look at this as derived from Entity directly.
 *
 * A lot in this class is not final but just for testing the game and developing new features.
 *
 * Here all the player controls are set. The player can:
 * - Move left, right, down (A, D, S)
 * - Jump (UP/SPACE)
 * - Place Dynamite (Q) (Temporary)
 * - Reset Position (T) (Temporary)
 *
 * Handles player collision with blocks (might move partly to a different class)
 *
 * Defines global gravity (this will move to a different class)
 *
 *
 */
public class Player extends NetPlayer {

    private static final float RUN_SPEED = 20; //Units per second
    private static final float TURN_SPEED = 720; //Degrees per second
    public static final float GRAVITY = -45; //Units per second
    private static final float JUMP_POWER = 25; //Units per second

    private static final float COLLISION_PUSH_OFFSET = 0.1f;

    private static float digDamage = 1; //Damage per second when colliding with blocks

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private List<Block> closeBlocks;

    private boolean isInAir = false; //Can't Jump while in the air


    /**
     * Spawn the Player. This will be handled differently in the future when we rework the Player class structure.
     */
    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale, null, 0, Game.getUsername(), Game.myModel, Game.myTexture, Game.myModelSize);
    }


    /**
     * Called every frame and does all the input reading, position updating, collision handling and potentially
     * server communication
     */
    public void move(){

        updateCloseBlocks(BlockMaster.getBlocks()); //We don't want to check collision for all blocks every frame

        if(Game.getActiveStages().size() == 1 && Game.getActiveStages().get(0) == PLAYING) {
            //Only check inputs if no other stage is active (stages are menu screens)
            checkInputs(); //See which relevant keys are pressed
            digDamage = 1;
        } else {
            currentSpeed = 0;
            digDamage = 0;
        }

        //Stop turning when facing directly left or right
        if (getRotY() <= -90 && currentTurnSpeed <0) {
            currentTurnSpeed = 0;
            setRotY(-90);
        } else if (getRotY() >= 90 && currentTurnSpeed >0) {
            currentTurnSpeed = 0;
            setRotY(90);
        }

        //Update position by distance travelled
        float distance = (float) (currentSpeed * Game.window.getFrameTimeSeconds());
        super.increasePosition(distance ,0,0);
        //Turn character by the turnSpeed (which is set to make a nice turning animation when changing direction)
        this.increaseRotation(0, (float) (currentTurnSpeed * Game.window.getFrameTimeSeconds()), 0);

        //Apply gravity to upwardspeed and change vertical position
        upwardsSpeed += GRAVITY * Game.window.getFrameTimeSeconds();
        super.increasePosition(0, (float) (upwardsSpeed * Game.window.getFrameTimeSeconds()), 0);

        //Handle collisions, we only check close blocks to optimize performance
        //Distance is much cheaper to check than overlap
        for (Block closeBlock : closeBlocks) {
            handleCollision(closeBlock);
        }

        //Send server update with update
        //TEMPORARY PROOF OF CONCEPT: THIS WILL GET REWORKED ONCE WE IMPLEMENT NET STUFF
        if(Game.isConnectedToServer() && (currentSpeed != 0 || upwardsSpeed != 0 || currentTurnSpeed != 0)) {
            //Packet01Move packet = new Packet01Move(Playing.getUsername(), this.getPosition(), this.getRotX(), this.getRotY(), this.getRotZ());
            //packet.writeData(Playing.getSocketClient());
        }
    }


    /**
     * Check if the player overlaps with a block. Determine from which direction the overlap is and handle it
     * appropriately. This is still very basic but it works. Can be improved if we have time.
     *
     * @param entity A block or other enity to check collision with. But usually a block.
     */
    private void handleCollision(Entity entity) {

        //Make this mess readable
        BoundingBox p = super.getbBox(); //PlayerBox
        BoundingBox e = entity.getbBox(); //EntityBox

        //Check if we collide with the block
        if(this.collidesWith(entity,2)) {

            //If we collide, we need to determine from which of the 4 cardinal directions
            float w = (p.getMinX() + p.getMaxX()) / 2 - (e.getMinX() + e.getMaxX()) / 2;
            float h = (p.getMinY() + p.getMaxY()) / 2 - (e.getMinY() + e.getMaxY()) / 2;

            if(Math.abs(w) < Math.abs(h)) { //vertical collision
                if (h > 0) { //from above
                    //setPositionY(e.getMaxY()); //This flickers the player on high resolution... bad!

                    //Undo the position change to keep the player in place
                    super.increasePosition(0, (float) -(upwardsSpeed * Game.window.getFrameTimeSeconds()), 0);
                    //Have a grace distance, if the overlap is too large, we reset to position to prevent hard clipping
                    if(getPosition().y+0.1 < e.getMaxY())
                        setPositionY(e.getMaxY());
                    //Reset jumping ability and downwards momentum
                    if (upwardsSpeed < 0)
                        upwardsSpeed = 0;
                    isInAir = false;
                    //If we hold S, dig down
                    if (InputHandler.isKeyDown(GLFW_KEY_S) && entity instanceof Block) {
                        digBlock((Block) entity);
                    }
                } else { // from below
                    //Reset Position to below the block, this doesnt flicker since we are falling
                     setPositionY(e.getMinY()-p.getDimY());
                     //Stop jumping up if we hit something above, will start accelerating down
                    if (upwardsSpeed > 0)
                        upwardsSpeed = 0;
                }
            } else { //horizontal collision
                if (w > 0) { //from right
                    //Have a small offset for smoother collision
                    setPositionX(e.getMaxX() + p.getDimX() / 2 + COLLISION_PUSH_OFFSET);
                    currentSpeed = 0; //Stop moving
                    isInAir = false; //Walljumps! Felt cute. Might delete later.
                } else { // from left
                    setPositionX(e.getMinX()-p.getDimX()/2 - COLLISION_PUSH_OFFSET);
                    currentSpeed = 0;
                    isInAir = false;
                }
                //Dig blocks whenever we collide horizontal
                if (entity instanceof Block) {
                    digBlock((Block) entity);
                }
            }
        }
    }

    /**
     * What happens PER FRAME when we dig a block.
     *
     * @param block block to dig
     */
    private void digBlock(Block block) {
        //Scale with frame time
        block.increaseDamage((float) (digDamage * Game.window.getFrameTimeSeconds()), this);
    }


    /**
     * VERY simple jump
     */
    private void jump() {
        if (!isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }


    /**
     * Maintain a list with blocks that are closer than the specified distance.
     * This is used to only check close block for collision or other interaction
     *
     * @param blocks Usually all blocks {@link BlockMaster#getBlocks()}
     * @param maxDistance Maximum distance for the block to be considered close
     */
    private void updateCloseBlocks(List<Block> blocks, float maxDistance) {
        List<Block> closeBlocks = new ArrayList<>();
        //Only 2D (XY) for performance
        for (Block block : blocks) {
            if(block.get2dDistanceFrom(super.getPositionXY()) <= block.getDim()+maxDistance) {
                closeBlocks.add(block);
            }
        }
        this.closeBlocks =  closeBlocks;
    }

    /**
     * Maintain a list with blocks that are closer than 5 units
     * A block is 6 units across, this will get all surrounding blocks
     * This is used to only check close block for collision or other interaction
     *
     * @param blocks Usually all blocks {@link BlockMaster#getBlocks()}
     */
    public void updateCloseBlocks(List<Block> blocks) {
        updateCloseBlocks(blocks, 5);
    }

    /**
     * Check for Keyboard and Mouse inputs and process them
     *
     * If chat is enabled, block all commands since we want to type text
     *
     * Simple movement without acceleration or anything. We can improve on this if there is time.
     *
     */
    private void checkInputs() {

        if(Game.getChat().isEnabled()) {
            currentSpeed = 0;
            return;
        }

        if (InputHandler.isKeyPressed(GLFW_KEY_Q)) {
            if (InputHandler.isPlacerMode()) {
                MousePlacer.cancelPlacing();
            } else {
                placeItem(DYNAMITE);
            }
        }

        if (InputHandler.isKeyPressed(GLFW_KEY_E)) {
            if (InputHandler.isPlacerMode()) {
                MousePlacer.cancelPlacing();
            } else {
                placeItem(TORCH);
            }
        }

        //SIMPLE Movement
        if (InputHandler.isKeyDown(GLFW_KEY_A)) {
            this.currentSpeed = -RUN_SPEED;
            this.currentTurnSpeed = -TURN_SPEED;
        } else if (InputHandler.isKeyDown(GLFW_KEY_D)) {
            this.currentSpeed = RUN_SPEED;
            this.currentTurnSpeed = TURN_SPEED;
        } else {
            this.currentSpeed = 0;
            currentTurnSpeed = 0;
        }


        if (InputHandler.isKeyPressed(GLFW_KEY_W) || InputHandler.isKeyPressed(GLFW_KEY_SPACE)) {
            jump();
        }

        if (InputHandler.isKeyPressed(GLFW_KEY_T)) {
            super.setPosition(new Vector3f(100, 0,getPosition().z ));
        }
    }

    /**
     * GENERATES an item and places it at the cursor, with a left mouse click the player can then deploy the item on the
     * cursor position.
     *
     * @param itemType Item to place as described in {@link ItemMaster.ItemTypes}
     */
    private void placeItem(ItemMaster.ItemTypes itemType) {
        if(InputHandler.isPlacerMode()) {
            //Already placing an item
            return;
        }

        //Generate item and pass it to Mouseplacer
        MousePlacer.placeEntity(
                /*
                Just place it at the player for the first frame, then update to cursor
                We dont want to run raycasting on every frame, just when the placer is active so this is an
                acceptable compromise.
                */
                ItemMaster.generateItem(itemType, getPosition())
        );
    }

}
