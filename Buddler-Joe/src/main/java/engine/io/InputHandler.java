package engine.io;

import bin.Game;
import engine.render.MasterRenderer;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.lang.Math;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    private static int[] keyState = new int[GLFW_KEY_LAST];
    private static boolean[] keyDown = new boolean[GLFW_KEY_LAST];

    private static int[] mouseState = new int[GLFW_MOUSE_BUTTON_LAST];
    private static boolean[] mouseDown = new boolean[GLFW_MOUSE_BUTTON_LAST];

    private static double cursorPosX;
    private static double cursorPosY;
    private static double cursorPosLX;
    private static double cursorPosLY;
    private static double cursorPosDX;
    private static double cursorPosDY;

    private static double mouseScrollY;

    private static Vector3f mouseRay = new Vector3f();
    private static Vector3f wallIntersection = new Vector3f();
    private static boolean placerMode = false;

    private static long window;

    public InputHandler(long window) {
        InputHandler.window = window;
    }

    //Callbacks
    protected GLFWKeyCallback keyboard = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            keyState[key] = action;
        }
    };

    protected GLFWMouseButtonCallback mouse = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseState[button] = action;
        }
    };

    protected static GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            mouseScrollY = yoffset;
        }
    };

    protected static GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double xpos, double ypos) {
            //This is mostly used to detect and quantify mouse movement. For mouse position we should use the glfw buffer!
            cursorPosX = xpos;
            cursorPosY = ypos;
            cursorPosDX = xpos - cursorPosLX;
            cursorPosDY = ypos - cursorPosLY;
        }
    };

    public static void updateRaycasting() {
        int[] viewport = new int[4];
        viewport[2] = Game.window.getWidth();
        viewport[3] = Game.window.getHeight();

        //We manually get mouse position here (via the glfw buffer) since when moving the player,
        // the mouse position changes without triggering the cursor callback function. It looks weird otherwise.

        //Unproject mouseRay with the projection Matrix, Viewport data and the cursor position
        MasterRenderer.getProjectionMatrix()
                .unprojectRay((float) getMouseX(), (float) (Game.window.getHeight() - getMouseY()), viewport, new Vector3f(), mouseRay);

        //Rotate mouseRay with camera
        new Matrix3f()
                .rotateY((float) Math.toRadians(-Game.camera.getYaw()))
                .rotateX((float) Math.toRadians(-Game.camera.getPitch()))
                .transform(mouseRay);

        //Calculate the intersection point of mouseRay and plane x=0, y=0, z=3 (a plane that goes through the center of the blocks)
        //This is done with binary search, always choosing the part of the ray with the plane in it
        float distance = Intersectionf.intersectRayPlane(Game.camera.getPosition(), mouseRay, new Vector3f(0, 0, 3), new Vector3f(0, 0, 1), 1e-5f);
        wallIntersection = getPointOnRay(Game.camera.getPosition(), mouseRay, distance);
    }

    public static Vector3f getPointOnRay(Vector3f origin, Vector3f ray, float distance) {
        Vector3f start = new Vector3f(origin.x, origin.y, origin.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return start.add(scaledRay);
    }

    public static boolean isPlacerMode() {
        return placerMode;
    }

    public static void setPlacerMode(boolean placerMode) {
        InputHandler.placerMode = placerMode;
        //Very important to only change placer mode with this function, otherwise it fucks up the cursor!
        //Disable cursor when an object is being placed with the cursor
        if(placerMode) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        } else {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }

    public static Vector3f getWallIntersection() {
        return wallIntersection;
    }

    //Update
    static void update() {
        //Do this before polling to preserve the state from the last update
        for (int i = 0; i < keyDown.length; i++) {
            keyDown[i] = isKeyDown(i);
        }

        for (int i = 0; i < mouseDown.length; i++) {
            mouseDown[i] = isMouseDown(i);
        }

        cursorPosLX = cursorPosX;
        cursorPosLY = cursorPosY;

        cursorPosDX = 0;
        cursorPosDY = 0;
        mouseScrollY = 0;

        if(isPlacerMode()) {
            updateRaycasting();
        }

    }


    //Key Handlers
    public static boolean isKeyDown(int keyCode) {
        return keyState[keyCode] != GLFW_RELEASE;
    }

    public static boolean isKeyPressed(int keyCode) {
        return isKeyDown(keyCode) && !keyDown[keyCode];
    }

    public static boolean isKeyReleased(int keyCode) {
        return !isKeyDown(keyCode) && keyDown[keyCode];
    }

    //Mouse Handlers
    public static boolean isMouseDown(int button) {
        return mouseState[button] != GLFW_RELEASE;
    }

    public static boolean isMousePressed(int button) {
        return isMouseDown(button) && !mouseDown[button];
    }

    public static boolean isMouseReleased(int button) {
        return !isMouseDown(button) && mouseDown[button];
    }

    public static double getCursorPosDX() {
        return cursorPosDX;
    }

    public static double getCursorPosDY() {
        return cursorPosDY;
    }

    //Cursor coordinates
    public static double getMouseX() {
        DoubleBuffer buffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(Game.window.getWindow(), buffer, null);
        return buffer.get(0);
    }

    public static double getMouseY() {
        DoubleBuffer buffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(Game.window.getWindow(), null, buffer);
        return buffer.get(0);
    }

    //Scrolling
    public static double getMouseScrollY() {
        return mouseScrollY;
    }
}