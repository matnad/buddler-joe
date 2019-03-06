package engine.render.fontRendering;

import engine.shaders.ShaderProgram;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Shader Program
 * Passes variables to the shader
 */
public class FontShader extends ShaderProgram {

	private static final String SHADER_NAME = "font";

	private int location_colour;
	private int location_translation;
	private int location_alpha;

	
	public FontShader() {
		super(SHADER_NAME);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colour = super.getUniformLocation("colour");
		location_translation = super.getUniformLocation("translation");
		location_alpha = super.getUniformLocation("alpha");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	protected void loadColour(Vector3f colour) {
		super.loadVector(location_colour, colour);
	}

	protected void loadTranslation(Vector2f translation) {
		super.load2DVector(location_translation, translation);
	}

	protected void loadAlpha(float alpha) {
		super.loadFloat(location_alpha, alpha);
	}


}
