package entities.blocks.debris;

import entities.Entity;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import game.Game;
import org.joml.Vector3f;

/** IN DEVELOPMENT Try to create debris that adheres to physics. */
public class Debris extends Entity {

  private final float weight;
  private Block baseBlock;
  private Vector3f direction;
  private float lifeLength;
  private Vector3f spin;

  private Entity spawnedBy;
  private float trackAcceleration = 5;

  private float elapsedTime = 0;

  /**
   * Create a (small) copy of a block with physics and a lifeLength attached. Don't call this
   * directly, use {@link DebrisMaster#generateDebris(Block)} instead.
   *
   * @param block base type to create debris from
   * @param size size for the debris
   */
  Debris(Block block, float size) {
    super(block.getDebrisModel(), 0, block.getPosition(), 0, 0, 0, size);

    baseBlock = block;
    spawnedBy = block.getDestroyedBy();
    float volume = (float) Math.pow(size, 2);
    float mass = 1f; // block.getMass();
    float gravity = 80; // TODO: Gravity constant in settings?
    this.weight = mass * volume * gravity;
    this.randomize();
  }

  /**
   * Randomize various parameters of the debris behaviour. Randomize lifeLength along a normal
   * distribution, Randomize spin, direction and position along an uniform distribution with a Z
   * bias for direction -> this creates the effect of more blocks falling "off the stage"
   */
  private void randomize() {
    lifeLength = (float) (2 + DebrisMaster.random.nextGaussian() * 4);
    direction =
        new Vector3f(
            DebrisMaster.random.nextFloat() * 3 - 1.5f,
            DebrisMaster.random.nextFloat() * 3 - 1.5f,
            DebrisMaster.random.nextFloat() * 4);
    spin =
        new Vector3f(
                DebrisMaster.random.nextFloat(),
                DebrisMaster.random.nextFloat(),
                DebrisMaster.random.nextFloat())
            .mul(100);
    setPosition(
        new Vector3f(
            getPosition().x + DebrisMaster.random.nextFloat() * 4 - 2,
            getPosition().y + DebrisMaster.random.nextFloat() * 4 - 2,
            getPosition().z + DebrisMaster.random.nextFloat() * 4 - 2));
  }

  /**
   * Very simple physics simulation. Update y position of the block according to (mass * density *
   * gravity) = weight Then translate along direction vector and rotate around spin vector
   */
  public void update() {

    float delta = (float) Game.dt();

    elapsedTime += delta;
    if (baseBlock.getType() == BlockMaster.BlockTypes.GOLD
        && elapsedTime > .8f
        && spawnedBy != null) {
      // After 1 second, start flying to the player
      trackAcceleration += 8 * delta;
      // Vector3f posNew = spawnedBy.getBbox().getCenter();
      // direction
      //    .mul((1 - (4 + trackAcceleration) * delta))
      //    .add(new Vector3f(posNew).sub(getPosition()).mul((4 + trackAcceleration) * delta));
      // if (direction.length() < 60 * delta) {
      //  System.out.println(direction.length());
      //  direction.mul(direction.length() / (60 * delta));
      // }
      // if (getPosition().distance(posNew) <= 1f) {
      //  elapsedTime = lifeLength;
      // }
      Vector3f posNew = spawnedBy.getBbox().getCenter();
      direction.mul(1 / elapsedTime / lifeLength);
      direction.add(new Vector3f(posNew).sub(getPosition())).normalize().mul(trackAcceleration);

      if (getPosition().distance(posNew) <= 1f) {
        elapsedTime = lifeLength;
      }

    } else {
      direction.y -= weight * delta;
      handleCollision();
    }
    increaseRotation(new Vector3f(spin).mul(delta));
    increasePosition(new Vector3f(direction).mul(2).mul(delta));
  }

  /**
   * Stop all movement when it collides with a surface. Will get us some clipping. The next step, to
   * do constraint based entities.collision with multiple contact points, is probably a bit too
   * much.
   */
  private void handleCollision() {
    if (getPosition().z <= getScale().z) {
      direction = new Vector3f();
      spin = new Vector3f();
    }

    for (Block block : BlockMaster.getBlocks()) {
      if (block.getDistanceSquaredFrom(getPosition()) < 16) {
        /*
        Block is close -> check entities.collision
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
