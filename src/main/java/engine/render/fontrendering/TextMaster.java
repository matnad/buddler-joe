package engine.render.fontrendering;

import engine.render.Loader;
import engine.render.fontmeshcreator.FontType;
import engine.render.fontmeshcreator.GuiText;
import engine.render.fontmeshcreator.TextMeshData;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manage the text objects that appear on screen, organized by font type. When a new text object is
 * added, the text mesh is loaded and a VAO is generated to store the text model. Will pass the text
 * objects to the renderer.
 */
public class TextMaster {

  private static final Map<FontType, CopyOnWriteArrayList<GuiText>> texts =
      new ConcurrentHashMap<>();
  private static Loader loader;
  private static FontRenderer renderer;

  public static void init(Loader theLoader) {
    renderer = new FontRenderer();
    loader = theLoader;
  }

  public static void render() {
    renderer.render(texts);
  }

  /**
   * Load text to a VAO and store the ID. The text can now be rendered.
   *
   * @param text guitext to load
   */
  public static void loadText(GuiText text) {
    FontType font = text.getFont();
    TextMeshData data = font.loadText(text);
    int vao = loader.loadToVao(data.getVertexPositions(), data.getTextureCoords());
    text.setMeshInfo(vao, data.getVertexCount());
    List<GuiText> textBatch = texts.computeIfAbsent(font, k -> new CopyOnWriteArrayList<>());
    textBatch.add(text);
  }

  /**
   * Removes a GuiText. This will no longer render it and eventually collect it from memory.
   *
   * @param text GuiText object to remove
   */
  public static void removeText(GuiText text) {
    List<GuiText> textBatch = texts.get(text.getFont());
    if (textBatch == null) {
      return;
    }
    textBatch.remove(text);
    if (textBatch.isEmpty()) {
      texts.remove(text.getFont());
    }
  }

  /** Remove ALL texts from the render list. Call this to hard-reset the interface. */
  public static void removeAll() {
    for (CopyOnWriteArrayList<GuiText> t : texts.values()) {
      for (GuiText guiText : t) {
        removeText(guiText);
      }
    }
  }

  public static void cleanUp() {
    renderer.cleanUp();
  }

  // public static Map<FontType, List<GuiText>> getTexts() {
  //  return texts;
  // }
}
