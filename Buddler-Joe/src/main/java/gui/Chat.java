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

    public Chat(Loader loader) {
        enabled = false;
        alpha = ALPHA_OFF;

        chatGui = new GuiTexture(loader.loadTexture("chat"), new Vector2f(-.6f, -.6f), new Vector2f(.4f, .4f), ALPHA_OFF);
        chatText = "";

        font = new FontType(loader.loadFontTexture("src/main/resources/assets/fonts/verdana"), new File("src/main/resources/assets/fonts/verdana.fnt"));
        textColour = new Vector3f(1f,1f,1f);
        guiText = new ChatText(chatText, 1, new Vector3f(textColour.x, textColour.y, textColour.z), alpha, font,  new Vector2f(.06f,.91f), 1f, false, false);

        messages = new ArrayList<>();
        msgSize = messages.size();


    }

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

    private ChatText clearChatText(ChatText text) {
        TextMaster.removeText(text);
        return new ChatText("", text.getFontSize(), text.getColour(), text.getAlpha(), text.getFont(),  text.getPosition(), text.getMaxLineSize(), text.isCentered(), text.isSent());
    }

    private void sendMessage() {
        ChatText messageText = new ChatText(guiText.getTime()+chatText, .7f, textColour, alpha, font,  new Vector2f(.06f,.91f),  1f, false, false);
        guiText = clearChatText(guiText);
        messages.add(messageText);
        chatText = "";
    }

    private void arrangeMessages() {
        //This should get its own class later to handle network messages

        //Maybe validate first if there are invalid messages

        //Check if messages changed so we don't have to create them every frame
        //If messages will be deleted in the future, revisit this (problem when size stays constant in 1 frame)
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

    private void updateGuiText() {
//        guiText.setTextString(chatText); // doesn't work, we need to reload the texture and create a new text
        TextMaster.removeText(guiText);
        guiText = new ChatText(chatText, 1, textColour, alpha, font,  new Vector2f(.06f,.91f),  1f, false, false);

    }

    private void updateAlpha() {
        if(enabled && alpha < ALPHA_ON) {
            alpha += .02f;
            //alpha = ALPHA_ON;

        } else if(!enabled && alpha > ALPHA_OFF) {
            alpha -= .025f;
            //alpha = ALPHA_OFF;
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
