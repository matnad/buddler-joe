package entities.blocks.debris;

import bin.Game;
import entities.Entity;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import org.joml.Vector3f;

/**
 * IN DEVELOPMENT
 *
 * Try to create debris that adheres to physics.
 *
 */
public class Debris extends Entity {

    private final float GRAVITY = 80;

    private Vector3f direction;
    private float lifeLength;
    private Vector3f spin;
    private float weight;

    private BlockMaster.BlockTypes type;

    private float elapsedTime = 0;

    /**
     * Create a (small) copy of a block with physics and a lifeLength attached.
     * Don't call this directly, use {@link DebrisMaster#generateDebris(Block)} instead.
     *
     * @param block base type to create debris from
     * @param size size for the debris
     */
    Debris(Block block, float size) {
        super(Block.getBlockModel(), block.getType().getTextureId(), block.getPosition(), 0, 0, 0, size);
        float volume = (float) Math.pow(size, 3);
        float mass = block.getMass();
        this.weight = mass * volume * GRAVITY;
        this.randomize();
    }

    /**
     * Randomize lifeLength along a normal distribution
     * Randomize spin, direction and position along an uniform distribution with a Z bias for direction
     *  -> this creates the effect of more blocks falling "off the stage"
     */
    private void randomize() {
        lifeLength = (float) (2 + DebrisMaster.random.nextGaussian() * 4);
        direction = new Vector3f(DebrisMaster.random.nextFloat()*3-1.5f, DebrisMaster.random.nextFloat()*3-1.5f, DebrisMaster.random.nextFloat()*4);
        spin = new Vector3f(DebrisMaster.random.nextFloat(), DebrisMaster.random.nextFloat(), DebrisMaster.random.nextFloat()).mul(100);
        setPosition(new Vector3f(getPosition().x+DebrisMaster.random.nextFloat()*4-2, getPosition().y+DebrisMaster.random.nextFloat()*4-2, getPosition().z+DebrisMaster.random.nextFloat()*4-2));
    }

    /**
     * Very simple physics simulation:
     * Update y position of the block according to (mass * density * gravity) = weight
     * Then translate along direction vector and rotate around spin vector
     */
    public void update() {
        elapsedTime += Game.window.getFrameTimeSeconds();
        direction.y -= weight*Game.window.getFrameTimeSeconds();
        handleCollision();
        increasePosition(new Vector3f(direction).mul((float) Game.window.getFrameTimeSeconds()));
        increaseRotation(new Vector3f(spin).mul((float) Game.window.getFrameTimeSeconds()));
    }

    /**
     * Stop all movement when it collides with a surface. Will get us some clipping.
     * The next step, to do constraint based collision with multiple contact points, is probably a bit too much.
     */
    private void handleCollision() {
        if (getPosition().z <= getScale()) {
            direction = new Vector3f();
            spin = new Vector3f();
        }

        for (Block block : BlockMaster.getBlocks()) {
            if(block.getDistanceSquaredFrom(getPosition()) < 16) {
                /*
                Block is close -> check collision
                We use squared distance because its faster
                 */
                if (block.collidesWith(this)) {
                    direction = new Vector3f();
                    spin = new Vector3f();
                }
            }
        }
    }

    float getLifeLength() {
        return lifeLength;
    }

    float getElapsedTime() {
        return elapsedTime;
    }
}
