package entities;

import org.joml.Vector3f;

/**
 * Simple Light Object with position and colour
 * Used in the shaders to calculate color of objects
 *
 * We will expand on this if time permits!
 */
public class Light {
    private Vector3f position;
    private Vector3f colour;

    /**
     * Strength of the light depends on distance and angle.
     *
     * @param position world coordinates
     * @param colour r, g, b
     */
    public Light(Vector3f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
}
