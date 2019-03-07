package gui;

import engine.render.MasterRenderer;
import engine.render.fontRendering.TextMaster;
import entities.NetPlayer;
import org.joml.*;
import util.Maths;

/**
 * TEMPORARY CLASS
 *
 * Displays username that stay on the edge of the screen.. still work to be done here.
 *
 * Disregard this as it is not implemented yet
 */
public class DirectionalUsername extends GUIString{

    private NetPlayer player;

    public DirectionalUsername(NetPlayer player) {
        super();
        this.player = player;

        setAlpha(.5f);
        setTextColour(new Vector3f(1,1,1));
        setFontSize(.5f);

        setGuiStringString(player.getUsername());
    }


    @Override
    public void updateString() {
        if (getGuiString() != null)
            TextMaster.removeText(getGuiString());
        setPosition(findLocation());

        createGuiText();
    }

    private Vector2f findLocation() {
        //Transforms world coodinates to normalized device coordinates. Experimental feature!
        //This will generate the effect of the text pointing in the direction of the player.
        Vector4f loc = new Vector4f(player.getbBox().getMinX(), player.getbBox().getMaxY(), player.getbBox().getMaxZ()-player.getbBox().getDimZ()/2, 1f)
                .mul(Maths.createViewMatrix())
                .mul(MasterRenderer.getProjectionMatrix())
                .normalize();


        float normX = (loc.x+1)/2;
        float normY = (1-loc.y)/2;
        return new Vector2f(normX,normY);
    }

}
