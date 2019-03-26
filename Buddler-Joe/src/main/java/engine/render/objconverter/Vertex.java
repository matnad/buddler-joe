package engine.render.objconverter;

import org.joml.Vector3f;

/**
 * One Vertex in a 3D Model with index information Only used WHILE parsing obj files to keep the
 * ObjFileLoader less cluttered. *
 */
public class Vertex {

  private static final int NO_INDEX = -1;

  private Vector3f position;
  private int textureIndex = NO_INDEX;
  private int normalIndex = NO_INDEX;
  private Vertex duplicateVertex = null;
  private int index;
  private float length;

  Vertex(int index, Vector3f position) {
    this.index = index;
    this.position = position;
    this.length = position.length();
  }

  int getIndex() {
    return index;
  }

  float getLength() {
    return length;
  }

  boolean isSet() {
    return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
  }

  boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
    return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
  }

  public Vector3f getPosition() {
    return position;
  }

  int getTextureIndex() {
    return textureIndex;
  }

  void setTextureIndex(int textureIndex) {
    this.textureIndex = textureIndex;
  }

  int getNormalIndex() {
    return normalIndex;
  }

  void setNormalIndex(int normalIndex) {
    this.normalIndex = normalIndex;
  }

  Vertex getDuplicateVertex() {
    return duplicateVertex;
  }

  void setDuplicateVertex(Vertex duplicateVertex) {
    this.duplicateVertex = duplicateVertex;
  }
}
