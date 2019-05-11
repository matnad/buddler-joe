package gui;

import static org.lwjgl.glfw.GLFW.glfwSetCursor;

import engine.io.Window;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

/**
 * The only purpose of this class is to set a provide the Method setMouseIcon().
 *
 * @author Sebastian Schlachter
 */
public class MouseIcon {

  /**
   * Sets a custom MouseIcon. (Inspired by:
   * https://gamedev.stackexchange.com/questions/124394/setting-up-a-custom-cursor-image-in-lwjgl-3)
   *
   * @param window the Window to which the cursor Icon should be applied.
   * @param fileName the filename of the png that should be set as mouseicon.
   */
  public static void setMouseIcon(Window window, String fileName) {

    try {
      InputStream stream =
          MouseIcon.class.getResourceAsStream("/assets/textures/" + fileName + ".png");
      BufferedImage image = ImageIO.read(stream);

      int width = image.getWidth();
      int height = image.getHeight();

      int[] pixels = new int[width * height];
      image.getRGB(0, 0, width, height, pixels, 0, width);

      // convert image to RGBA format
      ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int pixel = pixels[y * width + x];

          buffer.put((byte) ((pixel >> 16) & 0xFF)); // red
          buffer.put((byte) ((pixel >> 8) & 0xFF)); // green
          buffer.put((byte) (pixel & 0xFF)); // blue
          buffer.put((byte) ((pixel >> 24) & 0xFF)); // alpha
        }
      }
      buffer.flip(); // this will flip the cursor image vertically

      // create a GLFWImage
      GLFWImage cursorImg = GLFWImage.create();
      cursorImg.width(width); // setup the images' width
      cursorImg.height(height); // setup the images' height
      cursorImg.pixels(buffer); // pass image data

      // create custom cursor and store its ID
      int hotspotX = 0;
      int hotspotY = 0;
      long cursorId = GLFW.glfwCreateCursor(cursorImg, hotspotX, hotspotY);

      // set current cursor
      glfwSetCursor(window.getWindow(), cursorId);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
