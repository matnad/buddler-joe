package engine.render.fontmeshcreator;

/**
 * Simple data structure class holding information about a certain glyph in the font texture atlas.
 * All sizes are for a font-size of 1.
 *
 * @author Karl
 */
class Character {

  private final int id;
  private final double textureCoordX;
  private final double textureCoordY;
  private final double maxTextureCoordX;
  private final double maxTextureCoordY;
  private final double xoffset;
  private final double yoffset;
  private final double sizeX;
  private final double sizeY;
  private final double advanceX;

  /**
   * Create a new Character.
   *
   * @param id - the ASCII value of the character.
   * @param textureCoordX - the x texture coordinate for the top left corner of the character in the
   *     texture atlas.
   * @param textureCoordY - the y texture coordinate for the top left corner of the character in the
   *     texture atlas.
   * @param texSizeX - the width of the character in the texture atlas.
   * @param texSizeY - the height of the character in the texture atlas.
   * @param xoffset - the x distance from the cursor to the left edge of the character's quad.
   * @param yoffset - the y distance from the cursor to the top edge of the character's quad.
   * @param sizeX - the width of the character's quad in screen space.
   * @param sizeY - the height of the character's quad in screen space.
   * @param advanceX - how far in pixels the cursor should advance after adding this character.
   */
  Character(
      int id,
      double textureCoordX,
      double textureCoordY,
      double texSizeX,
      double texSizeY,
      double xoffset,
      double yoffset,
      double sizeX,
      double sizeY,
      double advanceX) {
    this.id = id;
    this.textureCoordX = textureCoordX;
    this.textureCoordY = textureCoordY;
    this.xoffset = xoffset;
    this.yoffset = yoffset;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.maxTextureCoordX = texSizeX + textureCoordX;
    this.maxTextureCoordY = texSizeY + textureCoordY;
    this.advanceX = advanceX;
  }

  int getId() {
    return id;
  }

  double getTextureCoordX() {
    return textureCoordX;
  }

  double getTextureCoordY() {
    return textureCoordY;
  }

  double getXMaxTextureCoord() {
    return maxTextureCoordX;
  }

  double getYMaxTextureCoord() {
    return maxTextureCoordY;
  }

  double getXoffset() {
    return xoffset;
  }

  double getYoffset() {
    return yoffset;
  }

  double getSizeX() {
    return sizeX;
  }

  double getSizeY() {
    return sizeY;
  }

  double getAdvanceX() {
    return advanceX;
  }
}
