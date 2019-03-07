package engine.shaders;


import org.joml.Matrix4f;

/**
 * Shader programm for gui
 * Just passing some variables to the shader
 */
public class GuiShader extends ShaderProgram{

    private static final String SHADER_NAME = "gui";

    private int location_transformationMatrix;
    private int location_alpha;


    public GuiShader() {
        super(SHADER_NAME);
    }

    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadAlpha(float alpha) {
        super.loadFloat(location_alpha, alpha);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_alpha = super.getUniformLocation("alpha");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }




}
