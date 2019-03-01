package entities;

import bin.Game;
import collision.BoundingBox;
import engine.io.InputHandler;
import engine.io.Window;
import engine.models.TexturedModel;
import entities.blocks.Block;
import net.packets.Packet01Move;
import org.joml.Vector2f;
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

    private float terrainHeight = -6.5f;


    private Window window;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private List<Block> closeBlocks;

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
//        if(super.getPosition().y < terrainHeight) {
//            upwardsSpeed = 0;
//            isInAir = false;
//            super.getPosition().y = terrainHeight;
//        }

        //Handle collisions, we only check close blocks to optimize performance
        //Distance is much cheaper to check than overlap
        for (Block closeBlock : closeBlocks) {
            handleCollision(closeBlock);
        }

        //Send server update
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
                } else { // from below
                     setPositionY(b.getMinY()-a.getDimY());
//                currentSpeed = 0;
                if (upwardsSpeed > 0)
                    upwardsSpeed = 0;
                }
            } else {
                if (w > 0) { //from right
                    setPositionX(b.getMaxX()+a.getDimX()/2);
                    currentSpeed = 0;
                    isInAir = false;

                } else { // from left
                    setPositionX(b.getMinX()-a.getDimX()/2);
                    currentSpeed = 0;
                    isInAir = false;

                }
            }

//            else {
//                // from left or right, check w to know
//                increasePosition((float) -(currentSpeed * window.getFrameTimeSeconds()), 0, 0);
//            }
//                increasePosition((float) -(currentSpeed * window.getFrameTimeSeconds()), 0, 0);
//                System.out.println("from right");
//            } else if (angle >= -135 && angle <= -45 ) { // from left
//                increasePosition((float) -(currentSpeed * window.getFrameTimeSeconds()), 0, 0);
//                System.out.println("from left");
//            }


//            Vector2f dir = new Vector2f()
//                    .add(entity.getPositionXY())
//                    .sub(super.getPositionXY());
//            float angle = (float) Math.toDegrees(dir.angle(new Vector2f(0,1)));
//            if(angle <= 160 && angle >= 45) { // from right
//                increasePosition((float) -(currentSpeed * window.getFrameTimeSeconds()), 0, 0);
//                System.out.println("from right");
//            } else if (angle >= -135 && angle <= -45 ) { // from left
//                increasePosition((float) -(currentSpeed * window.getFrameTimeSeconds()), 0, 0);
//                System.out.println("from left");
//            }
////            else if(angle < 45 && angle > -45) { // below
////                increasePosition(0, (float) -(upwardsSpeed * window.getFrameTimeSeconds()),  0);
////            } else if(angle < -135 && angle > -45) { // above
////                increasePosition(0, (float) -(upwardsSpeed * window.getFrameTimeSeconds()),  0);
////            }
//            else if (super.getPositionXY().y > entity.getPositionXY().y) { // from above
//                increasePosition(0, (float) -(upwardsSpeed * window.getFrameTimeSeconds()),  0);
//                System.out.println(angle);
//                if (upwardsSpeed < 0)
//                    upwardsSpeed = 0;
//                isInAir = false;
//            } else { // from below
//                increasePosition(0, (float) -(upwardsSpeed * window.getFrameTimeSeconds()),  0);
//                System.out.println("from below");
//                if (upwardsSpeed > 0)
//                    upwardsSpeed = 0;
//            }

//            float h = entity.getPosition().x - super.getPosition().x;
//            float w = entity.getPosition().y - super.getPosition().y;
//
//            float angle = (float) Math.toDegrees(Math.atan(h/w)); /// Math.PI * 180;
//
//            if (Math.abs(angle) < 135 && Math.abs(angle) > 45) {
//                super.setPositionX(super.getPositionBeforeMove().x);
//            } else {
//                super.setPositionY(super.getPositionBeforeMove().y);
//            }






            //Moving Right, means block is also right.
//            if(currentSpeed == 0) { // falling or jumping -> reset y
//                super.setPositionY(super.getPositionBeforeMove().y);
//            }
//            } else if() {
//
//            }

//            //Correct x pos
//
//            if(a.getMaxX() > b.getMaxX()) { //We are to the right
//                newPosX = b.getMaxX();
//                newPosX += (super.getbBox().getMaxXO() - super.getbBox().getMinXO())/2; //Shift by dimension of our box
//            } else { //We are to the left
//                newPosX = b.getMinX();
//                newPosX -= (super.getbBox().getMaxXO() - super.getbBox().getMinXO())/2; //Shift by dimension of our box
//            }
//
//            //Correct y pos
//            float newPosY = getPosition().y;
//            if(a.getMaxY() > b.getMaxY()) { //We are above
//                newPosY = b.getMaxY();
//                newPosY += (super.getbBox().getMaxYO() - super.getbBox().getMinYO())/2; //Shift by dimension of our box
//            } else { //We are below
//                newPosY = b.getMinY();
//                newPosY -= (super.getbBox().getMaxYO() - super.getbBox().getMinYO())/2; //Shift by dimension of our box
//            }
//            super.setPosition(new Vector3f(newPosX,newPosY,0));
        }
    }



    private void jump() {
        if (!isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    //This is shit and will be removed
    public void setBlocksToDig(List<Block> blocks) {
        diggableBlocks = new Block[4];
        float blockDim = 3f; //"radius"
        float reach = 5f+blockDim;
        for (Block block : blocks) {
            if ( block.getDistanceFrom(super.getPosition()) < reach) {
                //Reachable
                if (block.getPosition().x < super.getPosition().x && block.getPosition().y > super.getPosition().y-2 && block.getPosition().y < super.getPosition().y+2) {
                    diggableBlocks[3] = block;
                } else if (block.getPosition().x > super.getPosition().x && block.getPosition().y > super.getPosition().y-2 && block.getPosition().y < super.getPosition().y+2) {
                    diggableBlocks[1] = block;
                } else if (block.getPosition().y < super.getPosition().x) {
                    diggableBlocks[2] = block;
                } else {
                    diggableBlocks[0] = block;
                }
            }
        }
    }

    public void updateCloseBlocks(List<Block> blocks, float minDistance) {
        List<Block> closeBlocks = new ArrayList<>();
        //Only 2D (XY) for performance
        for (Block block : blocks) {
            if(block.get2DDistanceFrom(super.getPositionXY()) < block.getDim()+minDistance) {
                closeBlocks.add(block);
            }
        }
        this.closeBlocks =  closeBlocks;
    }

    public void updateCloseBlocks(List<Block> blocks) {
        updateCloseBlocks(blocks, 5);
    }


    //Temp demo function
    private void digHorizontal() {
        Entity digBlock = null;
        if(getRotY() < -10) {
            digBlock = diggableBlocks[3];
        } else if(getRotY() > 10) {
            digBlock = diggableBlocks[1];
        }
        if (digBlock != null) {
            digDelay += window.getFrameTimeSeconds();

            if (digDelay >= DIG_TIME) {
                digDelay = 0;
                digBlock.setPosition(new Vector3f(0,0,1000)); //really just for demo.
            }
        } else {
            digDelay = 0;
        }
    }

    //temp demo
    private void digDown() {
        if(diggableBlocks[2] != null) {
            digDelay += window.getFrameTimeSeconds();
            if (digDelay >= DIG_TIME) {
                digDelay = 0;
                diggableBlocks[2].setPosition(new Vector3f(0,0,1000)); //really just for demo.
            }
        } else {
            digDelay = 0;
        }
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

        if (InputHandler.isKeyPressed(GLFW_KEY_S)) {
            terrainHeight -= 6.25;
        }

        if (InputHandler.isKeyPressed(GLFW_KEY_T)) {
            super.setPosition(new Vector3f(100, 0,getPosition().z ));
            terrainHeight = -6.5f;
        }

        if (InputHandler.isKeyDown(GLFW_KEY_Q)) {
            digHorizontal();
        }

        if (InputHandler.isKeyDown(GLFW_KEY_C)) {
            digDown();
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
