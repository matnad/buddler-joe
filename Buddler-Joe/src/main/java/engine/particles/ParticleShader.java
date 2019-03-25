package engine.particles;

import engine.shaders.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * Shader programm for particles.
 * Just passing some variables to the shader
 */
public class ParticleShader extends ShaderProgram {

  private static final String SHADER_NAME = "particle";

  private int locationModelViewMatrix;
  private int locationProjectionMatrix;
  private int locationTexOffset1;
  private int locationTexOffset2;
  private int locationTexCoordInfo;

  ParticleShader() {
    super(SHADER_NAME);
  }

  @Override
  protected void getAllUniformLocations() {
    locationModelViewMatrix = super.getUniformLocation("modelViewMatrix");
    locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
    locationTexOffset1 = super.getUniformLocation("texOffset1");
    locationTexOffset2 = super.getUniformLocation("texOffset2");
    locationTexCoordInfo = super.getUniformLocation("texCoordInfo");
  }

  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
  }

  void loadTextureCoordInfo(Vector2f offset1, Vector2f offset2, float numRows,
                            float blend) {
    super.load2DVector(locationTexOffset1, offset1);
    super.load2DVector(locationTexOffset2, offset2);
    super.load2DVector(locationTexCoordInfo, new Vector2f(numRows, blend));
  }

  void loadModelViewMatrix(Matrix4f modelView) {
    super.loadMatrix(locationModelViewMatrix, modelView);
  }

  void loadProjectionMatrix(Matrix4f projectionMatrix) {
    super.loadMatrix(locationProjectionMatrix, projectionMatrix);
  }

}
