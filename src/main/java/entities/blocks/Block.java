package entities.blocks;

import engine.models.TexturedModel;
import engine.render.Loader;
import entities.Entity;
import entities.blocks.debris.DebrisMaster;
import game.Game;
import game.NetPlayerMaster;
import java.util.Random;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

/**
 * Abstract class for Blocks.
 *
 * <p>- Loads the texture Atlas - Handles damage to blocks - Provides getters and setters for
 * mandatory properties
 */
@SuppressWarnings("unused") // TODO: Still have some unused methods
public abstract class Block extends Entity {

  private final float hardness;
  private final float mass;
  private final float dim;
  private BlockMaster.BlockTypes type;
  private float damage;
  private Entity destroyedBy;

  // Grid Position for Map
  private int gridX;
  private int gridY;

  // Variables for moving and shaking a block
  private Vector3f moveTo;
  private Vector3f moveStartPos;
  private float moveDistance;
  private Vector3f speed;
  private Vector3f acceleration;
  private float moveDelay;
  private boolean shakeLeft;

  // Variables to track entities.collision time with a player while the block is moving
  private float collisionTime;

  /**
   * Abstract Constructor.
   *
   * @param blockModel textured model for the block
   * @param type type of block, described by Block Master enum
   * @param hardness damage before block is destroyed
   * @param mass mass or weight of object for physics calculation
   * @param position 3D coordinate of block center
   * @param rotX rotation around X axis
   * @param rotY rotation around Y axis
   * @param rotZ rotation around Z axis
   * @param scale scaling multiplier
   * @param gridX X coordinate for the block (map grid)
   * @param gridY Y coordinate for the block (map grid)
   */
  public Block(
      TexturedModel blockModel,
      BlockMaster.BlockTypes type,
      float hardness,
      float mass,
      Vector3f position,
      float rotX,
      float rotY,
      float rotZ,
      float scale,
      int gridX,
      int gridY) {
    // blockModel is a texture atlas, containing all block textures, ID is the position of the
    // texture on the atlas
    super(blockModel, type.getTextureId(), position, rotX, rotY, rotZ, scale);

    this.type = type;
    this.hardness = hardness;
    this.mass = mass;
    this.gridX = gridX;
    this.gridY = gridY;

    if (blockModel == null) {
      // Maybe need more than a warning
      // This is loaded in the BlockMaster.init() function
      System.out.println("WARNING! Load the block models first!");
    }

    setDestroyed(false);
    this.damage = 0;

    /*Dim is the distance from the center to a surface.
    For our basic block, the dimension is equal to the scale, however this might be different
     for other blocks.
    This allows us to override it from sub-blocks.
    */
    this.dim = scale;

    this.moveTo = getPosition();
    this.acceleration = new Vector3f(0, 1f, 0); // Added per second
    this.speed = new Vector3f(0, 0, 0);
  }

  /**
   * Preload texture atlas. Called by the Block Master's init().
   *
   * @param loader main loader
   */
  static void loadBlockModels(Loader loader) {
    // RawModel rawBlock = loader.loadToVao(ObjFileLoader.loadObj("dirt"));
    // ModelTexture blockAtlas = new ModelTexture(loader.loadTexture("blockAtlas"));
    // blockAtlas.setNumberOfRows(6);
    // blockModel = new TexturedModel(rawBlock, blockAtlas);
  }

  /**
   * 3D distance between block and a 3D point.
   *
   * @param pos distance from block to this point
   * @return distance in units
   */
  public float getDistanceFrom(Vector3f pos) {
    return super.getPosition().distance(pos);
  }

  /**
   * Squared 3D distance between block and a 3D point. Squared is faster
   *
   * @param pos distance from block to this point
   * @return distance in units
   */
  public float getDistanceSquaredFrom(Vector3f pos) {
    return super.getPosition().distanceSquared(pos);
  }

  /**
   * Assuming the objects are on the same Z-plane, this gets the 2D distance between the objects.
   *
   * @param pos X and Y coordinates for a position in the world
   * @return distance in units
   */
  public float get2dDistanceFrom(Vector2f pos) {
    return new Vector2f(super.getPosition().x, super.getPosition().y).distance(pos);
  }

  /**
   * Assuming the objects are on the same Z-plane, this gets the squared 2D distance between the
   * objects. Squared is faster!
   *
   * @param pos X and Y coordinates for a position in the world
   * @return distance in units
   */
  public float get2dDistanceSquaredFrom(Vector2f pos) {
    return new Vector2f(super.getPosition().x, super.getPosition().y).distanceSquared(pos);
  }

  /**
   * Add damage to the block.
   *
   * @param blockDamagerClientId clientId of the client that damaged the block
   * @param damage damage done to the block
   */
  public void increaseDamage(int blockDamagerClientId, float damage) {
    if (isDestroyed()) {
      return;
    }
    this.damage += damage;
    float percentIntegrity = (this.hardness - this.damage) / hardness;
    if (percentIntegrity < .25) {
      setTextureIndex(3);
    } else if (percentIntegrity < .5) {
      setTextureIndex(2);
    } else if (percentIntegrity < .75) {
      setTextureIndex(1);
    } else {
      setTextureIndex(0);
    }

    if (this.damage > this.hardness) {
      setDestroyedBy(NetPlayerMaster.getNetPlayerById(blockDamagerClientId));
      setDestroyed(true); // Destroy block
      Game.getMap().replaceWithAirBlock(new Vector2i(getGridX(), getGridY()));
    }
  }

  public Entity getDestroyedBy() {
    return destroyedBy;
  }

  private void setDestroyedBy(Entity destroyedBy) {
    this.destroyedBy = destroyedBy;
  }

  /**
   * Only change destroyed status via this method, never call the super directly. This will trigger
   * the abstract method onDestroy() for the block. The block type will determine what happens
   *
   * @param destroyed true if you want to destroy the block
   */
  public void setDestroyed(boolean destroyed) {
    super.setDestroyed(destroyed);
    if (destroyed) {
      DebrisMaster.generateDebris(this);
      onDestroy();
    }
    // Game.getMap().destroyBlock(this);
  }

  /** Removes block from the world without triggering onDestroy actions. */
  public void remove() {
    super.setDestroyed(true);
  }

  public float getDim() {
    return dim;
  }

  /**
   * Specifies what happens when this block is destroyed. Must be implemented by the block child
   * classes.
   */
  protected abstract void onDestroy();

  public abstract TexturedModel getDebrisModel();

  public BlockMaster.BlockTypes getType() {
    return type;
  }

  public float getMass() {
    return mass;
  }

  public Vector3f getMoveTo() {
    return moveTo;
  }

  public Vector3f getMoveStartPos() {
    return moveStartPos;
  }

  public float getMoveDistance() {
    return moveDistance;
  }

  /**
   * Settings a moveTo target will start the block to move there. Make sure speed and acceleration
   * parameters allow the movement in this direction. Currently only downwards acceleration
   * (=Gravity) is implemented. If you need other impulses, you have to implement them via an
   * acceleration setter.
   *
   * @param moveTo target to move the block to
   * @param moveDelay seconds to wait before moving
   */
  public void setMoveTo(Vector3f moveTo, float moveDelay) {
    this.moveDelay = moveDelay;
    this.moveTo = moveTo;
    this.speed = new Vector3f(0, 0, 0);
    this.moveStartPos = new Vector3f(getPosition());
    this.moveDistance = getPosition().distance(moveTo);
  }

  /**
   * Add the acceleration to the speed once per second, normalized by frame time.
   *
   * @param delta duration of the last frame
   */
  public void accelerate(float delta) {
    this.speed.add(new Vector3f(acceleration).mul(delta));
  }

  public Vector3f getSpeed() {
    return speed;
  }

  public void decreaseMoveDelay(float delta) {
    moveDelay -= delta;
  }

  public float getMoveDelay() {
    return moveDelay;
  }

  /**
   * Check if a block has to wait or can move.
   *
   * @return True if the move delay has run out
   */
  public boolean canMove() {
    return moveDelay <= 0;
  }

  /**
   * Determines the direction the block should turn while shaking.
   *
   * @return true if the block should turn to the left
   */
  private boolean isShakeLeft() {
    return shakeLeft;
  }

  /** Reverse the direction the block is turning while shaking. */
  private void toggleShake() {
    shakeLeft = !shakeLeft;
  }

  /**
   * Manipulate the X rotation with some variance to simulate shaking of the block. Call this every
   * frame for as long as you want the block to jiggle.
   */
  public void shake() {
    float speed = (float) Game.dt() * (new Random().nextFloat() * 60f + 45);
    if (isShakeLeft()) {
      setRotX(getRotX() - speed);
      if (getRotX() < -3) {
        toggleShake();
      }
    } else {
      setRotX(getRotX() + speed);
      if (getRotX() > 3) {
        toggleShake();
      }
    }
  }

  public int getGridX() {
    return gridX;
  }

  public void setGridX(int gridX) {
    this.gridX = gridX;
  }

  public int getGridY() {
    return gridY;
  }

  public void setGridY(int gridY) {
    this.gridY = gridY;
  }

  @Override
  public String toString() {
    return getType().toString();
  }
}
