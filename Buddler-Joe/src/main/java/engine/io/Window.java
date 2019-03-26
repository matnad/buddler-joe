package engine.io;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_STICKY_KEYS;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

/**
 * Set up the GLFW Game Window.
 *
 * <p>Controls
 */
public class Window {
  private int width;
  private int height;
  private final double fpsCap;
  private double time;
  private double processedTime;
  private final String title;
  private long window;
  private double delta;
  private int frames;
  private double frameTime;
  private int currentFps;
  private boolean isOneSecond; // is true for one frame per second
  private boolean fullscreen;

  /**
   * Create a new Game Window. We only ever need one.
   *
   * @param width x axis screen resolution
   * @param height y axis screen resolution
   * @param fps maximum fps (cap)
   * @param title title of the window
   */
  public Window(int width, int height, int fps, String title) {
    setSize(width, height);
    this.title = title;
    fpsCap = fps;
    processedTime = 0;
    frames = 0;
    frameTime = 0;
    currentFps = 0;
    isOneSecond = false;
    setFullscreen(false);
  }

  /** Calls the openGL binds to create a game window and sets openGL parameters. */
  public void create() {
    if (!glfwInit()) {
      System.err.println("Error: Couldn't initialize GLFW");
      System.exit(-1);
    }

    // Set openGL version to 4.00 core
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
    glfwWindowHint(GLFW_VERSION_MINOR, 0);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    window = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : 0, 0);
    if (window == 0) {
      System.err.println("Error: Window couldn't be created"); // No or old openGL is usually the
      // cause for this error
      System.exit(-1);
    }

    // Set up input handler properties

    // So we don't skip over very quick key presses
    glfwSetInputMode(window, GLFW_STICKY_KEYS, GLFW_TRUE);

    // add all the callback functions from the Input handler to the window
    glfwSetKeyCallback(window, InputHandler.keyboard);
    glfwSetMouseButtonCallback(window, InputHandler.mouse);
    glfwSetScrollCallback(window, InputHandler.scrollCallback);
    glfwSetCursorPosCallback(window, InputHandler.cursorPosCallback);

    // To tell openGL we are now in this window
    glfwMakeContextCurrent(window);
    GL.createCapabilities();

    // Set window dimensions on main monitor if fullscreen is not used
    if (!fullscreen) {
      GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
      if (videoMode != null) {
        glfwSetWindowPos(
            window, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);
      }
      glfwShowWindow(window);
    }

    time = getTime();
  }

  /** Returns true if the window is about to be closed or closed already. */
  public boolean isClosed() {
    return glfwWindowShouldClose(window);
  }

  /**
   * Processes all pending events. Runs this: {@link GLFW#glfwPollEvents()}
   *
   * @see org.lwjgl.glfw.GLFW#glfwPollEvents()
   */
  public void update() {
    glfwPollEvents();
  }

  /** Manually terminate the window. */
  public void kill() {
    glfwTerminate();
  }

  /**
   * Sets the value of the close flag of the specified window. This can be used to override the
   * user's attempt to close the window, or to signal that it should be closed.
   */
  public void stop() {
    glfwSetWindowShouldClose(window, true);
  }

  /**
   * Swaps the front and back buffers of the specified window when rendering with OpenGL or OpenGL
   * ES. If the swap interval is greater than zero, the GPU driver waits the specified number of
   * screen updates before swapping the buffers.
   */
  public void swapBuffers() {
    glfwSwapBuffers(window);
  }

  /** Returns system time in seconds. */
  private double getTime() {
    return (double) System.nanoTime() / 1e9;
  }

  /**
   * Returns true if the Game Loop should update and render a frame. This is to limit the FPS of the
   * game.
   */
  public boolean isUpdating() {
    delta = 0;
    boolean update = false;
    double nextTime = getTime();
    double passedTime = nextTime - time;
    processedTime += passedTime;
    time = nextTime;
    frameTime += passedTime;

    // Set the isOneSecond flag for one frame every second and
    // saves the number of frames rendered during the last second
    if (frameTime >= 1f) {
      frameTime = 0;
      currentFps = frames;
      frames = 0;
      isOneSecond = true;
    } else {
      isOneSecond = false;
    }

    // If time since last update is more than 1/fpsCap, update
    while (processedTime > 1f / fpsCap) {
      delta = processedTime;
      processedTime -= 1f / fpsCap;
      frames++;
      update = true;
    }
    return update;
  }

  // Getters
  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  /** Returns the GLFW window as a number. */
  public long getWindow() {
    return window;
  }

  public int getCurrentFps() {
    return currentFps;
  }

  /** Returns true for exactly one frame every second. */
  public boolean isOneSecond() {
    return isOneSecond;
  }

  /**
   * Returns the time the time delta between this and last frame in seconds. Is used to sync
   * frame-time tasks with real-time tasks.
   */
  public double getFrameTimeSeconds() {
    return delta;
  }

  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void setFullscreen(boolean fullscreen) {
    this.fullscreen = fullscreen;
  }
}
