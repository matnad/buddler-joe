package engine.render.fontrendering;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import engine.render.fontmeshcreator.FontType;
import engine.render.fontmeshcreator.GuiText;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class FontRenderer {

  private final FontShader shader;

  FontRenderer() {
    shader = new FontShader();
  }

  /**
   * Render the passed list of texts.
   *
   * @param texts list of texts to render
   */
  public void render(Map<FontType, CopyOnWriteArrayList<GuiText>> texts) {
    prepare();
    for (FontType fontType : texts.keySet()) {
      // For each font atlas, bind the font atlas and then render all texts in that font
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D, fontType.getTextureAtlas());
      for (GuiText guiText : texts.get(fontType)) {
        renderText(guiText);
      }
    }
    endRendering();
  }

  /** Clean up memory. */
  void cleanUp() {
    shader.cleanUp();
  }

  /** Set up openGL parameters for text rendering. */
  private void prepare() {
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glDisable(GL_DEPTH_TEST);
    shader.start();
  }

  /**
   * Load variables to shader and bind/enable VAOs, then draw the objects on screen.
   *
   * @param text text to render
   */
  private void renderText(GuiText text) {
    // Bind
    glBindVertexArray(text.getMesh());
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    // Pass variables to shader
    shader.loadColour(text.getColour());
    shader.loadTranslation(text.getPosition());
    shader.loadAlpha(text.getAlpha());
    // Render Text
    glDrawArrays(GL_TRIANGLES, 0, text.getVertexCount());
    // Unbind
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);
  }

  /** Set openGL parameters back to project level defaults. */
  private void endRendering() {
    shader.stop();
    glDisable(GL_BLEND);
    glEnable(GL_DEPTH_TEST);
  }
}
