package entities.items;

import bin.Game;
import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objConverter.OBJFileLoader;
import engine.textures.ModelTexture;
import entities.blocks.Block;
import org.joml.Vector3f;

public class Dynamite extends Item {
    private  final float GRAVITY = 10;
    private final float FUSE_TIMER = 3f;
    private float time;


    public Dynamite(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(getPreloadedModel(), position, rotX, rotY, rotZ, scale);
        time = 0;
    }

    public Dynamite(Vector3f position) {
        this(position, 0,0,0, 1);

    }

    public static void loadModel(Loader loader) {
        RawModel rawDynamite = loader.loadToVAO(OBJFileLoader.loadOBJ("dynamite"));
        setPreloadedModel(new TexturedModel(rawDynamite, new ModelTexture(loader.loadTexture("dynamite"))));
    }

    public void move() {
        boolean collision = false;
        for (Block block : Game.getBlocks()) {
            if (collidesWith(block)) {
                collision = true;
                break;
            }
        }
        if (!collision) {
            increasePosition(0, (float) -(GRAVITY * Game.window.getFrameTimeSeconds()), 0);
            updateBoundingBox();
        }
        time += Game.window.getFrameTimeSeconds();
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
        setDestroyed(true);
    }
}
