package engine.io;

import net.packets.Packet99Disconnect;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private int width;
    private int height;
    private double fpsCap, time, processedTime;
    private String title;
    private long window;
    private Vector3f backgroundColor;
    private double delta;
    private int frames;
    private double frameTime;
    private int currentFPS;
    private boolean isOneSecond; // is true for one frame per second
    private boolean fullscreen;
    private InputHandler inputHandler;

    public Window(int width, int height, int fps, String title) {
        setSize(width, height);
        this.title = title;
        fpsCap = fps;
        processedTime = 0;
        backgroundColor = new Vector3f(0f, 0f, 0f);
        frames = 0;
        frameTime = 0;
        currentFPS = 0;
        isOneSecond = false;
        setFullscreen(false);
    }

    public void create() {
        if (!glfwInit()) {
            System.err.println("Error: Couldn't initialize GLFW");
            System.exit(-1);
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        window = glfwCreateWindow(
                width,
                height,
                title,
                fullscreen ? glfwGetPrimaryMonitor() : 0,
                0);
        if(window == 0) {
            System.err.println("Error: Window couldn't be created");
            System.exit(-1);
        }

        //Set up input handler
        glfwSetInputMode(window, GLFW_STICKY_KEYS, GLFW_TRUE);
        glfwSetKeyCallback(window, InputHandler.keyboard);
        glfwSetMouseButtonCallback(window, InputHandler.mouse);
        glfwSetScrollCallback(window, InputHandler.scrollCallback);
        glfwSetCursorPosCallback(window, InputHandler.cursorPosCallback);


        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        if (!fullscreen) {
            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);
            glfwShowWindow(window);
        }

        time = getTime();
    }

    public static void setCallbacks(){
        glfwSetErrorCallback(new GLFWErrorCallback() {
            @Override
            public void invoke(int error, long description) {
                throw new IllegalStateException(GLFWErrorCallback.getDescription(description));
            }
        });
    }

    public boolean isClosed() {
        return glfwWindowShouldClose(window);
    }

    public void update() {
        glfwPollEvents();
    }

    public void kill() {
        glfwTerminate();
    }

    public void stop() {
        glfwSetWindowShouldClose(window, true);
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public double getTime(){
        return (double) System.nanoTime() / 1e9;
    }

    //Keyboard events
    public static boolean isKeyDown(int keyCode) {
        return InputHandler.isKeyDown(keyCode);
    }

    public static boolean isKeyPressed(int keyCode) {
        return InputHandler.isKeyPressed(keyCode);
    }

    public static boolean isKeyReleased(int keyCode) {
        return InputHandler.isKeyReleased(keyCode);
    }

    //Mouse events
    public static boolean isMouseDown(int button) {
        return InputHandler.isMouseDown(button);
    }

    public static boolean isMousePressed(int button) {
        return InputHandler.isMousePressed(button);
    }

    public static boolean isMouseReleased(int button) {
        return InputHandler.isMouseReleased(button);
    }

    public double getMouseScrollY() {
        return InputHandler.getMouseScrollY();
    }

    //Cursor coordinates
    public double getMouseX() {
        return InputHandler.getMouseX();
    }

    public double getMouseY() {
        return InputHandler.getMouseY();
    }

    //FPS limiter
    public boolean isUpdating() {
        delta = 0;
        boolean update = false;
        double nextTime = getTime();
        double passedTime = nextTime - time;
        processedTime += passedTime;
        time = nextTime;
        frameTime += passedTime;

        if (frameTime >= 1f) {
            frameTime = 0;
            currentFPS = frames;
            frames = 0;
            isOneSecond = true;
        } else {
            isOneSecond = false;
        }

        while (processedTime > 1f/fpsCap) {
            delta = processedTime;
            processedTime -= 1f/fpsCap;
            frames++;
            update = true;
        }
        return update;
    }


    //Getters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getFpsCap() {
        return fpsCap;
    }

    public String getTitle() {
        return title;
    }

    public long getWindow() {
        return window;
    }

    public int getCurrentFPS() {
        return currentFPS;
    }

    public boolean isOneSecond() {
        return isOneSecond;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public double getFrameTimeSeconds() {
        return delta;
    }

    //Setters
    public void setBackgroundColor(float r, float g, float b) {
        backgroundColor = new Vector3f(r,g,b);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }
}
