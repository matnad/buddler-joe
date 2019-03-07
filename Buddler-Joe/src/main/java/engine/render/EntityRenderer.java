package engine.render;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.shaders.StaticShader;
import engine.textures.ModelTexture;
import entities.Entity;
import org.joml.Matrix4f;
import util.Maths;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * Renders models defined by the Entity class.
 *
 * Gets a list of entities from the Master renderer that is pre-processed and with the general openGL settings
 * correctly enabled. This class does the rest required to render the entities.
 */
public class EntityRenderer {

    private StaticShader shader;

    /**
     * This is called form the master renderer. Passes correct shader and projection matrix for the shader
     * @param shader Terrain Shader
     * @param projectionMatrix matrix for shader
     */
    EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }


    /**
     * Renders entities by model. Will prepare a model, then render all entities of that model, then load the next
     * model. This is standard optimization so we have to load each model only once.
     *
     * @param entities A map that has a list of entities for each model
     */
    public void render(Map<TexturedModel, List<Entity>> entities) {
        for (TexturedModel texturedModel : entities.keySet()) {
            //Loop through models and prepare the model
            prepareTexturedModels(texturedModel);
            List<Entity> batch = entities.get(texturedModel);
            for (Entity entity : batch) {
                //Loop through entities of that model and render them
                prepareInstance(entity);
                glDrawElements(GL_TRIANGLES, texturedModel.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel(); //Unbind model
        }
    }

    /**
     * Binds model and texture. Do everything that needs to be done once per model.
     * Also disables culling if the texture is transparent to make sure we only render the backside when we see it.
     *
     * Also loads variables for the model that are the same across all entities to the shader
     *
     * @param model model to prepare for rendering
     */
    private void prepareTexturedModels(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        glBindVertexArray(rawModel.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        ModelTexture texture = model.getTexture();
        shader.loadNumberOfRows(texture.getNumberOfRows());
        if(texture.isHasTransparency())
            MasterRenderer.disableCulling();
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, model.getTexture().getID());
    }

    /**
     * Unbind the model from openGL
     */
    private void unbindTexturedModel(){
        MasterRenderer.enableCulling();
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    /**
     * Load entity specific variables to the shader. These values are different for each individual entity.
     *
     * @param entity the entity to render
     */
    private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }

}
