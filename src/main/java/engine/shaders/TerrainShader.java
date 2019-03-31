package engine.shaders;

import entities.Camera;
import entities.light.Light;
import entities.light.LightMaster;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import util.Maths;

/** Shader program for terrain. Just passing some variables to the shader */
@SuppressWarnings("Duplicates")
public class TerrainShader extends ShaderProgram {

  private static final int MAX_LIGHTS = LightMaster.getMaxLights();

  private static final String SHADER_NAME = "terrain";

  private int locationTransformationMatrix;
  private int locationProjectionMatrix;
  private int locationViewMatrix;
  private int[] locationLightPosition;
  private int[] locationLightColour;
  private int[] locationAttenuation;
  private int[] locationLightDirection;
  private int[] locationCutoff;
  private int locationShineDamper;
  private int locationReflectivity;
  private int locationSkyColour;
  private int locationBackgroundTexture;
  private int locationRTexture;
  private int locationGTexture;
  private int locationBTexture;
  private int locationBlendMap;

  public TerrainShader() {
    super(SHADER_NAME);
  }

  @Override
  protected void getAllUniformLocations() {
    locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
    locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
    locationViewMatrix = super.getUniformLocation("viewMatrix");
    locationShineDamper = super.getUniformLocation("shineDamper");
    locationReflectivity = super.getUniformLocation("reflectivity");
    locationSkyColour = super.getUniformLocation("skyColour");
    locationBackgroundTexture = super.getUniformLocation("backgroundTexture");
    locationRTexture = super.getUniformLocation("textureR");
    locationGTexture = super.getUniformLocation("textureG");
    locationBTexture = super.getUniformLocation("textureB");
    locationBlendMap = super.getUniformLocation("blendMap");

    locationLightPosition = new int[MAX_LIGHTS];
    locationLightColour = new int[MAX_LIGHTS];
    locationAttenuation = new int[MAX_LIGHTS];
    locationLightDirection = new int[MAX_LIGHTS];
    locationCutoff = new int[MAX_LIGHTS];
    for (int i = 0; i < MAX_LIGHTS; i++) {
      locationLightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
      locationLightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
      locationAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
      locationLightDirection[i] = super.getUniformLocation("lightDirection[" + i + "]");
      locationCutoff[i] = super.getUniformLocation("lightCutoff[" + i + "]");
    }
  }

  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
    super.bindAttribute(1, "textureCoords");
    super.bindAttribute(2, "normal");
  }

  /** Load position of the textures to blend into openGL. */
  public void connectTextureUnits() {
    super.loadInt(locationBackgroundTexture, 0);
    super.loadInt(locationRTexture, 1);
    super.loadInt(locationGTexture, 2);
    super.loadInt(locationBTexture, 3);
    super.loadInt(locationBlendMap, 4);
  }

  public void loadSkyColour(float r, float g, float b) {
    super.loadVector(locationSkyColour, new Vector3f(r, g, b));
  }

  public void loadShineVariables(float damper, float reflectivity) {
    super.loadFloat(locationShineDamper, damper);
    super.loadFloat(locationReflectivity, reflectivity);
  }

  /**
   * Loads a list of lights to the shader. These lights will affect the terrain.
   *
   * @param lights list of lights to load
   */
  public void loadLights(List<Light> lights) {
    for (int i = 0; i < MAX_LIGHTS; i++) {
      if (i < lights.size()) {
        super.loadVector(locationLightPosition[i], lights.get(i).getPosition());
        super.loadVector(locationLightColour[i], lights.get(i).getColour());
        super.loadVector(locationAttenuation[i], lights.get(i).getAttenuation());
        super.loadVector(locationLightDirection[i], lights.get(i).getDirection());
        super.loadFloat(locationCutoff[i], lights.get(i).getCutoff());
      } else {
        super.loadVector(locationLightPosition[i], new Vector3f());
        super.loadVector(locationLightColour[i], new Vector3f());
        super.loadVector(locationAttenuation[i], new Vector3f(1, 0, 0));
        super.loadVector(locationLightDirection[i], new Vector3f());
        super.loadFloat(locationCutoff[i], 0);
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
