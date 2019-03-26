package engine.shaders;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import engine.io.InputHandler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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

  private final int programID;
  private final int vertexShaderID;
  private final int fragmentShaderID;

  private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

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
    vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
    fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
    programID = GL20.glCreateProgram();
    GL20.glAttachShader(programID, vertexShaderID);
    GL20.glAttachShader(programID, fragmentShaderID);
    bindAttributes();
    GL20.glLinkProgram(programID);
    GL20.glValidateProgram(programID);
    getAllUniformLocations();
  }

  protected abstract void getAllUniformLocations();

  protected int getUniformLocation(String uniformName) {
    return GL20.glGetUniformLocation(programID, uniformName);
  }

  /** Called immediately before using the shader while rendering. */
  public void start() {
    GL20.glUseProgram(programID);
  }

  /** When done rendering with this shader. */
  public void stop() {
    GL20.glUseProgram(0);
  }

  /** Unbind both shaders. */
  public void cleanUp() {
    stop();
    GL20.glDetachShader(programID, vertexShaderID);
    GL20.glDetachShader(programID, fragmentShaderID);
    GL20.glDeleteShader(vertexShaderID);
    GL20.glDeleteShader(fragmentShaderID);
    GL20.glDeleteProgram(programID);
  }

  protected abstract void bindAttributes();

  /**
   * Bind shader attribute to openGL location.
   *
   * @param attribute slot
   * @param variableName name of the shader variable
   */
  protected void bindAttribute(int attribute, String variableName) {
    GL20.glBindAttribLocation(programID, attribute, variableName);
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
    int shaderID = GL20.glCreateShader(type);
    GL20.glShaderSource(shaderID, shaderSource);
    GL20.glCompileShader(shaderID);
    if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
      System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
      System.err.println("Could not compile shader!");
      System.exit(-1);
    }
    return shaderID;
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
