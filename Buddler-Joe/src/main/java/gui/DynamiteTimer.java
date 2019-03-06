package gui;

import bin.Game;
import engine.render.MasterRenderer;
import engine.render.fontRendering.TextMaster;
import engine.textures.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import util.Maths;

public class DynamiteTimer extends GUIString {
    public DynamiteTimer() {
        super();
    }


    public void updateString(Vector3f position) {
        if (getGuiString() != null) {
            TextMaster.removeText(getGuiString());
        }
//        setPosition(findLocation(position));
        setPosition(findLocation(position));
        //System.out.println(findLocation(position));
        super.updateString();

    }

    private Vector2f findLocation(Vector3f position) {
        //Transforms world coodinates to normalized device coordinates. Experimental feature!
        //This will generate the effect of the text pointing in the direction of the player.

        Vector3f out = new Vector3f();

        int[] viewport = new int[4];
        viewport[2] = Game.window.getWidth();
        viewport[3] = Game.window.getHeight();


        Vector4f coords = new Vector4f(position.x, position.y, position.z, 1f);

        Vector4f loc = new Vector4f(0,0,0,0);

        Maths.createViewMatrix()
                .transform(coords, loc);

        MasterRenderer.getProjectionMatrix()
                .transform(loc, loc);

        Vector3f screenCoords = clipSpaceToScreenSpace(loc);

//        float normX = (loc.x+1)/2;
//        float normY = (1-loc.y)/2;

        screenCoords.normalize();

        System.out.println(new Vector2f(screenCoords.x,screenCoords.y));

        return new Vector2f(screenCoords.x,screenCoords.y); //new Vector2f(out.x, out.y);
    }

//    public static Vector3f convertToScreenSpace(Vector3f position, Matrix4f viewMatrix, Matrix4f projMatrix) {
//        Vector4f coords = new Vector4f(position.x, position.y, position.z, 1f);
//        Matrix4f.transform(viewMatrix, coords, coords);
//        Matrix4f.transform(projMatrix, coords, coords);
//        Vector3f screenCoords = clipSpaceToScreenSpace(coords);
//        return screenCoords;
//    }

    private static Vector3f clipSpaceToScreenSpace(Vector4f coords) {
        if (coords.w < 0) {
            return null;
        }
        Vector3f screenCoords = new Vector3f(((coords.x / coords.w) + 1) / 2f,
                1 - (((coords.y / coords.w) + 1) / 2f), coords.z);
        return screenCoords;
    }
}
