package entities.items;

import bin.Game;
import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.fontRendering.TextMaster;
import engine.render.objConverter.OBJFileLoader;
import engine.textures.ModelTexture;
import entities.blocks.Block;
//import gui.DynamiteTimer;
import gui.DynamiteTimer;
import gui.GUIString;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Dynamite extends Item {
    private  final float GRAVITY = 20;
    private final float FUSE_TIMER = 3f;
    private float time;
    private boolean active;

    private DynamiteTimer timerGUI;

    public Dynamite(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(getPreloadedModel(), position, rotX, rotY, rotZ, scale);
        time = 0;
        active = false;
        timerGUI = new DynamiteTimer();
        timerGUI.setAlpha(1);
        Game.addEntity(this);
//        timerGUI = new DynamiteTimer();
    }

    public Dynamite(Vector3f position) {
        this(position, 0,0,0, 1);

    }

    public static void loadModel(Loader loader) {
        RawModel rawDynamite = loader.loadToVAO(OBJFileLoader.loadOBJ("dynamite"));
        setPreloadedModel(new TexturedModel(rawDynamite, new ModelTexture(loader.loadTexture("dynamite"))));
    }

    public void move() {

        if(!active)
            return;

        boolean collision = false;
        for (Block block : Game.getBlocks()) {
            if (collidesWith(block)) {
                collision = true;
                break;
            }
        }
        if (!collision) {
            increasePosition(0, (float) -(GRAVITY * Game.window.getFrameTimeSeconds()), 0);
        }
        time += Game.window.getFrameTimeSeconds();
//        timerGUI.setGuiStringString(""+Math.round((FUSE_TIMER-time)*10)/10f);
//        timerGUI.updateString(getPosition());
        if (time > FUSE_TIMER) {
            explode();
        }
    }

    private void explode() {
        for (Block block : Game.getBlocks()) {
            float distance = block.get2DDistanceFrom(getPositionXY());
            if(distance < 15) {
                //Damage blocks inverse to distance (closer = more damage)
                block.increaseDamage(1/distance * 50);
            }
        }
        //Destroy the dynamite object
//        TextMaster.removeText(timerGUI.getGuiString());
        setDestroyed(true);
    }



    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
