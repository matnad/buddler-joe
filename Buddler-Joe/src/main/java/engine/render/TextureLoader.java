package engine.render;

import static java.lang.Math.round;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBImageResize.STBIR_ALPHA_CHANNEL_NONE;
import static org.lwjgl.stb.STBImageResize.STBIR_COLORSPACE_SRGB;
import static org.lwjgl.stb.STBImageResize.STBIR_EDGE_CLAMP;
import static org.lwjgl.stb.STBImageResize.STBIR_FILTER_MITCHELL;
import static org.lwjgl.stb.STBImageResize.STBIR_FLAG_ALPHA_PREMULTIPLIED;
import static org.lwjgl.stb.STBImageResize.stbir_resize_uint8_generic;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static util.IoUtil.ioResourceToByteBuffer;

import engine.textures.Texture;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryStack;

/**
 * Loads texture files from the file system using low level buffer manipulation
 * Creates a texture in openGL and saves the texture ID. We can then load the texture with this ID.
 */
public class TextureLoader implements Texture {

  private final ByteBuffer image;

  private final int width;
  private final int height;
  private final int comp;
  private int texID;

  /**
   * Don't call this directly.
   * Will load an image into a Buffer and save width, height and alpha composition
   *
   * @param imagePath fill image path
   */
  private TextureLoader(URI imagePath) {
    ByteBuffer imageBuffer;
    try {
      imageBuffer = ioResourceToByteBuffer(imagePath, 8 * 1024);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try (MemoryStack stack = stackPush()) {
      IntBuffer w = stack.mallocInt(1);
      IntBuffer h = stack.mallocInt(1);
      IntBuffer comp = stack.mallocInt(1);

      // Use info to read image metadata without decoding the entire image.
      if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
        throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
      }

      // Decode the image
      image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
      if (image == null) {
        throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
      }

      this.width = w.get(0);
      this.height = h.get(0);
      this.comp = comp.get(0);

      texID = createTexture();
    }
  }

  /**
   * Don't call this directly.
   * Will load an image into a Buffer and save width, height and comp
   *
   * @param imagePath fill image path
   * @return Texture (can be accessed via texture interface)
   */
  public static Texture getTexture(URI imagePath) {
    return new TextureLoader(imagePath);
  }

  /**
   * To create transparency.
   */
  private void premultiplyAlpha() {
    int stride = width * 4;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int i = y * stride + x * 4;
        float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
        image.put(i, (byte) round(((image.get(i) & 0xFF) * alpha)));
        image.put(i + 1, (byte) round(((image.get(i + 1) & 0xFF) * alpha)));
        image.put(i + 2, (byte) round(((image.get(i + 2) & 0xFF) * alpha)));
      }
    }
  }

  /**
   * Create a Texture in openGL and set up the alpha channel to render transparency properly.
   * Also does edge clamp, so we need to make sure the models have seams.
   *
   * @return the ID of the texture in openGL
   */
  private int createTexture() {
    texID = glGenTextures();

    glBindTexture(GL_TEXTURE_2D, texID);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    int format;
    if (comp == 3) {
      if ((width & 3) != 0) {
        glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
      }
      format = GL_RGB;
    } else {
      premultiplyAlpha();

      glEnable(GL_BLEND);
      glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

      format = GL_RGBA;
    }

    glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, image);

    ByteBuffer inputPixels = image;
    int inputW = width;
    int inputH = height;
    int mipmapLevel = 0;
    while (1 < inputW || 1 < inputH) {
      int outputW = Math.max(1, inputW >> 1);
      int outputH = Math.max(1, inputH >> 1);

      ByteBuffer outputPixels = memAlloc(outputW * outputH * comp);
      stbir_resize_uint8_generic(
          inputPixels, inputW, inputH, inputW * comp,
          outputPixels, outputW, outputH, outputW * comp,
          comp, comp == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
          STBIR_EDGE_CLAMP,
          STBIR_FILTER_MITCHELL,
          STBIR_COLORSPACE_SRGB
      );

      if (mipmapLevel == 0) {
        stbi_image_free(image);
      } else {
        memFree(inputPixels);
      }

      glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, outputW, outputH, 0, format,
          GL_UNSIGNED_BYTE, outputPixels);

      inputPixels = outputPixels;
      inputW = outputW;
      inputH = outputH;
    }
    if (mipmapLevel == 0) {
      stbi_image_free(image);
    } else {
      memFree(inputPixels);
    }

    return texID;
  }

  @Override
  public float getHeight() {
    return height;
  }

  @Override
  public float getWidth() {
    return width;
  }


  @Override
  public int getTextureID() {
    return texID;
  }

}
