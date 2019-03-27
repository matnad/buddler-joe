package engine.shaders;

import entities.Camera;
import entities.light.Light;
import entities.light.LightMaster;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Maths;

/** Shader program for entities. Just passing some variables to the shader */
@SuppressWarnings("Duplicates")
public class StaticShader extends ShaderProgram {

  private static final int MAX_LIGHTS = LightMaster.getMaxLights();

  private static final String SHADER_NAME = "entity";

  private int locationTransformationMatrix;
  private int locationProjectionMatrix;
  private int locationViewMatrix;
  private int[] locationLightPosition;
  private int[] locationLightColour;
  private int[] locationAttenuation;
  private int locationShineDamper;
  private int locationReflectivity;
  private int locationUseFakeLighting;
  private int locationSkyColour;
  private int locationNumberOfRows;
  private int locationOffset;

  public StaticShader() {
    super(SHADER_NAME);
  }

  @Override
  protected void getAllUniformLocations() {
    locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
    locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
    locationViewMatrix = super.getUniformLocation("viewMatrix");
    locationShineDamper = super.getUniformLocation("shineDamper");
    locationReflectivity = super.getUniformLocation("reflectivity");
    locationUseFakeLighting = super.getUniformLocation("useFakeLighting");
    locationSkyColour = super.getUniformLocation("skyColour");
    locationNumberOfRows = super.getUniformLocation("numberOfRows");
    locationOffset = super.getUniformLocation("offset");

    locationLightPosition = new int[MAX_LIGHTS];
    locationLightColour = new int[MAX_LIGHTS];
    locationAttenuation = new int[MAX_LIGHTS];
    for (int i = 0; i < MAX_LIGHTS; i++) {
      locationLightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
      locationLightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
      locationAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
    }
  }

  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
    super.bindAttribute(1, "textureCoords");
    super.bindAttribute(2, "normal");
  }

  public void loadNumberOfRows(int numberOfRows) {
    super.loadFloat(locationNumberOfRows, numberOfRows);
  }

  public void loadOffset(float x, float y) {
    super.load2DVector(locationOffset, new Vector2f(x, y));
  }

  public void loadSkyColour(float r, float g, float b) {
    super.loadVector(locationSkyColour, new Vector3f(r, g, b));
  }

  public void loadFakeLightingVariable(boolean useFake) {
    super.loadBoolean(locationUseFakeLighting, useFake);
  }

  public void loadShineVariables(float damper, float reflectivity) {
    super.loadFloat(locationShineDamper, damper);
    super.loadFloat(locationReflectivity, reflectivity);
  }

  /**
   * Loads a list of lights to the shader. These lights will affect all entities.
   *
   * @param lights list of lights to load
   */
  public void loadLights(List<Light> lights) {
    for (int i = 0; i < MAX_LIGHTS; i++) {
      if (i < lights.size()) {
        super.loadVector(locationLightPosition[i], lights.get(i).getPosition());
        super.loadVector(locationLightColour[i], lights.get(i).getColour());
        super.loadVector(locationAttenuation[i], lights.get(i).getAttenuation());
      } else {
        super.loadVector(locationLightPosition[i], new Vector3f());
        super.loadVector(locationLightColour[i], new Vector3f());
        super.loadVector(locationAttenuation[i], new Vector3f(1, 0, 0));
      }
    }
  }

  public void loadTransformationMatrix(Matrix4f matrix) {
    super.loadMatrix(locationTransformationMatrix, matrix);
  }

  public void loadProjectionMatrix(Matrix4f matrix) {
    super.loadMatrix(locationProjectionMatrix, matrix);
  }

  public void loadViewMatrix(Camera camera) {
    Matrix4f viewMatrix = Maths.createViewMatrix(camera);
    super.loadMatrix(locationViewMatrix, viewMatrix);
  }
}
