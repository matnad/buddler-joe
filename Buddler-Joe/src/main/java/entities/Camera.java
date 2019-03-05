package entities;

import engine.io.InputHandler;
import engine.io.Window;
import entities.Player;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private float distanceFromPlayer;
    private float angleAroundPlayer;

    private Window window;

    private static Vector3f position = new Vector3f(0,0,0);
    private static float pitch;
    private static float yaw;
    private float roll;

    private float offsetX, offsetY, offsetZ;

    private float panSpeed = 20;

    private Player player;

    public Camera(Player player, Window window) {

        this.window = window;
        this.player = player;
        distanceFromPlayer = 30;
        angleAroundPlayer = 0;
        resetCam();
    }


    public void move() {
        calculateZoom();
        calculatePitch();
        calculateYaw();
        calculatePan();
        isResest();

        position.z = offsetZ;
        position.x = player.getPosition().x+offsetX;
        position.y = player.getPosition().y+offsetY;

    }



    public void moveTPP() {
        calculateZoomTPP();
        calculatePitchTPP();

        calculateAngleAroundPlayer();

        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
        this.yaw %= 360;
    }

    private void isResest() {
        if (InputHandler.isKeyDown(GLFW_KEY_R)) {
            resetCam();
        }
    }

    private void resetCam() {
        pitch = 30;
        yaw = 0;

        offsetZ = 55;
        offsetY = 20;
        offsetX = 0;
    }

    private void calculatePan() {
        float speed = (float) (panSpeed * window.getFrameTimeSeconds());
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


    private void calculateZoom() {
        float zoomLevel = (float) (window.getMouseScrollY() * 2f);
        offsetZ -= zoomLevel;
        if (offsetZ < 5) {
            offsetZ = 5;
        }
        if (offsetZ > 100) {
            offsetZ = 100;
        }
    }

    private void calculateZoomTPP() {
        float zoomLevel = (float) (window.getMouseScrollY() * 2f);
        distanceFromPlayer -= zoomLevel;
        if(distanceFromPlayer<5){
            distanceFromPlayer = 5;
        }
    }

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

    private void calculatePitchTPP() {
        if(InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_1)){
            float pitchChange = (float) (InputHandler.getCursorPosDY() * 0.2f);
            pitch -= pitchChange;
            if(pitch < 0){
                pitch = 0;
            }else if(pitch > 90){
                pitch = 90;
            }
        }
    }

    private void calculateAngleAroundPlayer() {
        if(InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_2)){
            float angleChange = (float) (InputHandler.getCursorPosDX() * 0.2f);
            angleAroundPlayer -= angleChange;
        }
    }

    private void calculateCameraPosition(float horizDistance, float verticDistance){
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticDistance + 4;
    }

    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch+4)));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch+4)));
    }

    public Vector3f getPosition() {
        return position;
    }

    public void invertPitch(){
        this.pitch = -pitch;
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
