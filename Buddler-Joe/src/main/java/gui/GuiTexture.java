package gui;

import org.joml.Vector2f;

public class GuiTexture {

    private int texture;
    private Vector2f position;
    private Vector2f scale;
    private float alpha;

    public GuiTexture(int texture, Vector2f position, Vector2f scale, float alpha) {
        this.texture = texture;
        this.position = position;
        this.scale = scale;
        this.alpha = alpha;
    }

    public int getTexture() {
        return texture;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
