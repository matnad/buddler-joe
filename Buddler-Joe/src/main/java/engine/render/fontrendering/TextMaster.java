package engine.render.fontrendering;

import engine.render.Loader;
import engine.render.fontmeshcreator.FontType;
import engine.render.fontmeshcreator.GuiText;
import engine.render.fontmeshcreator.TextMeshData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage the text objects that appear on screen, organized by font type.
 * When a new text object is added, the text mesh is loaded and a VAO is generated to store the
 * text model.
 * Will pass the text objects to the renderer.
 */
public class TextMaster {

  private static Loader loader;
  private static final Map<FontType, List<GuiText>> texts = new HashMap<>();
  private static FontRenderer renderer;

  public static void init(Loader theLoader) {
    renderer = new FontRenderer();
    loader = theLoader;
  }

  public static void render() {
    renderer.render(texts);
  }

  /**
   * Load text to a VAO and store the ID.
   * The text can now be rendered.
   */
  public static void loadText(GuiText text) {
    FontType font = text.getFont();
    TextMeshData data = font.loadText(text);
    int vao = loader.loadToVao(data.getVertexPositions(), data.getTextureCoords());
    text.setMeshInfo(vao, data.getVertexCount());
    List<GuiText> textBatch = texts.computeIfAbsent(font, k -> new ArrayList<>());
    textBatch.add(text);
  }

  /**
   * Removes a GuiText.
   * This will no longer render it and eventually collect it from memory.
   *
   * @param text GuiText object to remove
   */
  public static void removeText(GuiText text) {
    List<GuiText> textBatch = texts.get(text.getFont());
    textBatch.remove(text);
    if (textBatch.isEmpty()) {
      texts.remove(text.getFont());
    }
  }

  public static void cleanUp() {
    renderer.cleanUp();
  }


  //public static Map<FontType, List<GuiText>> getTexts() {
  //  return texts;
  //}
}
