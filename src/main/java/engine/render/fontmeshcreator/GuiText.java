package engine.render.fontmeshcreator;

import engine.render.fontrendering.TextMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Represents a piece of text in the game.
 *
 * <p>We added the an alpha variable to be able to fade our text in and out. - Joe's Builder Corp.
 *
 * @author Karl
 */
public class GuiText {

  private final String textString;
  private final float fontSize;
  private final float lineMaxSize;
  private float alpha;
  private int textMeshVao;
  private int vertexCount;
  private Vector3f colour = new Vector3f(0f, 0f, 0f);
  private Vector2f position;
  private int numberOfLines;

  private FontType font;

  private boolean centerText;

  /**
   * Creates a new text, loads the text's quads into a VAO, and adds the text to the screen.
   *
   * @param text - the text.
   * @param fontSize - the font size of the text, where a font size of 1 is the default size.
   * @param font - the font that this text should use.
   * @param position - the position on the screen where the top left corner of the text should be
   *     rendered. The top left corner of the screen is (0, 0) and the bottom right is (1, 1).
   * @param maxLineLength - basically the width of the virtual page in terms of screen width (1 is
   *     full screen width, 0.5 is half the width of the screen, etc.) Text cannot go off the edge
   *     of the page, so if the text is longer than this length it will go onto the next line. When
   *     text is centered it is centered into the middle of the line, based on this line length
   *     value.
   * @param centered - whether the text should be centered or not.
   */
  @SuppressWarnings("unused")
  public GuiText(
      String text,
      float fontSize,
      FontType font,
      Vector2f position,
      float maxLineLength,
      boolean centered) {
    this(text, fontSize, font, new Vector3f(1, 1, 1), 1f, position, maxLineLength, centered);
  }

  /**
   * Creates a new text, loads the text's quads into a VAO, and adds the text to the screen.
   *
   * @param text - the text.
   * @param fontSize - the font size of the text, where a font size of 1 is the default size.
   * @param font - the font that this text should use.
   * @param colour - the colour that this text should use (r, g ,b).
   * @param alpha - the transparency of this text on the screen. 0 is invisible, 1 is solid. Can be
   *     changed each frame to simulate fading in and out.
   * @param position - the position on the screen where the top left corner of the text should be
   *     rendered. The top left corner of the screen is (0, 0) and the bottom right is (1, 1).
   * @param maxLineLength - basically the width of the virtual page in terms of screen width (1 is
   *     full screen width, 0.5 is half the width of the screen, etc.) Text cannot go off the edge
   *     of the page, so if the text is longer than this length it will go onto the next line. When
   *     text is centered it is centered into the middle of the line, based on this line length
   *     value.
   * @param centered - whether the text should be centered or not.
   */
  public GuiText(
      String text,
      float fontSize,
      FontType font,
      Vector3f colour,
      float alpha,
      Vector2f position,
      float maxLineLength,
      boolean centered) {
    this.textString = text;
    this.fontSize = fontSize;
    this.font = font;
    setColour(colour.x, colour.y, colour.z);
    setAlpha(alpha);
    this.position = position;
    this.lineMaxSize = maxLineLength;
    this.centerText = centered;
    // load text
    TextMaster.loadText(this);
  }

  /** Remove the text from the screen. */
  public void remove() {
    // remove text
    TextMaster.removeText(this);
  }

  /**
   * Get Font Type.
   *
   * @return The font used by this text.
   */
  public FontType getFont() {
    return font;
  }

  /**
   * Set the colour of the text.
   *
   * @param r - red value, between 0 and 1.
   * @param g - green value, between 0 and 1.
   * @param b - blue value, between 0 and 1.
   */
  @SuppressWarnings("WeakerAccess")
  public void setColour(float r, float g, float b) {
    colour.set(r, g, b);
  }

  /**
   * Get text colour.
   *
   * @return the colour of the text.
   */
  public Vector3f getColour() {
    return colour;
  }

  /**
   * Get Number of lines used by the text.
   *
   * @return The number of lines of text. This is determined when the text is loaded, based on the
   *     length of the text and the max line length that is set.
   */
  @SuppressWarnings("unused")
  public int getNumberOfLines() {
    return numberOfLines;
  }

  /**
   * Sets the number of lines that this text covers (method used only in loading).
   *
   * @param number number of lines the text should use.
   */
  void setNumberOfLines(int number) {
    this.numberOfLines = number;
  }

  /**
   * Get the position of the text.
   *
   * @return The position of the top-left corner of the text in screen-space. (0, 0) is the top left
   *     corner of the screen, (1, 1) is the bottom right.
   */
  public Vector2f getPosition() {
    return position;
  }

  public void setPosition(Vector2f position) {
    this.position = position;
  }

  /**
   * Get ID of the text's VAO.
   *
   * @return the ID of the text's VAO, which contains all the vertex data for the quads on which the
   *     text will be rendered.
   */
  public int getMesh() {
    return textMeshVao;
  }

  /**
   * Set the VAO and vertex count for this text.
   *
   * @param vao - the VAO containing all the vertex data for the quads on which the text will be
   *     rendered.
   * @param verticesCount - the total number of vertices in all of the quads.
   */
  public void setMeshInfo(int vao, int verticesCount) {
    this.textMeshVao = vao;
    this.vertexCount = verticesCount;
  }

  /**
   * Get total number of vertices.
   *
   * @return The total number of vertices of all the text's quads.
   */
  public int getVertexCount() {
    return this.vertexCount;
  }

  /**
   * Get font size of the text.
   *
   * @return the font size of the text (a font size of 1 is normal).
   */
  public float getFontSize() {
    return fontSize;
  }

  /**
   * Check if text should be centered.
   *
   * @return {@code true} if the text should be centered.
   */
  public boolean isCentered() {
    return centerText;
  }

  /**
   * Get maximum line length.
   *
   * @return The maximum length of a line of this text.
   */
  public float getMaxLineSize() {
    return lineMaxSize;
  }

  /**
   * Get the string of the text.
   *
   * @return The string of text.
   */
  String getTextString() {
    return textString;
  }

  public float getAlpha() {
    return alpha;
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }
}
