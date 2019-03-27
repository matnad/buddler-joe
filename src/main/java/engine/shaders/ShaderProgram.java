package engine.shaders;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/** Abstract shader class. Has the functions to actually load values into the shader. */
public abstract class ShaderProgram {

  private static final String PATH_TO_GLSL = "/assets/glsl/";
  private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
  private final int programId;
  private final int vertexShaderId;
  private final int fragmentShaderId;

  /**
   * Initialize the VS and FS with the specified name. Load them in openGL and attach them to VS
   * resp. FS program
   *
   * <p>Then get UniformLocations (this is where variables we pass are stored and is defined in the
   * child classes)
   *
   * @param shaderName name of the shader. VS and FS need to have the same name!
   */
  public ShaderProgram(String shaderName) {
    String vertexFile = shaderName + ".vs";
    String fragmentFile = shaderName + ".fs";
    vertexShaderId = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
    fragmentShaderId = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
    programId = GL20.glCreateProgram();
    GL20.glAttachShader(programId, vertexShaderId);
    GL20.glAttachShader(programId, fragmentShaderId);
    bindAttributes();
    GL20.glLinkProgram(programId);
    GL20.glValidateProgram(programId);
    getAllUniformLocations();
  }

  /**
   * Read GLSL code file from the file system and tell openGL to compile it. Then return the ID of
   * the compiled and ready shader program.
   *
   * @param file GLSL file name on file system
   * @param type type of the shader (fragment or vertex)
   * @return ID of shader object in openGL
   */
  private static int loadShader(String file, int type) {
    StringBuilder shaderSource = new StringBuilder();
    try {
      InputStream in = ShaderProgram.class.getResourceAsStream(PATH_TO_GLSL + file);
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      String line;
      while ((line = reader.readLine()) != null) {
        shaderSource.append(line).append("//\n");
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    int shaderId = GL20.glCreateShader(type);
    GL20.glShaderSource(shaderId, shaderSource);
    GL20.glCompileShader(shaderId);
    if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
      System.out.println(GL20.glGetShaderInfoLog(shaderId, 500));
      System.err.println("Could not compile shader!");
      System.exit(-1);
    }
    return shaderId;
  }

  protected abstract void getAllUniformLocations();

  protected int getUniformLocation(String uniformName) {
    return GL20.glGetUniformLocation(programId, uniformName);
  }

  /** Called immediately before using the shader while rendering. */
  public void start() {
    GL20.glUseProgram(programId);
  }

  /** When done rendering with this shader. */
  public void stop() {
    GL20.glUseProgram(0);
  }

  /** Unbind both shaders. */
  public void cleanUp() {
    stop();
    GL20.glDetachShader(programId, vertexShaderId);
    GL20.glDetachShader(programId, fragmentShaderId);
    GL20.glDeleteShader(vertexShaderId);
    GL20.glDeleteShader(fragmentShaderId);
    GL20.glDeleteProgram(programId);
  }

  protected abstract void bindAttributes();

  /**
   * Bind shader attribute to openGL location.
   *
   * @param attribute slot
   * @param variableName name of the shader variable
   */
  protected void bindAttribute(int attribute, String variableName) {
    GL20.glBindAttribLocation(programId, attribute, variableName);
  }

  /*
  From here on are functions to load specific variable types to the shader in the specified
  location.
  They all do the same, but for different types.
   */
  protected void loadFloat(int location, float value) {
    GL20.glUniform1f(location, value);
  }

  void loadInt(int location, int value) {
    GL20.glUniform1i(location, value);
  }

  protected void loadVector(int location, Vector3f vector) {
    GL20.glUniform3f(location, vector.x, vector.y, vector.z);
  }

  // protected void loadVector(int location, Vector4f vector) {
  //  GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
  // }

  protected void load2DVector(int location, Vector2f vector) {
    GL20.glUniform2f(location, vector.x, vector.y);
  }

  void loadBoolean(int location, boolean value) {
    float toLoad = 0;
    if (value) {
      toLoad = 1;
    }
    GL20.glUniform1f(location, toLoad);
  }

  protected void loadMatrix(int location, Matrix4f matrix) {
    matrix.get(matrixBuffer);
    if (location != -1) {
      glUniformMatrix4fv(location, false, matrixBuffer);
    }
  }
}
