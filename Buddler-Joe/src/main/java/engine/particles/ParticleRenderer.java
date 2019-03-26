package engine.particles;

import engine.models.RawModel;
import engine.render.Loader;
import entities.Camera;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import util.Maths;

public class ParticleRenderer {

  // A simple quad
  private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};

  private final RawModel quad;
  private final ParticleShader shader;

  /**
   * Is used by the Particle Master to render particles.
   *
   * @param loader main loader
   * @param projectionMatrix projection matrix for shader
   */
  ParticleRenderer(Loader loader, Matrix4f projectionMatrix) {
    quad = loader.loadToVao(VERTICES); // Generate the raw model with the quad vertices
    shader = new ParticleShader();
    shader.start();
    shader.loadProjectionMatrix(projectionMatrix); // This doesnt change
    shader.stop();
  }

  /** Render all particles. Called from the Particle Master only! */
  // TODO: createViewMatrix is still taking a static camera. Change to nonstatic.
  protected void render(Map<ParticleTexture, List<Particle>> particles, Camera camera) {
    if (particles.size() == 0) {
      return;
    }
    Matrix4f viewMatrix = Maths.createViewMatrix(camera);
    prepare();
    for (ParticleTexture particleTexture : particles.keySet()) {
      // Loop over particle types and check if we want additive blending or not (on a type per
      // type basis)
      if (particleTexture.isAdditive()) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
      } else {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      }
      // Bind Texture
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, particleTexture.getTextureID());
      for (Particle particle : particles.get(particleTexture)) {
        // Rotate the particle to face the camera
        updateModelViewMatrix(
            particle.getPosition(),
            particle.getRotation(),
            particle.getScale(),
            new Matrix4f().set(viewMatrix)); // new().set() to avoid changing the viewMatrix itself.

        // Pass information to the shaders
        shader.loadTextureCoordInfo(
            particle.getTexOffset1(),
            particle.getTexOffset2(),
            particleTexture.getNumberOfRows(),
            particle.getBlend());

        // Draw the actual particle
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
      }
    }
    finishRendering();
  }

  void cleanUp() {
    shader.cleanUp();
  }

  /*
   * This calculation needs to be done for every particle in every frame and is currently done
   * all on the CPU.
   * TODO: It might be more effective to move (part of) this onto the shader?
   * If we ever run into performance problems, this is one of the functions we can probably
   * optimize, but I want to
   * avoid spending too much time on premature optimization.
   */
  private void updateModelViewMatrix(
      Vector3f position, float rotation, float scale, Matrix4f viewMatrix) {
    Matrix4f modelMatrix = new Matrix4f().translate(position);

    // Cancel rotation
    modelMatrix._m00(viewMatrix.m00());
    modelMatrix._m01(viewMatrix.m10());
    modelMatrix._m02(viewMatrix.m20());
    modelMatrix._m10(viewMatrix.m01());
    modelMatrix._m11(viewMatrix.m11());
    modelMatrix._m12(viewMatrix.m21());
    modelMatrix._m20(viewMatrix.m02());
    modelMatrix._m21(viewMatrix.m12());
    modelMatrix._m22(viewMatrix.m22());

    // New Rotation
    modelMatrix.rotateZ((float) Math.toRadians(rotation)).scale(scale);

    // This command here would probably be best to move onto a shader
    Matrix4f modelViewMatrix = viewMatrix.mul(modelMatrix);

    // Pass the newly calculated view * model matrix to the shader
    shader.loadModelViewMatrix(modelViewMatrix);
  }

  private void prepare() {
    shader.start();
    GL30.glBindVertexArray(quad.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glDepthMask(false); // Turn of DepthMask or we get artifacts when rendering
  }

  private void finishRendering() {
    GL11.glDepthMask(true); // Turn DepthMask back on
    GL11.glDisable(GL11.GL_BLEND);
    GL20.glDisableVertexAttribArray(0);
    shader.stop();
  }
}
