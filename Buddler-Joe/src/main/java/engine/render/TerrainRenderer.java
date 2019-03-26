package engine.render;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_TRIANGLES;
import static org.lwjgl.opengl.GL13.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL13.glBindTexture;
import static org.lwjgl.opengl.GL13.glDrawElements;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import engine.models.RawModel;
import engine.shaders.TerrainShader;
import engine.textures.TerrainTexturePack;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import terrains.TerrainFlat;
import util.Maths;

/**
 * Renders Terrains. Gets a list of terrain from the Master Renderer that is pre-processed and with
 * the general openGL settings correctly enabled. This class does the rest required to render the
 * terrain.
 */
public class TerrainRenderer {

  private final TerrainShader shader;

  /**
   * This is called form the master renderer. Passes correct shader and projection matrix for the
   * shader
   *
   * @param shader Terrain Shader
   * @param projectionMatrix matrix for shader
   */
  TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
    this.shader = shader;
    shader.start();
    shader.loadProjectionMatrix(projectionMatrix);

    // This is to load the positions of the textures into the shader so we can blend them
    // according to the blend map
    shader.connectTextureUnits();

    shader.stop();
  }

  /**
   * Bind, Render and Unbind Model.
   *
   * @param terrains pre-processed terrains from the Master Renderer
   */
  public void render(List<TerrainFlat> terrains) {
    for (TerrainFlat terrain : terrains) {
      prepareTerrain(terrain); // Bind Model
      loadModelMatrix(terrain); // Pass matrix to shader

      // Render Terrain
      glDrawElements(GL_TRIANGLES, terrain.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);

      unbindTexturedModel(); // Unbind
    }
  }

  /**
   * Load properties of the terrain into shader and bind model.
   *
   * @param terrain terrain to prepare for rendering
   */
  private void prepareTerrain(TerrainFlat terrain) {
    RawModel rawModel = terrain.getModel();
    glBindVertexArray(rawModel.getVaoId());
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    glEnableVertexAttribArray(2);
    bindTextures(terrain);
    shader.loadShineVariables(1, 0);
  }

  /**
   * Bind textures in specific slots. The blending happens in the fragment shader.
   *
   * @param terrain Terrain to render
   */
  private void bindTextures(TerrainFlat terrain) {
    TerrainTexturePack texturePack = terrain.getTexturePack();
    // Here we actually load the textures in slots 0-4. We pass this info to the shader in the
    // constructor
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureId());
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, texturePack.getTextureR().getTextureId());
    glActiveTexture(GL_TEXTURE2);
    glBindTexture(GL_TEXTURE_2D, texturePack.getTextureG().getTextureId());
    glActiveTexture(GL_TEXTURE3);
    glBindTexture(GL_TEXTURE_2D, texturePack.getTextureB().getTextureId());
    glActiveTexture(GL_TEXTURE4);
    glBindTexture(GL_TEXTURE_2D, terrain.getBlendMap().getTextureId());
  }

  /** Cleanup. */
  private void unbindTexturedModel() {
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glDisableVertexAttribArray(2);
    glBindVertexArray(0);
  }

  /**
   * Transformation is done in the shader. THis loads the transformation matrix to the shader.
   *
   * @param terrain terrain to render
   */
  private void loadModelMatrix(TerrainFlat terrain) {
    Matrix4f transformationMatrix =
        Maths.createTransformationMatrix(
            new Vector3f(terrain.getCoordX(), 0, terrain.getCoordZ()),
            terrain.getRotation().x,
            terrain.getRotation().y,
            terrain.getRotation().z,
            new Vector3f(1, 1, 1));
    shader.loadTransformationMatrix(transformationMatrix);
  }
}
