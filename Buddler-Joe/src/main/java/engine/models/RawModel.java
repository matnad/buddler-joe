package engine.models;

/**
 * Stores basic information about a 3D model
 *
 * <p>- Vertex Count - ID of Vertex Array Object (VAO): The location of the model data in the
 * memory. Each vao has a buffer for each type of model data like vertex position, normals, indices,
 * etc - boundingCoords to generate the Bounding Box later
 */
public class RawModel {

  private int vaoId;
  private int vertexCount;

  private float[] boundingCoords = new float[] {};

  public RawModel(int vaoId, int vertexCount) {
    this.vaoId = vaoId;
    this.vertexCount = vertexCount;
  }

  /**
   * Returns a reference for the location of the model data in the memory.
   *
   * @return a reference for the location of the model data in the memory
   */
  public int getVaoId() {
    return vaoId;
  }

  /**
   * Returns the number of vertices present in this model.
   *
   * @return number of vertices present in this model
   */
  public int getVertexCount() {
    return vertexCount;
  }

  public float[] getBoundingCoords() {
    return boundingCoords;
  }

  public void setBoundingCoords(float[] boundingCoords) {
    this.boundingCoords = boundingCoords;
  }
}
