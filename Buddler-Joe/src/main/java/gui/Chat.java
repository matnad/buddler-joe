package gui;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.fontMeshCreator.FontType;
import engine.render.fontRendering.TextMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The Chat Window Overlay in the Game
 *
 * In its normal state it is transparent. With ENTER it can be activated to type a message.
 * Pressing ENTER while a message is typed will send the message.
 * Pressing ENTER while no message is typed will fade out the chat.
 *
 * This class is only a mock up and not functional yet, except for the fading and positioning.
 * TODO (anyone): Build a proper message handler
 *
 */
public class Chat {

    private static final float ALPHA_OFF = .3f;
    private static final float ALPHA_ON = .8f;

    private boolean enabled;
    private String chatText;
    private GuiTexture chatGui;
    private float alpha;

    private FontType font;
    private ChatText guiText;
    private Vector3f textColour;

    private List<ChatText> messages;
    private int msgSize;

    /**
     * Initialize Chat, only needs to be called once on game init.
     *
     * @param loader main loader
     */
    public Chat(Loader loader) {
        enabled = false;
        alpha = ALPHA_OFF;

        //Load the background image of the chat and set rendering parameters
        chatGui = new GuiTexture(loader.loadTexture("chat"), new Vector2f(-.6f, -.6f), new Vector2f(.4f, .4f), ALPHA_OFF);
        chatText = "";

        //Load font and text properties for all messages
        font = new FontType(loader.loadFontTexture("src/main/resources/assets/fonts/verdana"), new File("src/main/resources/assets/fonts/verdana.fnt"));
        textColour = new Vector3f(1f,1f,1f);
        guiText = new ChatText(chatText, 1, new Vector3f(textColour.x, textColour.y, textColour.z), alpha, font,  new Vector2f(.06f,.91f), 1f, false, false);

        messages = new ArrayList<>();
        msgSize = messages.size();
    }


    /**
     *
     * TEMPORARY METHOD!!!!!
     *
     * Called every frame. Reads chat input and toggles chat window
     *
     * TODO (anyone): Build a keyboard text input handler
     *
     */
    public void checkInputs() {
        if (InputHandler.isKeyPressed(GLFW_KEY_ENTER)) {
            if (chatText.length() > 0 && enabled) {
                sendMessage();
            } else {
                toggleChat();
            }
        }

        updateAlpha();

        if(!enabled) {
            return;
        }

        char newChar;
        String newChatText = chatText;

        if(InputHandler.isKeyPressed(GLFW_KEY_A)) {
            newChar = 'a';
            if(InputHandler.isKeyDown(GLFW_KEY_LEFT_SHIFT) ) {
                newChar = Character.toUpperCase(newChar);
            }
            newChatText += newChar;

        } else if(InputHandler.isKeyPressed(GLFW_KEY_B)) {
            newChar = 'b';
            if(InputHandler.isKeyDown(GLFW_KEY_LEFT_SHIFT) ) {
                newChar = Character.toUpperCase(newChar);
            }
            newChatText += newChar;

        } else if (InputHandler.isKeyPressed(GLFW_KEY_BACKSPACE)) {
            if (newChatText.length() > 0) {
                newChatText = newChatText.substring(0, newChatText.length() - 1);
            }
        } else if (InputHandler.isKeyPressed(GLFW_KEY_DELETE)) {
            newChatText = "";
        }

        if (!chatText.equals(newChatText)) {
            chatText = newChatText;
            updateGuiText();
        }
        arrangeMessages();

    }

    /**
     * Clear text box on send
     *
     * @param text text to remove
     * @return an empty text
     */
    private ChatText clearChatText(ChatText text) {
        TextMaster.removeText(text);
        return new ChatText("", text.getFontSize(), text.getColour(), text.getAlpha(), text.getFont(),  text.getPosition(), text.getMaxLineSize(), text.isCentered(), text.isSent());
    }

    /**
     * Mock up.
     * Add timestamp and user to message.
     * Adds message to a message handler (currently just a list)
     * Clears the message.
     */
    private void sendMessage() {
        ChatText messageText = new ChatText(guiText.getTime()+chatText, .7f, textColour, alpha, font,  new Vector2f(.06f,.91f),  1f, false, false);
        guiText = clearChatText(guiText);
        messages.add(messageText);
        chatText = "";
    }

    /**
     * TEMPORARY MOCK UP!!
     *
     * This should get its own class later to handle network messages
     * Maybe validate first if there are invalid messages
     *
     * Check if messages changed so we don't have to create them every frame
     *
     */
    private void arrangeMessages() {
        if(messages.size() != msgSize) { //Something changed
            float posY = .64f;
            float posX = .045f;
            for (ChatText message : messages) {
                message.setPosition(new Vector2f(posX, posY));
                posY += .02f;
            }
            msgSize = messages.size(); //Update size so we can detect further changes
        }
    }

    /**
     * We need to fully recreate and render if even a single letter changes
     */
    private void updateGuiText() {
//        guiText.setTextString(chatText); // doesn't work, we need to reload the texture and create a new text
        TextMaster.removeText(guiText);
        guiText = new ChatText(chatText, 1, textColour, alpha, font,  new Vector2f(.06f,.91f),  1f, false, false);

    }

    /**
     * Chat fading
     */
    private void updateAlpha() {
        if(enabled && alpha < ALPHA_ON) {
            alpha += .02f;

        } else if(!enabled && alpha > ALPHA_OFF) {
            alpha -= .025f;
        }
        chatGui.setAlpha(alpha);
        guiText.setAlpha(alpha);
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

    private void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public GuiTexture getChatGui() {
        return chatGui;
    }
}
