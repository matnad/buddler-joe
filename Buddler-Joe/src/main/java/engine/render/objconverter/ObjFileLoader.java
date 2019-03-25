package engine.render.objconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * .obj file parser.
 * Loads a model and parses the data into arrays.
 * Is fully static and should be called as such.
 */
public class ObjFileLoader {

  private static final String RES_LOC = "src/main/resources/assets/models/";

  /**
   * Open a .obj model file and parse the content to write vertices, texture coords, normals,
   * indices and bounding
   * box coordinates in a ModelData object
   *
   * @param objFileName just the name of the file without path or extension. File must be .obj
   *                    and located in the
   *                    models folder
   * @return ModelData contains all the info of an obj file, parsed into arrays
   */
  public static ModelData loadObj(String objFileName) {
    FileReader isr = null;
    File objFile = new File(RES_LOC + objFileName + ".obj");
    try {
      isr = new FileReader(objFile);
    } catch (FileNotFoundException e) {
      System.err.println("File not found in res; don't use any extension");
    }
    BufferedReader reader = null;
    if (isr != null) {
      reader = new BufferedReader(isr);
    }
    String line;
    List<Vertex> vertices = new ArrayList<>();
    List<Vector2f> textures = new ArrayList<>();
    List<Vector3f> normals = new ArrayList<>();
    List<Integer> indices = new ArrayList<>();

    //For Bounding Box
    float minX = Float.POSITIVE_INFINITY;
    float maxX = Float.NEGATIVE_INFINITY;
    float minY = Float.POSITIVE_INFINITY;
    float maxY = Float.NEGATIVE_INFINITY;
    float minZ = Float.POSITIVE_INFINITY;
    float maxZ = Float.NEGATIVE_INFINITY;

    try {
      while (true) {
        //Parse line by line. Obj files have an identifier as the first 1 or 2 characters on each
        // line
        line = reader != null ? reader.readLine() : null;
        if (line == null) {
          continue;
        }
        if (line.startsWith("v ")) { //Vertices
          String[] currentLine = line.split(" ");
          Vector3f vertex = new Vector3f(Float.valueOf(currentLine[1]),
              Float.valueOf(currentLine[2]),
              Float.valueOf(currentLine[3]));
          Vertex newVertex = new Vertex(vertices.size(), vertex);
          vertices.add(newVertex);

          //For bounding box
          if (vertex.x < minX) {
            minX = vertex.x;
          }
          if (vertex.x > maxX) {
            maxX = vertex.x;
          }
          if (vertex.y < minY) {
            minY = vertex.y;
          }
          if (vertex.y > maxY) {
            maxY = vertex.y;
          }
          if (vertex.z < minZ) {
            minZ = vertex.z;
          }
          if (vertex.z > maxZ) {
            maxZ = vertex.z;
          }

        } else if (line.startsWith("vt ")) { //Textures
          String[] currentLine = line.split(" ");
          Vector2f texture = new Vector2f(Float.valueOf(currentLine[1]),
              Float.valueOf(currentLine[2]));
          textures.add(texture);
        } else if (line.startsWith("vn ")) { //Normals
          String[] currentLine = line.split(" ");
          Vector3f normal = new Vector3f(Float.valueOf(currentLine[1]),
              Float.valueOf(currentLine[2]),
              Float.valueOf(currentLine[3]));
          normals.add(normal);
        } else if (line.startsWith("f ")) {
          break;
        }
      }
      //Add info to vertex objects
      while (line != null && line.startsWith("f ")) {
        String[] currentLine = line.split(" ");
        String[] vertex1 = currentLine[1].split("/");
        String[] vertex2 = currentLine[2].split("/");
        String[] vertex3 = currentLine[3].split("/");
        processVertex(vertex1, vertices, indices);
        processVertex(vertex2, vertices, indices);
        processVertex(vertex3, vertices, indices);
        line = reader.readLine();
      }
      reader.close();
    } catch (IOException e) {
      System.err.println("Error reading the file");
    }
    removeUnusedVertices(vertices);
    float[] verticesArray = new float[vertices.size() * 3];
    float[] texturesArray = new float[vertices.size() * 2];
    float[] normalsArray = new float[vertices.size() * 3];
    float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
        texturesArray, normalsArray);
    int[] indicesArray = convertIndicesListToArray(indices);

    float[] boundingCoords = {minX, maxX, minY, maxY, minZ, maxZ};

    return new ModelData(verticesArray, texturesArray, normalsArray, indicesArray,
        furthest, boundingCoords);
  }

  private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
    int index = Integer.parseInt(vertex[0]) - 1;
    Vertex currentVertex = vertices.get(index);
    int textureIndex = Integer.parseInt(vertex[1]) - 1;
    int normalIndex = Integer.parseInt(vertex[2]) - 1;
    if (!currentVertex.isSet()) {
      currentVertex.setTextureIndex(textureIndex);
      currentVertex.setNormalIndex(normalIndex);
      indices.add(index);
    } else {
      dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
          vertices);
    }
  }

  private static int[] convertIndicesListToArray(List<Integer> indices) {
    int[] indicesArray = new int[indices.size()];
    for (int i = 0; i < indicesArray.length; i++) {
      indicesArray[i] = indices.get(i);
    }
    return indicesArray;
  }

  private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
                                           List<Vector3f> normals, float[] verticesArray,
                                           float[] texturesArray,
                                           float[] normalsArray) {
    float furthestPoint = 0;
    for (int i = 0; i < vertices.size(); i++) {
      Vertex currentVertex = vertices.get(i);
      if (currentVertex.getLength() > furthestPoint) {
        furthestPoint = currentVertex.getLength();
      }
      Vector3f position = currentVertex.getPosition();


      verticesArray[i * 3] = position.x;
      verticesArray[i * 3 + 1] = position.y;
      verticesArray[i * 3 + 2] = position.z;

      Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
      texturesArray[i * 2] = textureCoord.x;
      texturesArray[i * 2 + 1] = 1 - textureCoord.y;

      Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
      normalsArray[i * 3] = normalVector.x;
      normalsArray[i * 3 + 1] = normalVector.y;
      normalsArray[i * 3 + 2] = normalVector.z;
    }
    return furthestPoint;
  }

  private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
                                                     int newNormalIndex, List<Integer> indices,
                                                     List<Vertex> vertices) {
    if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
      indices.add(previousVertex.getIndex());
    } else {
      Vertex anotherVertex = previousVertex.getDuplicateVertex();
      if (anotherVertex != null) {
        dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
            indices, vertices);
      } else {
        Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
        duplicateVertex.setTextureIndex(newTextureIndex);
        duplicateVertex.setNormalIndex(newNormalIndex);
        previousVertex.setDuplicateVertex(duplicateVertex);
        vertices.add(duplicateVertex);
        indices.add(duplicateVertex.getIndex());
      }

    }
  }

  private static void removeUnusedVertices(List<Vertex> vertices) {
    for (Vertex vertex : vertices) {
      if (!vertex.isSet()) {
        vertex.setTextureIndex(0);
        vertex.setNormalIndex(0);
      }
    }
  }


}
