package entities.blocks;

import engine.particles.Particle;
import engine.particles.systems.Debris;
import entities.NetPlayer;
import entities.Player;
import org.joml.Vector3f;

import java.util.Random;


/**
 * Dirt Block
 *
 * Holds methods and variables specific to Dirt Blocks.
 */
public class DirtBlock extends Block {

    /**
     * Extended Constructor, dont call directly.
     */
    DirtBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        //Must pass block type and hardness here as they are required
        super(BlockMaster.BlockTypes.DIRT, 0.9f, position, rotX, rotY, rotZ, scale);
    }

    /**
    Shortened constructer with just position. Dont call directly.
     */
    DirtBlock(Vector3f position) {
        this(position, 0, 0 ,0, 3);
    }

    @Override
    protected void onDestroy() {
        //Experimental Debris generation
        if(getDestroyedBy() instanceof Player || getDestroyedBy() instanceof NetPlayer ) {
            Random r = new Random();
            for (int i = 0; i < generateValue(r, 10, 1f); i++) {
                new Particle(Debris.getParticleTexture(), new Vector3f(
                        getPosition().x + (r.nextFloat() * getDim() * 2) - getDim(),
                        getPosition().y + (r.nextFloat() * getDim() * 2) - getDim(),
                        getPosition().z + (r.nextFloat() * getDim() * 2) - getDim()),
                        new Vector3f(0, 0, generateValue(r, 15, .2f)),
                        generateValue(r, 0.3f, .05f),
                        generateValue(r, 2, .5f),
                        r.nextFloat() * 360,
                        generateValue(r, 1, .5f));
            }
        }
    }

    /**
     * Temporary function to generate some randomness. Just for testing.
     */
    private float generateValue(Random r, float average, float errorMargin) {
        float offset = (r.nextFloat() - 0.5f) * 2f * errorMargin;
        return average + offset;
    }
}
