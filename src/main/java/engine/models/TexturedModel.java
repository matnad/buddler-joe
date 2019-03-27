package engine.models;

import engine.textures.ModelTexture;

/**
 * Contains a raw model and a texture. See these classes for more details.
 *
 * @see RawModel
 * @see ModelTexture
 */
public class TexturedModel {

  private RawModel rawModel;
  private ModelTexture texture;

  public TexturedModel(RawModel model, ModelTexture texture) {
    this.rawModel = model;
    this.texture = texture;
  }

  public RawModel getRawModel() {
    return rawModel;
  }

  public ModelTexture getTexture() {
    return texture;
  }
}
