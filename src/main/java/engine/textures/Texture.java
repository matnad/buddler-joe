package engine.textures;

/**
 * The description of a texture. Used by the Texture loader.
 */
public interface Texture {

  /**
   * Get the height of the physical texture.
   *
   * @return The height of physical texture
   */
  float getHeight();

  /**
   * Get the width of the physical texture.
   *
   * @return The width of physical texture
   */
  float getWidth();

  /**
   * Get the OpenGL texture ID for this texture.
   *
   * @return The OpenGL texture ID
   */
  int getTextureId();
}
