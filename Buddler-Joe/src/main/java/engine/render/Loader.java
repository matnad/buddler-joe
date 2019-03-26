package engine.render;

import static org.lwjgl.opengl.GL30.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL30.GL_REPEAT;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteTextures;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL30.glTexParameterf;
import static org.lwjgl.opengl.GL30.glTexParameteri;

import engine.models.RawModel;
import engine.render.objconverter.ModelData;
import engine.textures.Texture;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

/**
 * Loads models and textures into Vertex Array Objects and Textures into Buffers
 *
 * <p>Provides different methods for different kind of models: - Simple Geometric Forms (just
 * vertices) - Text Models (Flat vertices and texture coords) - Standard 3D Models: Vertices,
 * Texture Coords, Normals, Indices. everything contained in an obj file - Standard 3D Models with
 * Bounding Boxes (simplified the constructor for this a bit)
 *
 * <p>Provides different methods to load textures into openGL Buffers. Does not load the Textures
 * from the File System. This is done in the TextureLoader class - Font Map (created with Hierro or
 * a similar tool) - Regular Terrain and Entity Textures Sets some options for textures such as Lod
 * Bias (lower rendering quality with distance)
 */
public class Loader {

  private final List<Integer> vaos = new ArrayList<>();
  private final List<Integer> vbos = new ArrayList<>();
  private final List<Integer> textures = new ArrayList<>();

  /**
   * Load simple geometric figures.
   *
   * <p>For example quads for particles or GUI elements.
   *
   * @param positions vertices
   * @return Raw Model that holds the VAO ID
   */
  public RawModel loadToVao(float[] positions) {
    int vaoID = createVao();
    this.storeDataInAttributeList(0, 2, positions);
    unbindVao();
    return new RawModel(vaoID, positions.length / 2);
  }

  /**
   * Load Textured simple geometric figures.
   *
   * <p>For example letters or words of a text.
   *
   * @param positions Vertices
   * @param textureCoords TextureCoordinates
   * @return Raw Model that holds the VAO ID
   */
  public int loadToVao(float[] positions, float[] textureCoords) {
    final int vaoID = createVao();
    storeDataInAttributeList(0, 2, positions);
    storeDataInAttributeList(1, 2, textureCoords);
    unbindVao();
    return vaoID;
  }

  /**
   * Load a normal model without Bounding Box.
   *
   * <p>Used for all models that dont need collision. Can just use the ModelData constructor too.
   *
   * @param positions Vertices
   * @param textureCoords TextureCoords
   * @param normals Normal vectors
   * @param indices Indices (Order/Combination of V/T/N)
   * @return Raw Model that holds the VAO ID
   */
  public RawModel loadToVao(
      float[] positions, float[] textureCoords, float[] normals, int[] indices) {
    final int vaoID = createVao();
    bindIndicesBuffer(indices);
    storeDataInAttributeList(0, 3, positions);
    storeDataInAttributeList(1, 2, textureCoords);
    storeDataInAttributeList(2, 3, normals);
    unbindVao();
    return new RawModel(vaoID, indices.length);
  }

  /**
   * Shorter Constructor for models from the obj loader. Should use this for pretty much all normal
   * 3D models.
   *
   * @param data Direct output of the obj loader. Contains vertices, texture coords, normals,
   *     indices and potentially bounding box
   * @return Raw Model that holds the VAO ID
   */
  public RawModel loadToVao(ModelData data) {
    RawModel rawModel =
        loadToVao(
            data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
    if (data.getBoundingCoords().length == 6) {
      rawModel.setBoundingCoords(data.getBoundingCoords());
    }
    return rawModel;
  }

  /**
   * Load FONT Texture into openGL, set some parameters and get the ID (position) The actual loading
   * is done in the TextureLoader class.
   *
   * <p>For fonts we use slightly different parameters than for the rest of the textures. (No
   * wrapping, no blurring, etc)
   *
   * @param fileName font atlas. Must be png. Just file name
   * @return The OpenGL texture ID
   */
  public int loadFontTexture(String fileName) {
    Texture texture = null;
    try {
      // Load the texture from the file system into openGL
      texture = TextureLoader.getTexture("/assets/fonts/" + fileName + ".png");

      // Set parameters such as rendering function and distance/quality (LOD BIAS)
      glGenerateMipmap(GL_TEXTURE_2D);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0f); // Stay sharp
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Tried to load texture " + fileName + ".png , didn't work");
      System.exit(-1);
    }
    textures.add(texture.getTextureID());
    return texture.getTextureID();
  }

  /**
   * Load ENTITY and TERRAIN Texture into openGL, set some parameters and get the ID (position) The
   * actual loading is done in the TextureLoader class.
   *
   * @param fileName font atlas. Must be png. Just file name
   * @return The OpenGL texture ID
   */
  public int loadTexture(String fileName) {
    Texture texture = null;
    try {
      texture = TextureLoader.getTexture("/assets/textures/" + fileName + ".png");
      glGenerateMipmap(GL_TEXTURE_2D);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -.3f); // Textures appear blurred the
      // further away they are
      // GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11
      // .GL_LINEAR); //Experimental Filters
      // GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // Tiling
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Tried to load texture " + fileName + ".png , didn't work");
      System.exit(-1);
    }
    int textureID = texture.getTextureID();
    textures.add(textureID);
    return textureID;
  }

  /** Delete Vertex Arrays, Buffers and Textures when the game is closed. (Clean up memory) */
  public void cleanUp() {
    for (int vao : vaos) {
      glDeleteVertexArrays(vao);
    }
    for (int vbo : vbos) {
      GL15.glDeleteBuffers(vbo);
    }
    for (int texture : textures) {
      glDeleteTextures(texture);
    }
  }

  /** Returns a new ID to store a Vertex Array in Memory and binds it. */
  private int createVao() {
    int vaoID = glGenVertexArrays();
    vaos.add(vaoID);
    glBindVertexArray(vaoID);
    return vaoID;
  }

  /**
   * Low level openGL functions to get the model data into openGL buffers.
   *
   * @param attributeNumber slot
   * @param coordinateSize dim
   * @param data one type of model data (vertices, normals or texture coords)
   */
  private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
    int vboID = GL15.glGenBuffers();
    vbos.add(vboID);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
    FloatBuffer buffer = storeDataInFloatBuffer(data);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
  }

  private void unbindVao() {
    glBindVertexArray(0);
  }

  /**
   * Load indices separately into an openGL buffer.
   *
   * @param indices indices for 3D model
   */
  private void bindIndicesBuffer(int[] indices) {
    int vboID = GL15.glGenBuffers();
    vbos.add(vboID);
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
    IntBuffer buffer = storeDataInIntBuffer(indices);
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
  }

  /**
   * Stores data into and int buffer and returns it.
   *
   * @param data any int data (vertices, normals, ...)
   * @return a flipped java buffer to be loaded into openGL
   */
  private IntBuffer storeDataInIntBuffer(int[] data) {
    IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }

  /**
   * Stores data into a float buffer and returns it.
   *
   * @param data any int data (vertices, normals, ...)
   * @return a flipped java buffer to be loaded into openGL
   */
  private FloatBuffer storeDataInFloatBuffer(float[] data) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }
}
