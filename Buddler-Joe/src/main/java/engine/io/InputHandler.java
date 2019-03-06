package engine.io;

import bin.Game;
import engine.render.MasterRenderer;
import entities.Camera;
import org.joml.Intersectionf;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Keystates, CursorPos, -Movement, Mousestates, Raycasting.
 *
 * Fully static class since there is only one input to listen to. Is truly global and can be used everywhere.
 *
 * Has the following functionalities: *
 * Keyboard:
 *      - KeyDown: Detects if a key is held down
 *      - KeyPressed: Detects if a key is pressed and was not pressed last frame
 *
 * Mouse:
 *      - Button Down: Detects if a button is held down
 *      - Button Pressed: Detects if a button is pressed and was not pressed last frame
 *      - Scrolldistance of current frame
 *      - MouseDistance traveled in X and Y of current frame
 *      - MouseCursor Position via callback and directly from the buffer (important when mouse was not moved but cursor
 *          changed position)
 *
 * RayCasting:
 *      - Current projected mouse cursor vector, updated every frame while MousePlacer is true
 *      - Intersection point of the mouse vector with the Z+3 plane to place items in 2D
 *
 * @author Joe's Buddler Corp.
 *
 */
public class InputHandler {
    private static final int[] keyState = new int[GLFW_KEY_LAST];
    private static final boolean[] keyDown = new boolean[GLFW_KEY_LAST];

    private static final int[] mouseState = new int[GLFW_MOUSE_BUTTON_LAST];
    private static final boolean[] mouseDown = new boolean[GLFW_MOUSE_BUTTON_LAST];

    private static double cursorPosX;
    private static double cursorPosY;
    private static double cursorPosLastFrameX;
    private static double cursorPosLastFrameY;
    private static double cursorPosDX;
    private static double cursorPosDY;

    private static double mouseScrollY;
    private static final Vector3f mouseRay = new Vector3f();
    private static Vector3f wallIntersection = new Vector3f();
    private static boolean placerMode = false;

    private static final long window = Game.window.getWindow();

    /*
     * Callbacks get triggered when an event happens that GLFW picks up.
     * Callbacks then pass a number of arguments to an invoke method that is run every time the callback fires.
     */
    static GLFWKeyCallback keyboard = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            //save the state of the pressed key
            keyState[key] = action;
        }
    };

    static GLFWMouseButtonCallback mouse = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            //save the state of the pressed button
            mouseState[button] = action;
        }
    };

    static GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            //yoffset is the "normal" scroll direction. Simply saves the distance scrolled since the last update.
            mouseScrollY = yoffset;
        }
    };

    static GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double xpos, double ypos) {
            //This is mostly used to detect and quantify mouse movement. For mouse position we should use the glfw buffer!
            cursorPosX = xpos;
            cursorPosY = ypos;

            //Save the difference of the current position and the position in the last frame to get the delta
            cursorPosDX = xpos - cursorPosLastFrameX;
            cursorPosDY = ypos - cursorPosLastFrameY;
        }
    };

    /**
     * CursorPosition to 3D.
     *
     * Calculate a 3D direction vector originating from the camera.
     * Save the vector and the intersection with the z=3 plane for static access.
     * Is updated every frame when the MousePlacer is active.
     *
     * @param camera the origin of the ray
     *
     * @author Joe's Buddler Corp. (Matthias)
     *
     */
    private static void updateRaycasting(Camera camera) {
        //ViewPort is used by JOML and stores x,y,width,height of the window
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
                .rotateY((float) Math.toRadians(-camera.getYaw()))
                .rotateX((float) Math.toRadians(-camera.getPitch()))
                .transform(mouseRay);

        //Calculate the intersection point of mouseRay and plane x=0, y=0, z=3 (a plane that goes through the center of the blocks)
        //This is done with binary search, always choosing the part of the ray with the plane in it
        float distance = Intersectionf.intersectRayPlane(camera.getPosition(), mouseRay, new Vector3f(0, 0, 3), new Vector3f(0, 0, 1), 1e-5f);
        wallIntersection = getPointOnRay(camera.getPosition(), mouseRay, distance);
    }


    /**
     * Simple vector math to get point on a ray.
     *
     * @param origin starting point of the ray. Usually a camera position
     * @param ray direction vector
     * @param distance distance to travel on the vector
     * @return point at distance from origin in direction of the ray
     */
    private static Vector3f getPointOnRay(Vector3f origin, Vector3f ray, float distance) {
        Vector3f start = new Vector3f(origin.x, origin.y, origin.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return start.add(scaledRay);
    }

    /**
     * Returns true if the Game is currently asking the player to place an object with the mouse cursor.
     */
    public static boolean isPlacerMode() {
        return placerMode;
    }

    /**
     * Enter and leave placer mode. Mouse cursor is disabled while in placer mode to get a better view of the object
     * being placed. Never set placerMode directly!
     * @param placerMode True to enter placerMode, False to leave it
     */
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

    /**
     * Returns position of the intersection with the mouse ray and the z=3 plane. Thus z will always be 3 here.
     *
     * A 3D direction vector originating from the camera is updated every frame while in placer mode and the
     * intersection with the z=3 plane can be found with this static method. This position vector is used to place
     * objects in our pseudo 3D world.
     *
     */
    public static Vector3f getWallIntersection() {
        return wallIntersection;
    }


    /**
     * Called every frame to update states of keys and mouse.
     * While in placer mode it will also update the mouse ray and z=3 wall intersection
     * It is very important that this is called before the window polling happens or the single press functions will
     * not work.
     */
    public static void update() {
        //Do this before polling to preserve the state from the last update
        for (int i = 0; i < keyDown.length; i++) {
            keyDown[i] = isKeyDown(i);
        }

        for (int i = 0; i < mouseDown.length; i++) {
            mouseDown[i] = isMouseDown(i);
        }

        cursorPosLastFrameX = cursorPosX;
        cursorPosLastFrameY = cursorPosY;

        //These have to be set to 0 if not otherwise set this frame. Otherwise we get lingering movement
        cursorPosDX = 0;
        cursorPosDY = 0;
        mouseScrollY = 0;

        if(isPlacerMode()) {
            updateRaycasting(Game.getActiveCamera());
        }

    }


    /**
     * @param keyCode GLFW intentifier of the pressed Key
     * @return true if the key is not released (pressed or held) in this frame
     */
    public static boolean isKeyDown(int keyCode) {
        return keyState[keyCode] != GLFW_RELEASE;
    }

    /**
     * Is true ONCE on the first frame a key is being held down or pressed. The same key needs to be released
     * before it can trigger this function again.
     *
     * GLFW has no built in method to detect if a key was just pressed and not held as far as we can see.
     *
     * @param keyCode GLFW intentifier of the pressed Key
     * @return true if the key is pressed or held this frame and was not last frame
     */
    public static boolean isKeyPressed(int keyCode) {
        return isKeyDown(keyCode) && !keyDown[keyCode];
    }

    /**
     * Is true ONCE on the first frame a key is being released. The same key needs to be pressed
     * before it can trigger this function again.
     *
     * GLFW has no built in method to detect if a key was just released as far as we can see.
     *
     * @param keyCode GLFW intentifier of the pressed Key
     * @return true if the key is not pressed and was pressed last frame
     */
    public static boolean isKeyReleased(int keyCode) {
        return !isKeyDown(keyCode) && keyDown[keyCode];
    }

    /**
     * @param button GLFW intentifier of the pressed mouse button
     * @return true if the button is not released (pressed or held) in this frame
     */
    public static boolean isMouseDown(int button) {
        return mouseState[button] != GLFW_RELEASE;
    }

    /**
     * Is true ONCE on the first frame a button is being held down or pressed. The same button needs to be released
     * before it can trigger this function again.
     *
     * GLFW has no built in method to detect if a button was just pressed and not held as far as we can see.
     *
     * @param button GLFW intentifier of the pressed button
     * @return true if the button is pressed or held this frame and was not last frame
     */
    public static boolean isMousePressed(int button) {
        return isMouseDown(button) && !mouseDown[button];
    }

    /**
     * Is true ONCE on the first frame a button is being released. The same button needs to be pressed
     * before it can trigger this function again.
     *
     * GLFW has no built in method to detect if a button was just released as far as we can see.
     *
     * @param button GLFW intentifier of the pressed button
     * @return true if the button is not pressed and was pressed last frame
     */
    public static boolean isMouseReleased(int button) {
        return !isMouseDown(button) && mouseDown[button];
    }


    /**
     * Returns the distance the mouse was moved since the last frame in the horizontal direction.
     */
    public static double getCursorPosDX() {
        return cursorPosDX;
    }

    /**
     * Returns the distance the mouse was moved since the last frame in the vertical direction.
     */
    public static double getCursorPosDY() {
        return cursorPosDY;
    }


    /**
     * Returns the X position of the cursor, in screen coordinates, relative to the upper-left corner of the client
     * area of the specified window.
     *
     * Use this if you need the cursor position even if the cursor is not moving (when callback is unreliable).
     */
    public static double getMouseX() {
        DoubleBuffer buffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(Game.window.getWindow(), buffer, null);
        return buffer.get(0);
    }

    /**
     * Returns the Y position of the cursor, in screen coordinates, relative to the upper-left corner of the client
     * area of the specified window.
     *
     * Use this if you need the cursor position even if the cursor is not moving (when callback is unreliable).
     */
    public static double getMouseY() {
        DoubleBuffer buffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(Game.window.getWindow(), null, buffer);
        return buffer.get(0);
    }

    /**
     * Returns the "normal" scroll distance with a mouse wheel since the last frame.
     * Positive value when scrolling the wheel "up" or away from the person, and negative value when scrolling "down"
     * or towards the person.
     */
    //Scrolling
    public static double getMouseScrollY() {
        return mouseScrollY;
    }
}