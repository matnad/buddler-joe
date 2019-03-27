package engine.render.fontrendering;

import engine.shaders.ShaderProgram;
import org.joml.Vector2f;
import org.joml.Vector3f;

/** Shader Program. Passes variables to the shader */
public class FontShader extends ShaderProgram {

  private static final String SHADER_NAME = "font";

  private int locationColour;
  private int locationTranslation;
  private int locationAlpha;

  FontShader() {
    super(SHADER_NAME);
  }

  @Override
  protected void getAllUniformLocations() {
    locationColour = super.getUniformLocation("colour");
    locationTranslation = super.getUniformLocation("translation");
    locationAlpha = super.getUniformLocation("alpha");
  }

  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
    super.bindAttribute(1, "textureCoords");
  }

  void loadColour(Vector3f colour) {
    super.loadVector(locationColour, colour);
  }

  void loadTranslation(Vector2f translation) {
    super.load2DVector(locationTranslation, translation);
  }

  void loadAlpha(float alpha) {
    super.loadFloat(locationAlpha, alpha);
  }
}
