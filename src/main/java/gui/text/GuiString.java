package gui.text;

import engine.render.Loader;
import engine.render.fontmeshcreator.FontType;
import engine.render.fontmeshcreator.GuiText;
import engine.render.fontrendering.TextMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * A GUI Text with preloaded font. Not sure this is needed. Will revisit when we work on the HUD For
 * now its basically the same as GuiText
 */
public abstract class GuiString {

  private static FontType font;
  private GuiText guiText;
  private String text;
  private Vector2f position;
  private Vector3f textColour;
  private float alpha;
  private float fontSize;
  private boolean centered;
  private float maxLineLength;

  /** Generate a GUI String that can be rendered on the screen. */
  public GuiString() {
    position = new Vector2f(0, 0);
    textColour = new Vector3f(1, 1, 1);
    alpha = 0;
    centered = false;
    maxLineLength = 1f;
    fontSize = 1;
  }

  public static void loadFont(Loader loader) {
    font = new FontType(loader, "verdanaAsciiEx");
  }

  /**
   * Re-Creates the gui text. This needs to be called whenever the text (text) is changed
   * because we need to re-arrange the glyphs.
   */
  public void createGuiText() {
    setGuiText(
        new GuiText(
            text,
            getFontSize(),
            getFont(),
            getTextColour(),
            getAlpha(),
            getPosition(),
            getMaxLineLength(),
            isCentered()));
  }

  public void updateString() {
    if (getGuiText() != null) {
      TextMaster.removeText(getGuiText());
    }
    createGuiText();
  }

  public Vector2f getPosition() {
    return new Vector2f(position);
  }

  public void setPosition(Vector2f position) {
    this.position = position;
  }

  public FontType getFont() {
    return font;
  }

  public void setFont(FontType font) {
    GuiString.font = font;
  }

  public Vector3f getTextColour() {
    return textColour;
  }

  public void setTextColour(Vector3f textColour) {
    this.textColour = textColour;
  }

  public float getAlpha() {
    return alpha;
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  public float getFontSize() {
    return fontSize;
  }

  public void setFontSize(float fontSize) {
    this.fontSize = fontSize;
  }

  public GuiText getGuiText() {
    return guiText;
  }

  public void setGuiText(GuiText guiText) {
    this.guiText = guiText;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
    // updateString();
  }

  public boolean isCentered() {
    return centered;
  }

  public void setCentered(boolean centered) {
    this.centered = centered;
  }

  public float getMaxLineLength() {
    return maxLineLength;
  }

  public void setMaxLineLength(float maxLineLength) {
    this.maxLineLength = maxLineLength;
  }
}
