package entities.items;

import bin.Game;
import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.Particle;
import engine.particles.systems.Explosion;
import engine.particles.systems.Fire;
import engine.particles.systems.Smoke;
import engine.render.Loader;
import engine.render.objConverter.OBJFileLoader;
import engine.textures.ModelTexture;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import gui.DynamiteTimer;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dynamite extends Item {
    private  final float GRAVITY = 20;
    private final float FUSE_TIMER = 3f;
    private final float EXPLOSION_TIME = .5f;
    private float time;
    private boolean active;
    private boolean exploded;

    private DynamiteTimer timerGUI;

    private Fire particleFuse;
    private Explosion particleExplosion;
    private Explosion particleShrapnel;
    private Smoke particleSmoke;
    private Explosion particleShockwave;

    public Dynamite(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(ItemMaster.ItemTypes.DYNAMITE, getPreloadedModel(), position, rotX, rotY, rotZ, scale);

        time = 0;
        active = false;
        exploded = false;
        timerGUI = new DynamiteTimer();
        timerGUI.setAlpha(1);

        //Generate Fuse Effect
        particleFuse = new Fire(70, 1, .01f, 1, 3);
        particleFuse.setDirection(new Vector3f(0,1,0), 0.1f);
        particleFuse.setLifeError(.2f);
        particleFuse.setSpeedError(.1f);
        particleFuse.setScaleError(.5f);
        particleFuse.randomizeRotation();

        //Generate Explosion Effect
        particleExplosion = new Explosion(200, 11, 0, .4f, 15);
        particleExplosion.setScaleError(.4f);
        particleExplosion.setSpeedError(.3f);
        particleExplosion.setLifeError(.2f);

        //Generate Shrapnel Effect
        particleShrapnel = new Explosion(1200, 70, 0, 1.5f, .85f);
        particleShrapnel.setScaleError(.2f);
        particleShrapnel.setLifeError(.5f);
        particleShrapnel.setSpeedError(.3f);

        //Generate Smoke Effect
        particleSmoke = new Smoke(40, 5, -.1f, 6f, 20f);
        particleSmoke.setScaleError(.2f);
        particleSmoke.setLifeError(.8f);
        particleSmoke.setSpeedError(.3f);
        particleSmoke.randomizeRotation();

        //Generate Shockwave effect
        //We can vary pps by graphic setting, but it looks very nice with a lot of particles!
        Random rnd = new Random(); //Give the shockwave a slight random tilt
        particleShockwave = new Explosion(3000, 40, 0f, .8f, 5f);
        particleShockwave.setScaleError(.1f);
        particleShockwave.setLifeError(.1f);
        particleShockwave.setDirection(new Vector3f(1,0,0), 0);
        particleShockwave.setRotationAxis(new Vector3f(rnd.nextFloat()*.4f-.2f,1,rnd.nextFloat()*.4f-.2f), 0);
    }

    public Dynamite(Vector3f position) {
        this(position, 0,0,0, 1);

    }

    public static void init(Loader loader) {
        RawModel rawDynamite = loader.loadToVAO(OBJFileLoader.loadOBJ("dynamite"));
        setPreloadedModel(new TexturedModel(rawDynamite, new ModelTexture(loader.loadTexture("dynamite"))));
    }

    @Override
    public void update() {

        if(!active)
            return;
        time += Game.window.getFrameTimeSeconds();
        if(time < FUSE_TIMER) {
            boolean collision = false;
            for (Block block : BlockMaster.getBlocks()) {
                if (collidesWith(block)) {
                    collision = true;
                    break;
                }
            }
            if (!collision) {
                increasePosition(0, (float) -(GRAVITY * Game.window.getFrameTimeSeconds()), 0);
            }

            float offset = getbBox().getDimY() * 2 * (FUSE_TIMER-time)/FUSE_TIMER;
            particleFuse.generateParticles(new Vector3f(0,offset, 0).add(getPosition()));
        } else if (time >= FUSE_TIMER + EXPLOSION_TIME) {
            setDestroyed(true); //Remove Object
        } else if (time >= FUSE_TIMER) {
            explode();

            particleExplosion.generateParticles(getPosition());

            particleSmoke.generateParticles(getPosition());
            if(time <= FUSE_TIMER+0.1) {
                particleShrapnel.generateParticles(getPosition());
                particleShockwave.generateParticles(getPosition());
            }


        }
    }

    private void explode() {
        if (exploded) {
            return;
        }
        exploded = true;
        setScale(0); //Hide the model, but keep the object for the explosion effect to complete
        for (Block block : BlockMaster.getBlocks()) {
            float distance = block.get2dDistanceFrom(getPositionXY());
            if(distance < 15) {
                //Damage blocks inverse to distance (closer = more damage)
                block.increaseDamage(1/distance * 50, this);
            }
        }
    }



    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
