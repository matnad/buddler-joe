package engine.render;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import engine.models.TexturedModel;
import engine.shaders.StaticShader;
import engine.shaders.TerrainShader;
import entities.Camera;
import entities.Entity;
import entities.light.Light;
import game.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import terrains.TerrainFlat;

/**
 * Is master over general render settings. -(static) projection matrix - FOV - Sight distance - Sky
 * colour
 *
 * <p>Collects items to render, starts/stops shaders, loads some variables to shaders and passes
 * priority to - Entity renderer - Terrain renderer TODO: Include control flow for Gui and Particle
 * renderers here
 */
public class MasterRenderer {

  private static final float FOV = 60;
  private static final float NEAR_PLANE = .1f;
  private static final float FAR_PLANE = 500;

  private static final float RED = .0f;
  private static final float GREEN = .0f;
  private static final float BLUE = .0f;

  private static Matrix4f projectionMatrix;

  private final StaticShader staticShader;
  private final EntityRenderer entityRenderer;

  private final TerrainRenderer terrainRenderer;
  private final TerrainShader terrainShader;

  private final Map<TexturedModel, List<Entity>> entities = new HashMap<>();
  private final List<TerrainFlat> terrains = new ArrayList<>();

  /** Initialize Master renderer. Only needs to be once at the start. */
  public MasterRenderer() {
    enableCulling(); // Don't render the "backside" of objects that we can't see
    createProjectionMatrix();

    staticShader = new StaticShader();
    entityRenderer = new EntityRenderer(staticShader, projectionMatrix);

    terrainShader = new TerrainShader();
    terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
  }

  /**
   * Enables culling. Culling is the default for almost all models, except for models with
   * transparency Culling doesn't render the "backside" of objects that we can't see
   */
  static void enableCulling() {
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
  }

  /**
   * Disables culling. Culling is the default for almost all models, except for models with
   * transparency Culling doesn't render the "backside" of objects that we can't see
   */
  static void disableCulling() {
    glDisable(GL_CULL_FACE);
  }

  /**
   * Projection Matrix to transform Camera Coordinates to Screen Coordinates. The inverse of this
   * matrix is used to turn Screen Coordinates to Camera Coordinates This is static until either
   * FOV, render distance (Far-/NearPlane) or screen resolution change
   */
  private static void createProjectionMatrix() {
    float aspectRatio = (float) Game.window.getWidth() / (float) Game.window.getHeight();
    float scaleY = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
    float scaleX = scaleY / aspectRatio;
    final float frustumLength = FAR_PLANE - NEAR_PLANE;

    projectionMatrix = new Matrix4f(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    projectionMatrix._m00(scaleX);
    projectionMatrix._m11(scaleY);
    projectionMatrix._m22(-((FAR_PLANE + NEAR_PLANE) / frustumLength));
    projectionMatrix._m23(-1);
    projectionMatrix._m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustumLength));
    projectionMatrix._m33(0);
  }

  public static Matrix4f getProjectionMatrix() {
    return new Matrix4f(projectionMatrix);
  }

  /**
   * Start/stop shaders and pass rendering priority for entities and terrains.
   *
   * @param lights list of lights to render
   * @param camera active camera, used to generate view matrix
   */
  @SuppressWarnings("Duplicates")
  public void render(List<Light> lights, Camera camera) {
    prepare();

    // Render static entities.
    staticShader.start();
    staticShader.loadSkyColour(RED, GREEN, BLUE); // Pass sky colour to the shader
    staticShader.loadLights(lights); // Pass light colour and position to the shader
    staticShader.loadViewMatrix(camera); // Pass view matrix to the shader
    entityRenderer.render(entities); // entityRenderer handles the detailed rendering process
    staticShader.stop();

    // Render the terrain, comments are equivalent to static entity renderer above
    terrainShader.start();
    terrainShader.loadSkyColour(RED, GREEN, BLUE);
    terrainShader.loadLights(lights);
    terrainShader.loadViewMatrix(camera);
    terrainRenderer.render(terrains);
    terrainShader.stop();

    terrains.clear();
    entities.clear();
  }

  /**
   * Adds terrains to the queue to be rendered.
   *
   * @param terrain Terrain to be rendered
   */
  public void processTerrain(TerrainFlat terrain) {
    if (terrain != null) {
      terrains.add(terrain);
    }
  }

  /**
   * Adds entity to queue to be rendered. Entities are grouped by model so we render all instances
   * of the same model without unbinding. Makes rendering a lot of blocks much smoother!
   *
   * @param entity Entity to be rendered
   */
  public void processEntity(Entity entity) {
    TexturedModel entityModel = entity.getModel();
    List<Entity> batch = entities.get(entityModel);
    if (batch != null) {
      batch.add(entity);
    } else {
      List<Entity> newBatch = new ArrayList<>();
      newBatch.add(entity);
      entities.put(entityModel, newBatch);
    }
  }

  /** Unbinds shaders in openGL. */
  public void cleanUp() {
    staticShader.cleanUp();
    terrainShader.cleanUp();
  }

  /** Set rendering constants and clears color buffers. */
  private void prepare() {
    glEnable(GL_DEPTH_TEST);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    GL11.glClearColor(RED, GREEN, BLUE, 1f);
  }
}
