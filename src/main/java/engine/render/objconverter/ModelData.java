package engine.render.objconverter;

/**
 * Fully parsed .obj file.
 *
 * <p>All the infos from an .obj file parsed and ready to load into VAOs
 */
public class ModelData {

  private float[] vertices;
  private float[] textureCoords;
  private float[] normals;
  private int[] indices;
  // private float furthestPoint;
  private float[] boundingCoords;

  // We keep the variable in the constructor for now, will probably use it again later
  @SuppressWarnings("unused")
  ModelData(
      float[] vertices,
      float[] textureCoords,
      float[] normals,
      int[] indices,
      float furthestPoint,
      float[] boundingCoords) {

    this.vertices = vertices;
    this.textureCoords = textureCoords;
    this.normals = normals;
    this.indices = indices;
    // this.furthestPoint = furthestPoint;

    if (boundingCoords.length == 6) {
      this.boundingCoords = boundingCoords;
    }
  }

  public float[] getVertices() {
    return vertices;
  }

  public float[] getTextureCoords() {
    return textureCoords;
  }

  public float[] getNormals() {
    return normals;
  }

  public int[] getIndices() {
    return indices;
  }

  public float[] getBoundingCoords() {
    return boundingCoords;
  }
}
