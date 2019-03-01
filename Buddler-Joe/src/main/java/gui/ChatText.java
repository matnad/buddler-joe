package gui;

import bin.Game;
import engine.render.fontMeshCreator.FontType;
import engine.render.fontMeshCreator.GUIText;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatText extends GUIText {

//    private Date date;
    private String time;
    private String username;
    private boolean sent;

    public ChatText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered, boolean sent) {
        super(text, fontSize, font, position, maxLineLength, centered);

        username = Game.username;
        this.sent = sent;
        time = "["+username+"-"+new SimpleDateFormat("HH:mm").format(new Date())+"] ";

    }

    public ChatText(String text, float fontSize, Vector3f colour, float alpha, FontType font, Vector2f position, float maxLineLength, boolean centered, boolean sent) {
        super(text, fontSize, font, colour, alpha, position, maxLineLength, centered);

        username = Game.username;
        this.sent = sent;
        time = "["+username+"-"+new SimpleDateFormat("HH:mm").format(new Date())+"] ";
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }
}
