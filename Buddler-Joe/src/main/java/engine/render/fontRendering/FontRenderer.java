package engine.render.fontRendering;


import engine.render.fontMeshCreator.FontType;
import engine.render.fontMeshCreator.GUIText;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class FontRenderer {

	private FontShader shader;

	public FontRenderer() {
		shader = new FontShader();
	}

	public void render (Map<FontType, List<GUIText>> texts) {
		prepare();
		for (FontType fontType : texts.keySet()) {
			//For each font atlas, bind the font atlas and then render all texts in that font
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, fontType.getTextureAtlas());
			for (GUIText guiText : texts.get(fontType)) {
				renderText(guiText);
			}
		}
		endRendering();
	}

	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare(){
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		shader.start();
	}
	
	private void renderText(GUIText text){
		//Bind
		glBindVertexArray(text.getMesh());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		//Pass variables to shader
		shader.loadColour(text.getColour());
		shader.loadTranslation(text.getPosition());
		shader.loadAlpha(text.getAlpha());
		//Render Text
		glDrawArrays(GL_TRIANGLES, 0, text.getVertexCount());
		//Unbind
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}
	
	private void endRendering(){
		shader.stop();
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
	}

}
