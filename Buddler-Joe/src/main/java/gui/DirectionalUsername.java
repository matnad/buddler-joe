package gui;

import bin.Game;
import engine.io.Window;
import engine.render.Loader;
import engine.render.MasterRenderer;
import engine.render.fontMeshCreator.FontType;
import engine.render.fontMeshCreator.GUIText;
import engine.render.fontRendering.TextMaster;
import entities.NetPlayer;
import entities.Player;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import util.Maths;

import javax.swing.*;
import java.io.File;
import java.lang.Math;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class DirectionalUsername extends GUIString{

    private NetPlayer player;

    private Matrix4f viewMatrix;
    private Matrix4f projectionMatrix;



    public DirectionalUsername(NetPlayer player, Loader loader) {
        super(loader);
        this.player = player;
        setGuiStringString(player.getUsername());

        setFont(new FontType(loader.loadFontTexture("src/main/resources/assets/fonts/verdana"), new File("src/main/resources/assets/fonts/verdana.fnt")));
        setAlpha(.5f);
        setTextColour(new Vector3f(1,1,1));
        setFontSize(.5f);
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
        //System.out.println(""+normX+" "+normY);
        return new Vector2f(normX,normY);
    }

}
