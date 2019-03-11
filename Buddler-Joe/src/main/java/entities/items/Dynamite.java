package entities.items;

import bin.Game;
import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.systems.Explosion;
import engine.particles.systems.Fire;
import engine.particles.systems.Smoke;
import engine.render.Loader;
import engine.render.objConverter.OBJFileLoader;
import engine.textures.ModelTexture;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import entities.light.Light;
import entities.light.LightMaster;
import org.joml.Vector3f;

import java.util.Random;

/**
 * A budle of dynamite that can damage blocks or the player
 */
public class Dynamite extends Item {
    private  final float GRAVITY = 20;
    private final float FUSE_TIMER = 3f;
    private final float EXPLOSION_TIME = .5f;
    private final float TOTAL_EFFECTS_TIME = 2.5f;
    private final float EXPLOSION_RANGE = 15;
    private final float MAXIMUM_DAMAGE = 50;

    private static TexturedModel preloadedModel;

    private float time;
    private boolean active;
    private boolean exploded;

    private Fire particleFuse;
    private Explosion particleExplosion;
    private Explosion particleShrapnel;
    private Smoke particleSmoke;
    private Explosion particleShockwave;
    private Light flash;


    /**
     * Extended Constructor for Dynamite. Don't use directly. Use the Item Master to create items.
     */
    Dynamite(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(ItemMaster.ItemTypes.DYNAMITE, getPreloadedModel(), position, rotX, rotY, rotZ, scale);
        time = 0;
        active = false;
        exploded = false;



        //Generate Fuse Effect
        particleFuse = new Fire(70, 1, .01f, 1, 3);
        particleFuse.setDirection(new Vector3f(0,1,0), 0.1f);
        particleFuse.setLifeError(.2f);
        particleFuse.setSpeedError(.1f);
        particleFuse.setScaleError(.5f);
        particleFuse.randomizeRotation();

        /* Generate Fancy Particle Effects for an explosion */
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

    /**
     * Constructor for the dynamite. Don't use directly. Use the Item Master to create items.
     *
     * @param position position to spawn the dynamite
     */
    Dynamite(Vector3f position) {
        this(position, 0,0,0, 1);

    }

    /**
     * Preload model
     *
     * @param loader passed by Item Master
     */
    public static void init(Loader loader) {
        RawModel rawDynamite = loader.loadToVAO(OBJFileLoader.loadOBJ("dynamite"));
        setPreloadedModel(new TexturedModel(rawDynamite, new ModelTexture(loader.loadTexture("dynamite"))));
    }

    /**
     * Called every frame. Updates all the animations for the Dynamite and decides which particle systems will fire.
     * Takes care of collision and removes itself when done with everything.
     */
    @Override
    public void update() {

        //Skip if dynamit is being placed or otherwise inactive
        if(!active)
            return;

        //Update the fuse time
        time += Game.window.getFrameTimeSeconds();

        /*
        Case 1: Dynamit is about to blow -> play fuse animation, check for collision and update position if falling
          Dynamite can only move if it is in this phase, the explosion will be stationary.
        */
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

        /*
        Case 2: Explosion is finished, remove the object
         */
        } else if (time >= FUSE_TIMER + EXPLOSION_TIME) {

        /*
        Case 3: Time is up, explode the dynamite.
        Play explosion and smoke effect, and for 0.1 seconds, also play shrapnel and shockwave effects
         */
        } else if (time >= FUSE_TIMER && time <= FUSE_TIMER + EXPLOSION_TIME) {
            explode();
            particleExplosion.generateParticles(getPosition());
            particleSmoke.generateParticles(getPosition());
            if(time <= FUSE_TIMER+0.1) {
                particleShrapnel.generateParticles(getPosition());
                particleShockwave.generateParticles(getPosition());
            }

        }

        if(time >= FUSE_TIMER+TOTAL_EFFECTS_TIME) {
            setDestroyed(true); //Remove Object
            flash.setDestroyed(true);
        } else if (time >= FUSE_TIMER + .3f) {
            float scaleBrightness = (float) (1-Game.window.getFrameTimeSeconds()*5);
            flash.getColour().mul(scaleBrightness);
        } else if (time > FUSE_TIMER){
            float scaleBrightness = (float) (1+Game.window.getFrameTimeSeconds()*10);
            flash.getColour().mul(scaleBrightness);
        }
    }

    /**
     * Damage the blocks in range of the explosion and hide the dynamite
     */
    private void explode() {
        if (exploded) {
            return;
        }
        exploded = true;
        setScale(0); //Hide the model, but keep the object for the explosion effect to complete
        for (Block block : BlockMaster.getBlocks()) {
            float distance = block.get2dDistanceFrom(getPositionXY());
            if(distance < EXPLOSION_RANGE) {
                //Damage blocks inverse to distance (closer = more damage)
                block.increaseDamage(1/distance * MAXIMUM_DAMAGE, this);
            }
        }
        flash = LightMaster.generateLight(LightMaster.LightTypes.FLASH, getPosition(), new Vector3f(1,1,1));

    }



    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private static void setPreloadedModel(TexturedModel preloadedModel) {
        Dynamite.preloadedModel = preloadedModel;
    }

    private static TexturedModel getPreloadedModel() {
        return preloadedModel;
    }
}
