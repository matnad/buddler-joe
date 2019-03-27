package engine.particles;

/**
 * Stores openGL reference to the texture (glGenTextures) as well as how many frames the texture has
 * (rows * rows) and if the texture uses additive blending or not.
 */
public class ParticleTexture {
  private final int textureId;
  private final int numberOfRows;
  private final boolean additive;

  /**
   * Stores texture. Only called from the texture loader and gets openGL texture ID.
   *
   * @param textureId openGL reference to the texture in memory
   * @param numberOfRows 1 for static, 2+ for square texture atlas (specify number of rows=columns)
   * @param additive true if the textures wants to use additive blending
   */
  public ParticleTexture(int textureId, int numberOfRows, boolean additive) {
    this.textureId = textureId;
    this.numberOfRows = numberOfRows;
    this.additive = additive;
  }

  int getTextureId() {
    return textureId;
  }

  int getNumberOfRows() {
    return numberOfRows;
  }

  boolean isAdditive() {
    return additive;
  }
}
