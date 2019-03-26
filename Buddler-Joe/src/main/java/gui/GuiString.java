package gui;

import engine.render.Loader;
import engine.render.fontmeshcreator.FontType;
import engine.render.fontmeshcreator.GuiText;
import java.io.File;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * A GUI Text with preloaded font. Not sure this is needed.
 * Will revisit when we work on the HUD
 * For now its basically the same as GuiText
 */
public abstract class GuiString {

  private GuiText guiString;

  private String guiStringString;
  private Vector2f position;
  private static FontType font;
  private Vector3f textColour;
  private float alpha;
  private float fontSize;
  private boolean centered;
  private float maxLineLength;

  /**
   * Generate a GUI String that can be rendered on the screen.
   */
  public GuiString() {
    position = new Vector2f(0, 0);
    textColour = new Vector3f(1, 1, 1);
    alpha = 0;
    centered = false;
    maxLineLength = 1f;
  }

  public static void loadFont(Loader loader) {
    font = new FontType(loader.loadFontTexture("verdana"),
        new File("src/main/resources/assets/fonts/verdana.fnt"));
  }

  public void createGuiText() {
    setGuiString(new GuiText(guiStringString, 1f, getFont(), getTextColour(), getAlpha(),
        getPosition(), getMaxLineLength(), isCentered()));
  }

  public void updateString() {
    createGuiText();
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  public void setPosition(Vector2f position) {
    this.position = position;
  }

  public Vector2f getPosition() {
    return position;
  }

  public void setTextColour(Vector3f textColour) {
    this.textColour = textColour;
  }

  public FontType getFont() {
    return font;
  }

  public Vector3f getTextColour() {
    return textColour;
  }

  public float getAlpha() {
    return alpha;
  }

  public void setFont(FontType font) {
    GuiString.font = font;
  }

  public float getFontSize() {
    return fontSize;
  }

  public void setFontSize(float fontSize) {
    this.fontSize = fontSize;
  }

  public GuiText getGuiString() {
    return guiString;
  }

  public void setGuiString(GuiText guiString) {
    this.guiString = guiString;
  }

  public String getGuiStringString() {
    return guiStringString;
  }

  public void setGuiStringString(String guiStringString) {
    this.guiStringString = guiStringString;
    //updateString();
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
