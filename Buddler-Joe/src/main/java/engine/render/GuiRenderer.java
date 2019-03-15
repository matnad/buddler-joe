package engine.render;

import engine.models.RawModel;
import engine.shaders.GuiShader;
import gui.GuiTexture;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import util.Maths;

import java.util.List;

/**
 * Renders HUD and GUI elements
 * They are simple quads rendered directly onto the screen coordinates
 * No transformations are necessary except translation and scale by the gui position and scale
 * A simple 2D to 2D mapping
 */
public class GuiRenderer {

    private final RawModel quad;
    private GuiShader shader;

    /**
     * Needs to be created once.
     * @param loader Pass the main loader from the Playing class. There is no reason to have more than one loader.
     */
    public GuiRenderer(Loader loader){
        float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1}; //simple quad, full screen
        quad = loader.loadToVAO(positions);
        shader = new GuiShader();
    }

    /**
     * Renders a list of guis. Will enable and disable all openGL settings as required.
     * For details see comments in the code.
     *
     * @param guis list of guis to render
     */
    public void render(List<GuiTexture> guis){
        shader.start();
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); //For transparency options
        GL11.glDisable(GL11.GL_DEPTH_TEST); //Guis are in front of the world
        for(GuiTexture gui: guis){
            //Bind gui texture
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());

            //Load matrix into the shader to transform the fullscreen quad to the right position and size
            Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
            shader.loadTransformation(matrix);
            shader.loadAlpha(gui.getAlpha()); //pass alpha/transparency value of the gui

            //Draw the picutre
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        ///Unbind and stop (clean up shader)
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    /**
     * Unbinds shaders in openGL
     */
    public void cleanUp(){
        shader.cleanUp();
    }
}
