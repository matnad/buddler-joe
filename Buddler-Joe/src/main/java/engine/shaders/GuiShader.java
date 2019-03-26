package engine.shaders;

import org.joml.Matrix4f;

/** Shader program for gui. Just passing some variables to the shader */
public class GuiShader extends ShaderProgram {

  private static final String SHADER_NAME = "gui";

  private int locationTransformationMatrix;
  private int locationAlpha;

  public GuiShader() {
    super(SHADER_NAME);
  }

  public void loadTransformation(Matrix4f matrix) {
    super.loadMatrix(locationTransformationMatrix, matrix);
  }

  public void loadAlpha(float alpha) {
    super.loadFloat(locationAlpha, alpha);
  }

  @Override
  protected void getAllUniformLocations() {
    locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
    locationAlpha = super.getUniformLocation("alpha");
  }

  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
  }
}
