package engine.render.fontmeshcreator;

import engine.render.Loader;

/**
 * Represents a font. It holds the font's texture atlas as well as having the ability to create the
 * quad vertices for any text using this font.
 *
 * @author Karl
 */
public class FontType {

  private final int textureAtlas;
  private final TextMeshCreator loader;

  /**
   * Creates a new font and loads up the data about each character from the font file.
   *
   * @param loader main loader
   * @param fontFileName file name without path or extension
   */
  public FontType(Loader loader, String fontFileName) {

    this.textureAtlas = loader.loadFontTexture(fontFileName);
    this.loader = new TextMeshCreator(fontFileName);
  }

  /**
   * Get font texture atlas.
   *
   * @return The font texture atlas.
   */
  public int getTextureAtlas() {
    return textureAtlas;
  }

  /**
   * Takes in an unloaded text and calculate all of the vertices for the quads on which this text
   * will be rendered. The vertex positions and texture coords and calculated based on the
   * information from the font file.
   *
   * @param text - the unloaded text.
   * @return Information about the vertices of all the quads.
   */
  public TextMeshData loadText(GuiText text) {
    return loader.createTextMesh(text);
  }
}
