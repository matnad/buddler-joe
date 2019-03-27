package engine.render.fontmeshcreator;

import java.util.ArrayList;
import java.util.List;

/**
 * During the loading of a text this represents one word in the text.
 *
 * @author Karl
 */
class Word {

  private final List<Character> characters = new ArrayList<>();
  private final double fontSize;
  private double width = 0;

  /**
   * Create a new empty word.
   *
   * @param fontSize - the font size of the text which this word is in.
   */
  Word(double fontSize) {
    this.fontSize = fontSize;
  }

  /**
   * Adds a character to the end of the current word and increases the screen-space width of the
   * word.
   *
   * @param character - the character to be added.
   */
  void addCharacter(Character character) {
    characters.add(character);
    width += character.getAdvanceX() * fontSize;
  }

  /** Returns he list of characters in the word. */
  List<Character> getCharacters() {
    return characters;
  }

  /** Returns the width of the word in terms of screen size. */
  double getWordWidth() {
    return width;
  }
}
