package gui;

import engine.render.fontmeshcreator.FontType;
import engine.render.fontmeshcreator.GuiText;
import game.Game;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * A Chat message with Username and Formatted Timestamp.
 *
 * <p>This will change when we build a proper chat We need a proper timestamp too
 */
class ChatText extends GuiText {

  private String time;
  private String username;
  private boolean sent;

  /**
   * Create a chat message that can be rendered on screen.
   *
   * @param text Text of the message
   * @param fontSize font size...
   * @param colour r, g, b
   * @param alpha transparency, used to fade the text with the chat window
   * @param font font style
   * @param position in screen coordinates
   * @param maxLineLength 1 = screen wide
   * @param centered true = center of line length
   * @param sent true if the message is submitted
   */
  ChatText(
      String text,
      float fontSize,
      Vector3f colour,
      float alpha,
      FontType font,
      Vector2f position,
      float maxLineLength,
      boolean centered,
      boolean sent) {
    super(text, fontSize, font, colour, alpha, position, maxLineLength, centered);

    username = Game.getActivePlayer().getUsername();
    this.sent = sent;
    time = "[" + username + "-" + new SimpleDateFormat("HH:mm").format(new Date()) + "] ";
  }

  boolean isSent() {
    return sent;
  }

  public void setSent(boolean sent) {
    this.sent = sent;
  }

  String getTime() {
    return time;
  }

  public String getUsername() {
    return username;
  }
}
