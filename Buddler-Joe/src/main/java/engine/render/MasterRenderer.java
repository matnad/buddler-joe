package engine.render;

import bin.Game;
import engine.io.Window;
import engine.models.TexturedModel;
import engine.shaders.StaticShader;
import engine.shaders.TerrainShader;
import entities.Camera;
import entities.Entity;
import entities.Light;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import terrains.TerrainFlat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class MasterRenderer {

    private static final float FOV = 60;
    private static final float NEAR_PLANE = .1f;
    private static final float FAR_PLANE = 180;

    private static final float RED = .0f;
    private static final float GREEN = .0f;
    private static final float BLUE = .0f;

    private static Matrix4f projectionMatrix;

    private Window window;

    private StaticShader shader;
    private EntityRenderer entityRenderer;

    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<TerrainFlat> terrains = new ArrayList<>();

    public MasterRenderer(Window window) {
        enableCulling();
        this.window = window;
        createProjectionMatrix();

        shader = new StaticShader();
        entityRenderer = new EntityRenderer(shader, projectionMatrix);

        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    public static void enableCulling() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public static void disableCulling() {
        glDisable(GL_CULL_FACE);
    }

    public void render(Light sun, Camera camera) {
        prepare();

        shader.start();
        shader.loadSkyColour(RED, GREEN, BLUE);
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        shader.stop();

        terrainShader.start();
        terrainShader.loadSkyColour(RED, GREEN, BLUE);
        terrainShader.loadLight(sun);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        terrains.clear();
        entities.clear();
    }

    public void processTerrain(TerrainFlat terrain) {
        terrains.add(terrain);
    }

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

    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
    }

    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED, GREEN, BLUE, 1f);
    }

    private static void createProjectionMatrix() {
        float aspectRatio = (float) Game.window.getWidth() / (float) Game.window.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix._m00(x_scale);
        projectionMatrix._m11(y_scale);
        projectionMatrix._m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        projectionMatrix._m23(-1);
        projectionMatrix._m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
        projectionMatrix._m33(0);
    }

    public static Matrix4f getProjectionMatrix() {
        return new Matrix4f().set(projectionMatrix);
    }
}
