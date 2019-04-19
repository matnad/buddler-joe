package entities;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import entities.collision.BoundingBox;
import entities.light.Light;
import entities.light.LightMaster;
import game.Game;
import gui.text.Nameplate;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;

/**
 * This will be reworked very shortly so I will not fully document it. A LOT will change and it is
 * not really used now. Ignore the class please.
 */
public class NetPlayer extends Entity {

  private static final float joeModelSize = .2f;
  private static final float ripModelSize = .5f;
  private static final Vector3f[] lampColors = {
    new Vector3f(1, 1, 1).normalize(),
    new Vector3f(3f, 1, 1).normalize(),
    new Vector3f(1, 3f, 1).normalize(),
    new Vector3f(1, 1, 3f).normalize(),
    new Vector3f(3f, 1, 3f).normalize()
  };
  private static TexturedModel joeModel;
  private static TexturedModel ripModel;
  private static int counter;
  private int clientId;
  private String username;
  private Light headLight;
  private Light headLightGlow;
  // private DirectionalUsername directionalUsername;
  private Nameplate nameplate;

  private boolean defeated;

  // Movement related
  static final float angle45 = (float) (45 * Math.PI / 180);
  Block collideWithBlockAbove;
  Block collideWithBlockBelow;

  static final float jumpPower = 28; // Units per second
  public static final float gravity = -45; // Units per second
  static final float runSpeed = 20; // Units per second
  static final float turnSpeed = 720; // Degrees per second

  static final float interpolationFactor = 0.15f; // Rate of acceleration via LERP
  Vector3f currentVelocity = new Vector3f();
  Vector3f goalVelocity = new Vector3f();

  List<Block> closeBlocks;

  boolean isInAir = false;

  /**
   * Create a net player.
   *
   * <p>REWORK IN PROGRESS
   *
   * @param clientId client id is given out by server
   * @param position 3D world coords
   * @param rotX rotation x axis
   * @param rotY rotation y axis
   * @param rotZ rotation z axis
   * @param username username of net player
   */
  public NetPlayer(
      int clientId, String username, Vector3f position, float rotX, float rotY, float rotZ) {
    super(getJoeModel(), position, rotX, rotY, rotZ, getJoeModelSize());

    this.clientId = clientId;
    this.username = username;
    this.defeated = false;
    int colorIdx = counter++ % lampColors.length;
    headLight =
        LightMaster.generateLight(
            LightMaster.LightTypes.SPOT, getHeadlightPosition(), lampColors[colorIdx]);
    headLight.setCutoff(25f);
    headLightGlow =
        LightMaster.generateLight(
            LightMaster.LightTypes.TORCH, getHeadlightPosition(), new Vector3f(1f, 1, 1));
    // headLightGlow.setDirection(new Vector3f(0,1,0));
    // headLightGlow.setCutoff(110);

    nameplate = new Nameplate(this);
  }

  /**
   * Load the player model before creating player models.
   *
   * @param loader main loader
   */
  public static void init(Loader loader) {
    RawModel rawPlayer = loader.loadToVao(ObjFileLoader.loadObj("joe"));
    joeModel = new TexturedModel(rawPlayer, new ModelTexture(loader.loadTexture("uvjoe")));

    // To prevent some problems with normals and for a more comic style look
    joeModel.getTexture().setUseFakeLighting(true);
    joeModel.getTexture().setShineDamper(.3f);

    RawModel rawTomb = loader.loadToVao(ObjFileLoader.loadObj("tomb"));
    ripModel = new TexturedModel(rawTomb, new ModelTexture(loader.loadTexture("tomb")));
  }

  /**
   * Called every frame to update the position of the NetPlayer.
   * Collision for every player is calculated locally.
   * Once per second the positions are synced.
   *
   * */
  public void update() {

    collideWithBlockAbove = null;
    collideWithBlockBelow = null;

    updateCloseBlocks(BlockMaster.getBlocks());

    float ipfX = interpolationFactor;
    if (isInAir) {
      goalVelocity.y += gravity * Game.window.getFrameTimeSeconds();
      ipfX /= 5;
    }

    // Linear Interpolation of current velocity and goal velocity
    currentVelocity.x += (goalVelocity.x - currentVelocity.x) * ipfX;
    currentVelocity.y += (goalVelocity.y - currentVelocity.y) * interpolationFactor;

    // Move player
    increasePosition(new Vector3f(currentVelocity).mul((float) Game.window.getFrameTimeSeconds()));

    // Handle character rotation (check run direction see if we need to rotate more)
    this.increaseRotation(
        0, (float) (getCurrentTurnSpeed() * Game.window.getFrameTimeSeconds()), 0);

    for (Block closeBlock : closeBlocks) {
      handleNetPlayerCollision(closeBlock);
    }

    isInAir = collideWithBlockBelow == null;

    nameplate.updateString();
  }

  /**
   * Maintain a list with blocks that are closer than the specified distance. This is used to only
   * check close block for entities.collision or other interaction
   *
   * @param blocks Usually all blocks {@link BlockMaster#getBlocks()}
   */
  void updateCloseBlocks(List<Block> blocks) {
    List<Block> closeBlocks = new ArrayList<>();
    // Only 2D (XY) for performance
    for (Block block : blocks) {
      if (block.get2dDistanceSquaredFrom(super.getPositionXy()) <= 64) {
        closeBlocks.add(block);
      }
    }
    this.closeBlocks = closeBlocks;
  }

  /**
   * Check if the player overlaps with a block. Determine from which direction the overlap is and
   * handle it appropriately. This is still very basic but it works. Can be improved if we have
   * time. -> Has been improved to be vector and angle based now. Works much smoother.
   *
   * @param block A block to check entities.collision with.
   */
  private void handleNetPlayerCollision(Block block) {
    // Make this mess readable
    BoundingBox p = super.getBbox(); // PlayerBox
    BoundingBox e = block.getBbox(); // EntityBox

    // Check if we collide with the block
    if (this.collidesWith(block, 2)) {
      Vector3f direction = new Vector3f(p.getCenter()).sub(e.getCenter());
      direction.z = 0;
      float theta = direction.angle(new Vector3f(0, 1, 0));
      if (theta <= angle45) {
        // From above
        collideWithBlockBelow = block;
        // Have a grace distance, if the overlap is too large, we reset to position to prevent
        // hard clipping
        if (getPosition().y + 0.1 < e.getMaxY()) {
          setPositionY(e.getMaxY());
        }
      } else if (theta >= angle45 * 3) {
        // From below
        collideWithBlockAbove = block;
        // Reset Position to below the block, this doesnt flicker since we are falling
        setPositionY(e.getMinY() - p.getDimY());
        // Stop jumping up if we hit something above, will start accelerating down
      } else {
        setPositionX(
            (float) (getPosition().x - currentVelocity.x * Game.window.getFrameTimeSeconds()));
      }
    }
  }

  float getCurrentTurnSpeed() {
    float currentTurnSpeed;
    if (goalVelocity.x == -runSpeed) {
      currentTurnSpeed = -turnSpeed;
      if (getRotY() <= -90) {
        currentTurnSpeed = 0;
        setRotY(-90);
      }
    } else if (goalVelocity.x == runSpeed) {
      currentTurnSpeed = turnSpeed;
      if (getRotY() >= 90) {
        currentTurnSpeed = 0;
        setRotY(90);
      }
    } else {
      currentTurnSpeed = 0;
    }
    return currentTurnSpeed;
  }

  public static TexturedModel getJoeModel() {
    return joeModel;
  }

  public static float getJoeModelSize() {
    return joeModelSize;
  }

  public boolean isDefeated() {
    return defeated;
  }

  public static float getRipModelSize() {
    return ripModelSize;
  }

  public static TexturedModel getRipModel() {
    return ripModel;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public void turnHeadlightOff() {
    headLight.setBrightness(0);
    headLightGlow.setBrightness(0);
  }

  public void turnHeadlightOn() {
    headLight.setBrightness(8);
    headLightGlow.setBrightness(2f);
  }

  private Vector3f getHeadlightPosition() {
    float rotCompX = ((getRotY()) / 90) * getBbox().getDimX() / 2 * .8f;
    return new Vector3f(getPosition()).add(rotCompX, 4.2f, 0);
  }

  private void updateHeadlightPosition() {
    headLight.setPosition(getHeadlightPosition());
    headLightGlow.setPosition(getHeadlightPosition().add(0, 0.5f, 6f));
  }

  private void updateHeadlightDirection() {
    Vector3f direction = new Vector3f(0, 0, 1).rotateY((float) Math.toRadians(getRotY()));
    headLight.setDirection(direction);
  }

  /**
   * Turn the player into a gravestone and disable all controls if it is the active player. Will set
   * the defeated flag for other classes to use.
   *
   * @param defeated can only be true for now. No way to revive a player
   */
  public void setDefeated(boolean defeated) {
    if (defeated) {
      this.defeated = defeated;
      setModel(ripModel);
      setScale(new Vector3f(ripModelSize, ripModelSize, ripModelSize));
      setRotY(0);
      if (getClientId() == Game.getActivePlayer().getClientId()) {
        Game.setActiveCamera(new SpectatorCamera(Game.window, getPosition()));
      }
    }
  }

  public void updateVelocities(Vector3f current, Vector3f goal) {
    currentVelocity = current;
    goalVelocity = goal;
  }

  @Override
  public void setRotY(float rotY) {
    super.setRotY(rotY);
    updateHeadlightDirection();
  }

  @Override
  public void increaseRotation(float dx, float dy, float dz) {
    super.increaseRotation(dx, dy, dz);
    updateHeadlightDirection();
  }

  @Override
  public void increaseRotation(Vector3f spin) {
    increaseRotation(spin.x, spin.y, spin.z);
  }

  @Override
  public void setPosition(Vector3f position) {
    super.setPosition(position);
    updateHeadlightPosition();
  }

  @Override
  public void increasePosition(Vector3f velocity) {
    super.increasePosition(velocity);
    updateHeadlightPosition();
  }

  public static float getJumpPower() {
    return jumpPower;
  }

  public static float getRunSpeed() {
    return runSpeed;
  }
}
