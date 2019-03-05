package engine.particles;

import java.util.List;
import java.util.Map;

import engine.models.RawModel;
import engine.render.Loader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import entities.Camera;
import org.lwjgl.openvr.RenderModelTextureMap;
import util.Maths;

public class ParticleRenderer {
	
	private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
	
	private RawModel quad;
	private ParticleShader shader;
	
	protected ParticleRenderer(Loader loader, Matrix4f projectionMatrix){
		quad = loader.loadToVAO(VERTICES);
		shader = new ParticleShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();



	}
	
	protected void render(Map<ParticleTexture, List<Particle>> particles, Camera camera){
		if(particles.size() == 0)
			return;
		Matrix4f viewMatrix = Maths.createViewMatrix();
		prepare();
		for (ParticleTexture particleTexture : particles.keySet()) {
			if(particleTexture.isAdditive()) {
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			} else {
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, particleTexture.getTextureID());
			for (Particle particle : particles.get(particleTexture)) {
				updateModelViewMatrix(particle.getPosition(), particle.getRotation(), particle.getScale(), new Matrix4f().set(viewMatrix));
				shader.loadTextureCoordInfo(particle.getTexOffset1(), particle.getTexOffset2(), particleTexture.getNumberOfRows(), particle.getBlend());
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
			}
			finishRendering();
		}
	}

	//The code below is for the updateModelViewMatrix() method
	//modelMatrix.m00 = viewMatrix.m00;
	//modelMatrix.m01 = viewMatrix.m10;
	//modelMatrix.m02 = viewMatrix.m20;
	//modelMatrix.m10 = viewMatrix.m01;
	//modelMatrix.m11 = viewMatrix.m11;
	//modelMatrix.m12 = viewMatrix.m21;
	//modelMatrix.m20 = viewMatrix.m02;
	//modelMatrix.m21 = viewMatrix.m12;
	//modelMatrix.m22 = viewMatrix.m22;

	protected void cleanUp(){
		shader.cleanUp();
	}

	private void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix) {
		Matrix4f modelMatrix = new Matrix4f()
				.translate(position);

		//Cancel rotation
		modelMatrix._m00(viewMatrix.m00());
		modelMatrix._m01(viewMatrix.m10());
		modelMatrix._m02(viewMatrix.m20());
		modelMatrix._m10(viewMatrix.m01());
		modelMatrix._m11(viewMatrix.m11());
		modelMatrix._m12(viewMatrix.m21());
		modelMatrix._m20(viewMatrix.m02());
		modelMatrix._m21(viewMatrix.m12());
		modelMatrix._m22(viewMatrix.m22());

		//New Rotation
		modelMatrix
				.rotateZ((float) Math.toRadians(rotation))
				.scale(scale);

		Matrix4f modelViewMatrix = viewMatrix
				.mul(modelMatrix);

		shader.loadModelViewMatrix(modelViewMatrix);
	}

	private void prepare(){
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
	}
	
	private void finishRendering(){
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		shader.stop();
	}

}
