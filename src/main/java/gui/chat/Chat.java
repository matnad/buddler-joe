package gui.chat;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontmeshcreator.FontType;
import engine.render.fontrendering.TextMaster;
import game.Game;
import gui.GuiTexture;
import java.util.ArrayList;
import java.util.List;
import net.packets.chat.PacketChatMessageToServer;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * The Chat Window Overlay in the Game
 *
 * <p>In its normal state it is transparent. With ENTER it can be activated to type a message.
 * Pressing ENTER while a message is typed will send the message. Pressing ENTER while no message is
 * typed will fade out the chat.
 *
 * <p>This class is only a mock up and not functional yet, except for the fading and positioning.
 */
public class Chat {

  private static final float ALPHA_OFF = .1f;
  private static final float ALPHA_ON = .8f;
  private static final float ALPHA_HIDDEN = 0f;
  private final float temporaryShowDuration = 3f;
  private boolean enabled;
  private boolean showTemporary;
  private float temporaryShowElapsed;

  private String chatText;
  private GuiTexture chatGui;

  private float alpha;

  private FontType font;
  private ChatText guiText;
  private ChatText wisperAddress;
  private Vector3f textColour;
  private Vector2f chatPosition;
  private Vector2f messagePosition;

  private float maxLineLength;
  private float differenceMessageToChat;
  private int maxLines;
  private boolean inLobby;

  private List<ChatText> messages;
  private int msgSize;
  private List<String> text;
  private String output;
  private String wisperName;
  private boolean backToChat;

  private boolean hidden = false;

  /**
   * Initialize Chat, only needs to be called once on game init.
   *
   * @param maxLines Maximum Number of lines to display (screen coords)
   * @param maxLineLength maximum length of lines before linebreak (screen coords)
   * @param loader main loader
   */
  public Chat(Loader loader, int maxLines, float maxLineLength) {
    this.maxLines = maxLines;
    this.maxLineLength = maxLineLength;
    this.temporaryShowElapsed = 0;
    enabled = false;
    alpha = ALPHA_OFF;

    // Load the background image of the chat and set rendering parameters
    chatGui =
        new GuiTexture(
            loader.loadTexture("chatBackground"),
            new Vector2f(-.6f, -.6f),
            new Vector2f(.4f, .4f),
            ALPHA_OFF);
    chatText = "";

    // Load font and text properties for all messages
    font = new FontType(loader, "verdanaAsciiEx");
    textColour = new Vector3f(1f, 1f, 1f);
    guiText =
        new ChatText(
            chatText,
            0.25f,
            new Vector3f(textColour.x, textColour.y, textColour.z),
            alpha,
            font,
            new Vector2f(.06f, .91f),
            maxLineLength,
            false,
            false);

    wisperAddress =
            new ChatText(
                    chatText,
                    0.25f,
                    new Vector3f(textColour.x, textColour.y, textColour.z),
                    alpha,
                    font,
                    new Vector2f(.06f, .91f),
                    maxLineLength,
                    false,
                    false);

    wisperName = "";
    messages = new ArrayList<>();
    text = new ArrayList<>();
    msgSize = 0;
    chatPosition = new Vector2f();
    messagePosition = new Vector2f();
    differenceMessageToChat = 0f;
    inLobby = true;
  }

  /**
   * Method to check user input which calls the keyboardInputHandler Method check the wisper
   * function. If the username is correct, the player only sends the message to this person.
   * With @all the player send a message to all player. Without @ the player send the message to the
   * lobby.
   *
   * <p>Called every frame. Reads chat input and toggles chat window text input handler
   */
  public void checkInputs() {
    if (inLobby) {
      setEnabled(true);
      hidden = false;
      InputHandler.readInputOn();
    }
    if (InputHandler.isKeyPressed(GLFW_KEY_ENTER)) {
      if (chatText.length() > 0 && enabled) {
        PacketChatMessageToServer chatString = new PacketChatMessageToServer(wisperName + chatText);
        chatString.sendToServer();
        TextMaster.removeText(wisperAddress);
        wisperName = "";

        chatText = "";
        InputHandler.resetInputString();

      } else {
        if (enabled) {
          setEnabled(false);
          InputHandler.readInputOff();
        } else if (!hidden) {
          setEnabled(true);
          InputHandler.readInputOn();
        }
        InputHandler.resetInputString();
      }
    }

    if (messages.size() != text.size()) {
      addChatText();
    }
    if (!inLobby) {
      updateAlpha();
    }
    if (showTemporary) {
      temporaryShowElapsed += Game.dt();
      if (temporaryShowElapsed >= temporaryShowDuration) {
        temporaryShowElapsed = 0;
        showTemporary = false;
      }
    }

    if (!enabled) {
      if (showTemporary) {
        arrangeMessages();
      }
      return;
    }

    String newChatText = chatText;

    newChatText = InputHandler.getInputString();

    if (newChatText.length() > 100) {
      newChatText = chatText;
      StringBuilder temp = new StringBuilder(chatText);
      InputHandler.setInputString(temp);
    }

    if (!chatText.equals(newChatText)) {
      chatText = newChatText;
      updateGuiText();
    }
    arrangeMessages();
  }

  /**
   * Clear text box on send.
   *
   * @param text text to remove
   * @return an empty text
   */
  private ChatText clearChatText(ChatText text) {
    TextMaster.removeText(text);
    return new ChatText(
        "",
        text.getFontSize(),
        text.getColour(),
        text.getAlpha(),
        text.getFont(),
        text.getPosition(),
        text.getMaxLineSize(),
        text.isCentered(),
        text.isSent());
  }

  /**
   * Mock up. Add timestamp and user to message. Adds message to a message handler (currently just a
   * list) Clears the message.
   */
  private void sendMessage() {
    ChatText messageText =
        new ChatText(
            guiText.getTime() + chatText,
            .7f,
            textColour,
            alpha,
            font,
            new Vector2f(.06f, .91f),
            maxLineLength,
            false,
            false);
    guiText = clearChatText(guiText);
    messages.add(messageText);
    chatText = "";
  }

  /**
   * TEMPORARY MOCK UP.!!
   *
   * <p>This should get its own class later to handle network messages Maybe validate first if there
   * are invalid messages
   *
   * <p>Check if messages changed so we don't have to create them every frame
   */
  public void arrangeMessages() {

    if (messages.size() != msgSize || backToChat) { // Something changed
      float posY = chatPosition.y;
      float posX = chatPosition.x;
      int currentLines = 0;

      for (int i = messages.size() - 1; i >= 0; i--) {
        int lines = messages.get(i).getNumberOfLines();
        currentLines += lines;
        posY -= .02f * lines;
        messages.get(i).setPosition(new Vector2f(posX, posY));
        if (currentLines >= maxLines + 1) {
          messages.get(i).remove();
        }
        if (currentLines > maxLines + 1) {
          break;
        }
      }
      // for (ChatText message : messages) {
      //  message.setPosition(new Vector2f(posX, posY));
      //  posY += .02f * message.getNumberOfLines();
      // }
      backToChat = false;
      msgSize = messages.size(); // Update size so we can detect further changes
    }
  }

  /** We need to fully recreate and render if even a single letter changes. */
  private void updateGuiText() {
    // guiText.setTextString(chatText); // doesn't work, we need to reload the texture and
    // create a new text
    output = chatText;
    do {
      TextMaster.removeText(guiText);

      guiText = new ChatText(output, 1, textColour, alpha, font, messagePosition, 1f, false, false);

      if (output.length() > 0) {
        output = output.substring(1);
      }

    } while (guiText.getLengthOfLines().get(guiText.getLengthOfLines().size() - 1)
        > maxLineLength - differenceMessageToChat);
    //    System.out.println(guiText.getLengthOfLines().get(guiText.getLengthOfLines().size()-1));
  }

  /** Chat fading. */
  private void updateAlpha() {
    if ((enabled || showTemporary) && alpha < ALPHA_ON) {
      alpha += .02f;

    } else if (!enabled && !showTemporary && alpha > ALPHA_OFF) {
      if (alpha - ALPHA_OFF > 0.001f) {
        alpha -= .02f;
      }
    }

    if (hidden) {
      if (alpha > ALPHA_HIDDEN) {
        alpha -= .02f;
      }
    } else if (alpha < ALPHA_OFF) {
      if (ALPHA_OFF - alpha > 0.001f) {
        alpha += .02f;
      }
    }
    chatGui.setAlpha(alpha);
    guiText.setAlpha(alpha);
    wisperAddress.setAlpha(alpha);
    for (ChatText message : messages) {
      message.setAlpha(alpha);
    }
  }

  private void toggleChat() {
    setEnabled(!enabled);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  // public String getChatText() {
  //  return chatText;
  // }
  //
  // public void setChatText(String chatText) {
  //  this.chatText = chatText;
  // }
  //
  public GuiTexture getChatGui() {
    return chatGui;
  }

  /**
   * Add a new chat message to the List text.
   *
   * @param stringText is the new message which comes in
   */
  public void addText(String stringText) {
    text.add(stringText);
    if (!enabled) {
      showTemporary = true;
      temporaryShowElapsed = 0;
    }
  }

  /** creates a new chat message which can be displayed in the chat window. */
  public void addChatText() {

    ChatText messageText =
        new ChatText(
            text.get(text.size() - 1),
            .7f,
            textColour,
            alpha,
            font,
            new Vector2f(.06f, .91f),
            maxLineLength,
            false,
            false);
    guiText = clearChatText(guiText);
    messages.add(messageText);
  }

  /** Adjusts all variables for the chat to fit in the InGame-Chatbox. */
  public void setGameChatSettings() {
    chatPosition.x = 0.03f;
    chatPosition.y = 0.88f;
    maxLines = 10;
    textColour.x = 1f;
    textColour.y = 1f;
    textColour.z = 1f;
    maxLineLength = 0.34f;
    differenceMessageToChat = 0.04f;
    messagePosition.x = 0.06f;
    messagePosition.y = 0.91f;
    inLobby = false;
    enabled = false;
  }

  /** Adjusts all variables for the chat to fit in the InLobbyMenu-Chatbox. */
  public void setLobbyChatSettings() {
    chatPosition.x = 0.53f;
    chatPosition.y = 0.71f;
    maxLines = 23;
    textColour.x = 0f;
    textColour.y = 0f;
    textColour.z = 0f;
    maxLineLength = 0.205f;
    messagePosition.x = 0.525f;
    messagePosition.y = 0.785f;
    alpha = 1;
  }

  public float getAlpha() {
    return alpha;
  }

  /** Hides the chat. */
  public void hide() {
    hidden = true;
    enabled = false;
    showTemporary = false;
  }

  /** Unhides the Chat. */
  public void unhide() {
    hidden = false;
  }
  public void setWisperName(String wisperName) {
    this.wisperName = "@" + wisperName;
    updateGuiWisperName();
  }

  public void setBackToChat(boolean backToChat) {
    this.backToChat = backToChat;
  }
  public void updateGuiWisperName(){
    wisperAddress = new ChatText(wisperName, 0.75f, textColour, alpha, font, new Vector2f(0.145f,0.65f), 1f, false, false);
  }
}
