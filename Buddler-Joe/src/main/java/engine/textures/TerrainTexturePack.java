package engine.textures;

/** Holds TerrainTextures for use with a blend map. */
public class TerrainTexturePack {

  private final TerrainTexture backgroundTexture;
  private final TerrainTexture textureR;
  private final TerrainTexture textureG;
  private final TerrainTexture textureB;

  /**
   * Generate a texture pack for blending textures on a terrain.
   *
   * @param backgroundTexture texture used for the background
   * @param textureR texture blended depending on red value
   * @param textureG texture blended depending on green value
   * @param textureB texture blended depending on blue value
   */
  public TerrainTexturePack(
      TerrainTexture backgroundTexture,
      TerrainTexture textureR,
      TerrainTexture textureG,
      TerrainTexture textureB) {
    this.backgroundTexture = backgroundTexture;
    this.textureR = textureR;
    this.textureG = textureG;
    this.textureB = textureB;
  }

  public TerrainTexture getBackgroundTexture() {
    return backgroundTexture;
  }

  public TerrainTexture getTextureR() {
    return textureR;
  }

  public TerrainTexture getTextureG() {
    return textureG;
  }

  public TerrainTexture getTextureB() {
    return textureB;
  }
}
