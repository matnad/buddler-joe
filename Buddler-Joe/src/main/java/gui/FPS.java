package gui;

import engine.render.fontrendering.TextMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Shows FPS as text on screen
 */
public class FPS extends GUIString {

    private double counterD;

    /**
     * Create a yellow GUIString in the top right corner ready for rendering
     */
    public FPS() {
        super();

        setPosition(new Vector2f(.92f,.02f));
        setTextColour(new Vector3f(1f,1f,0f));
        setAlpha(1);
    }

    /**
     * To update, we need to re-create the string object as with all text objects
     *
     * @param fps pass current fps from the window class
     */
    public void updateString(String fps) {
        if (getGuiString() != null)
            TextMaster.removeText(getGuiString());
        try {
            double fpsD = Double.parseDouble(fps);
            counterD = fpsD;
            fpsD = Math.round(fpsD);
            setGuiStringString(""+fpsD);
            createGuiText();
        } catch (NumberFormatException e) {
            counterD = 0;
        }
    }

    public double getFPS() {
        return counterD;
    }


}
