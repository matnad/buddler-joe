package gui;

import engine.render.Loader;
import engine.render.fontMeshCreator.FontType;
import engine.render.fontMeshCreator.GUIText;
import engine.render.fontRendering.TextMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.w3c.dom.Text;

import java.io.File;

public class FPS extends GUIString {

    private double counterD;

    public FPS(Loader loader) {
        super(loader);

        setPosition(new Vector2f(.92f,.02f));
        setTextColour(new Vector3f(1f,1f,0f));
        setAlpha(1);
    }

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
