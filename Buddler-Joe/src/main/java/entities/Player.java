package entities;

import bin.Game;
import collision.BoundingBox;
import engine.io.InputHandler;
import engine.io.Window;
import engine.models.TexturedModel;
import entities.blocks.AbstractBlock;
import net.packets.Packet01Move;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends NetPlayer {

    private static final float RUN_SPEED = 20; //Units per second
    private static final float TURN_SPEED = 720; //Degrees per second
    private static final float GRAVITY = -45; //Units per second
    private static final float JUMP_POWER = 25; //Units per second
    private static final float DIG_TIME = 1.2f; //In seconds

    private static final float COLLISION_PUSH_OFFSET = 0.1f;

    private static float digDamage = 1; //Damage per second


    private Window window;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private List<AbstractBlock> closeBlocks;

    private boolean isInAir = false;
    private float digDelay;

    private Entity[] diggableBlocks = new Entity[4];


    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale, null, 0, Game.getUsername(), Game.myModel, Game.myTexture, Game.myModelSize);
        this.window = Game.window;
    }


    public void move(){
        super.setPositionBeforeMove(new Vector3f(super.getPosition()));
        checkInputs();
        if (getRotY() <= -90 && currentTurnSpeed <0) {
            currentTurnSpeed = 0;
        } else if (getRotY() >= 90 && currentTurnSpeed >0) {
            currentTurnSpeed = 0;
        }

        float distance = (float) (currentSpeed * window.getFrameTimeSeconds());
        super.increasePosition(distance ,0,0);
        this.increaseRotation(0, (float) (currentTurnSpeed * window.getFrameTimeSeconds()), 0);

        upwardsSpeed += GRAVITY * window.getFrameTimeSeconds();
        super.increasePosition(0, (float) (upwardsSpeed * window.getFrameTimeSeconds()), 0);

        //Handle collisions, we only check close blocks to optimize performance
        //Distance is much cheaper to check than overlap
        for (AbstractBlock closeBlock : closeBlocks) {
            handleCollision(closeBlock);
        }

        //Send server update with move
        if(Game.isConnectedToServer() && (currentSpeed != 0 || upwardsSpeed != 0 || currentTurnSpeed != 0)) {
            Packet01Move packet = new Packet01Move(Game.getUsername(), this.getPosition(), this.getRotX(), this.getRotY(), this.getRotZ());
            packet.writeData(Game.getSocketClient());
        }
    }


    private void handleCollision(Entity entity) {

        super.updateBoundingBox();
        //Entities are static for now

        //Make this mess readable
        BoundingBox a = super.getbBox(); //PlayerBox
        BoundingBox b = entity.getbBox(); //EntityBox

        //Only check in 2 dimensions
        if(this.collidesWith(entity,2)) {

            float w = (a.getMinX() + a.getMaxX()) / 2 - (b.getMinX() + b.getMaxX()) / 2;
            float h = (a.getMinY() + a.getMaxY()) / 2 - (b.getMinY() + b.getMaxY()) / 2;

            if(Math.abs(w) < Math.abs(h)) {
                if (h > 0) { //from above
                    setPositionY(b.getMaxY());
                    if (upwardsSpeed < 0)
                        upwardsSpeed = 0;
                    isInAir = false;
                    if (InputHandler.isKeyDown(GLFW_KEY_S) && entity instanceof AbstractBlock) {
                        digBlock((AbstractBlock) entity);
                    }
                } else { // from below
                     setPositionY(b.getMinY()-a.getDimY());
//                  currentSpeed = 0;
                    if (upwardsSpeed > 0)
                        upwardsSpeed = 0;
                }
            } else {
                if (w > 0) { //from right
                    setPositionX(b.getMaxX() + a.getDimX() / 2 + COLLISION_PUSH_OFFSET);
                    currentSpeed = 0;
                    isInAir = false;
                } else { // from left
                    setPositionX(b.getMinX()-a.getDimX()/2 - COLLISION_PUSH_OFFSET);
                    currentSpeed = 0;
                    isInAir = false;
                }
                if (entity instanceof AbstractBlock) {
                    digBlock((AbstractBlock) entity);
                }
            }
        }
    }

    private void digBlock(AbstractBlock block) {
        block.increaseDamage((float) (digDamage * window.getFrameTimeSeconds()));
    }


    private void jump() {
        if (!isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    public void updateCloseBlocks(List<AbstractBlock> blocks, float minDistance) {
        List<AbstractBlock> closeBlocks = new ArrayList<>();
        //Only 2D (XY) for performance
        for (AbstractBlock block : blocks) {
            if(block.get2DDistanceFrom(super.getPositionXY()) < block.getDim()+minDistance) {
                closeBlocks.add(block);
            }
        }
        this.closeBlocks =  closeBlocks;
    }

    public void updateCloseBlocks(List<AbstractBlock> blocks) {
        updateCloseBlocks(blocks, 5);
    }

    private void checkInputs() {

        if(Game.chat.isEnabled()) {
            currentSpeed = 0;
            return;
        }

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

    /* HORIZONTAL PLANE (XZ) STUFF */

    /*
    public void moveHoriz(TerrainFlat terrain) {
        this.checkInputs();
        System.out.println(getPosition());
        this.increaseRotation(0, (float) (currentTurnSpeed * window.getFrameTimeSeconds()), 0);
        float distance = (float) (currentSpeed * window.getFrameTimeSeconds());
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * window.getFrameTimeSeconds();
        super.increasePosition(0, (float) (upwardsSpeed * window.getFrameTimeSeconds()), 0);
        float terrainHeight = this.terrainHeight;//terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
//        System.out.println(""+super.getPosition().x+","+super.getPosition().z);
        if(super.getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }

        if(game.isConnectedToServer() && (currentSpeed != 0 || upwardsSpeed != 0 || currentTurnSpeed != 0)) {
            Packet01Move packet = new Packet01Move(game.getUsername(), this.getPosition(), this.getRotX(), this.getRotY(), this.getRotZ());
            packet.writeData(game.getSocketClient());
        }
    }

    //Move in XZ plane, will probably be removed but can leave it for testing
    private void checkInputsHoriz() {

        if(Game.chat.isEnabled()) {
            currentSpeed = 0;
            currentTurnSpeed = 0;
            return;
        }

        if (InputHandler.isKeyDown(GLFW_KEY_W)) {
            this.currentSpeed = RUN_SPEED;
        } else if (InputHandler.isKeyDown(GLFW_KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
        } else {
            this.currentSpeed = 0;
        }

        if (InputHandler.isKeyDown(GLFW_KEY_D)) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else if (InputHandler.isKeyDown(GLFW_KEY_A)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }

//        if (InputHandler.isKeyPressed(GLFW_KEY_SPACE)) {
//            jump();
//        }

        if (InputHandler.isKeyDown(GLFW_KEY_SPACE)) {
            upwardsSpeed = JUMP_POWER;
        } else if (InputHandler.isKeyDown(GLFW_KEY_C)) {
            upwardsSpeed = -JUMP_POWER;
        } else {
            upwardsSpeed = 0;
        }
    }
    */
}
