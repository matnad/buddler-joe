package engine.textures;

/**
 * Defines properties of a Model Texture
 *
 * <p>Shine Damper: Softens reflection Reflectivity: Intensifies reflectivity
 *
 * <p>Calculated in the shader as follows: finalSpecular = pow(specularFactor, shineDamper) *
 * reflectivity * lightColour; finalSpecular is then added to the texture colour
 *
 * <p>hasTransparency: Part of the texture is transparent and we need to set some extra openGL
 * options like disableCulling useFakeLighting: Simulate lighting when no normals are defined: Just
 * sets the normal to vec3(0 .0, 1.0, 0.0).
 *
 * <p>Class just has basic setters and getters to change how the texture behaves under light.
 */
@SuppressWarnings("unused") // We will use them later. TODO: Include transparency and reflectivity
public class ModelTexture {

  private int textureId;

  private float shineDamper = 1;
  private float reflectivity = 0;

  private boolean hasTransparency = false;
  private boolean useFakeLighting = false;

  private int numberOfRows = 1;

  public ModelTexture(int id) {
    this.textureId = id;
  }

  public float getShineDamper() {
    return shineDamper;
  }

  public void setShineDamper(float shineDamper) {
    this.shineDamper = shineDamper;
  }

  public float getReflectivity() {
    return reflectivity;
  }

  public void setReflectivity(float reflectivity) {
    this.reflectivity = reflectivity;
  }

  public boolean isHasTransparency() {
    return hasTransparency;
  }

  public void setHasTransparency(boolean hasTransparency) {
    this.hasTransparency = hasTransparency;
  }

  public boolean isUseFakeLighting() {
    return useFakeLighting;
  }

  public void setUseFakeLighting(boolean useFakeLighting) {
    this.useFakeLighting = useFakeLighting;
  }

  public int getId() {
    return textureId;
  }

  public int getNumberOfRows() {
    return numberOfRows;
  }

  public void setNumberOfRows(int numberOfRows) {
    this.numberOfRows = numberOfRows;
  }
}
