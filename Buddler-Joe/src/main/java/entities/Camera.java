package entities;

import engine.io.InputHandler;
import engine.io.Window;
import entities.Player;
import game.Game;
import org.joml.Vector3f;
import util.Maths;

import static game.Game.Stage.GAMEMENU;
import static game.Game.Stage.PLAYING;
import static org.lwjgl.glfw.GLFW.*;

/**
 * The main camera of the game that stays around the player character.
 * It can be
 * - moved (pan): Middle Mouse Button or Arrow Keys
 * - tilted (pitch): Left Mouse Button
 * - turned (yaw): Right Mouse Button
 * - zoomed: Scroll Wheel
 * - reset: R
 *
 * All the movement options have maximum and minimum values to keep the character somewhat in frame.
 * If more than one button is pressed at the same time, the camera can be moved along all selected axes
 *
 * Currently we only have one camera, but support for multiple cameras is definitely possible
 * and relatively easy to accomplish.
 *
 * This object will be used in a lot of places to calculate the transformation of the World Coordinates.
 * See here: {@link Maths#createViewMatrix()}
 */
public class Camera {

    private Window window;

    private static Vector3f position = new Vector3f(0,0,0);
    private static float pitch;
    private static float yaw;
    private float roll; //Not used right now, but we might

    private float offsetX, offsetY, offsetZ;

    private float panSpeed = 20;

    private Player player;

    public Camera(Player player, Window window) {

        this.window = window;
        this.player = player;
        resetCam();
    }


    public void move() {

        if(Game.getActiveStages().size() == 1 && Game.getActiveStages().get(0) == PLAYING) {

            calculateZoom();
            calculatePitch();
            calculateYaw();
            calculatePan();
            isResest();
        }

        position.z = offsetZ;
        position.x = player.getPosition().x + offsetX;
        position.y = player.getPosition().y + offsetY;

    }

    /**
     * Check if a reset is requested by the player and reset the camera
     */
    private void isResest() {
        if (InputHandler.isKeyDown(GLFW_KEY_R)) {
            resetCam();
        }
    }

    /**
     * Reset the camera to the default position and orientation
     */
    private void resetCam() {
        pitch = 35;
        yaw = 0;

        offsetZ = 65;
        offsetY = 35;
        offsetX = 0;
    }

    /**
     * Camera can be panned with the arrow keys or by holding down the middle mouse button and moving the mouse
     * This will update camera position and make sure the pan never goes above the maximum distance from the character
     */
    private void calculatePan() {
        float speed = (float) (panSpeed * window.getFrameTimeSeconds()); //panSpeed is in seconds, so we multiply by frame delta
        if (InputHandler.isKeyDown(GLFW_KEY_LEFT)) {
            offsetX -= speed;
        } else if (InputHandler.isKeyDown(GLFW_KEY_RIGHT)) {
            offsetX += speed;
        } else if (InputHandler.isKeyDown(GLFW_KEY_UP)) {
            offsetY += speed;
        } else if (InputHandler.isKeyDown(GLFW_KEY_DOWN)) {
            offsetY -= speed;
        }

        if(InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_3)){
            offsetX += (float) (InputHandler.getCursorPosDX() * 0.1f);
            offsetY -= (float) (InputHandler.getCursorPosDY() * 0.1f);
        }

        if (offsetX > 30) {
            offsetX = 30;
        } else if (offsetX < -30) {
            offsetX = -30;
        } else if (offsetY > 50) {
            offsetY = 50;
        } else if (offsetY < -30) {
            offsetY = -30;
        }

    }

    /**
     * Camera can be zoomed in and out by scrolling the mouse wheel.
     * This will update camera position and make sure the zoom never goes above or below the allowed distances
     */
    private void calculateZoom() {
        float zoomLevel = (float) (InputHandler.getMouseScrollY() * 2f);
        offsetZ -= zoomLevel;
        if (offsetZ < 5) {
            offsetZ = 5;
        }
        if (offsetZ > 100) {
            offsetZ = 100;
        }
    }

    /**
     * Camera can be pitched up and down while holding the left mouse button.
     * This will update update and bound the pitch. "Updating" of the position based on pitch is done in
     * {@link Maths#createViewMatrix()}
     *
     * Pitch is also used for Ray Casting calculations.
     */
    private void calculatePitch() {
        if(InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_1)){
            float pitchChange = (float) (InputHandler.getCursorPosDY() * 0.2f);
            pitch += pitchChange;
            if(pitch > 60){
                pitch = 60;
            }else if(pitch < -10){
                pitch = -10;
            }
        }
    }

    /**
     * Camera can be yawed left and right while holding the right mouse button.
     * This will update update and bound the yaw. "Updating" of the position based on yaw is done in
     * {@link Maths#createViewMatrix()}
     *
     * Yaw is also used for Ray Casting calculations.
     */
    private void calculateYaw() {
        if(InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_2)){
            float yawChange = (float) (InputHandler.getCursorPosDX() * 0.2f);
            yaw += yawChange;
            if(yaw < -75){
                yaw = -75;
            }else if(yaw > 75){
                yaw = 75;
            }
        }
    }


    public Vector3f getPosition() {
        return position;
    }


    /**
     * Maybe as a setting?
     */
    public void invertPitch(){
        pitch = -pitch;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }


}
