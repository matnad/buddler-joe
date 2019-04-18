package entities;

import static entities.items.ItemMaster.ItemTypes.TORCH;
import static game.Game.Stage.PLAYING;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import engine.io.InputHandler;
import entities.blocks.AirBlock;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import entities.collision.BoundingBox;
import entities.items.ItemMaster;
import game.Game;
import game.stages.Playing;
import java.util.ArrayList;
import java.util.List;
import net.packets.block.PacketBlockDamage;
import net.packets.life.PacketLifeStatus;
import net.packets.playerprop.PacketPos;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.MousePlacer;

/**
 * NOTE: This is derived from NetPlayer, but doesn't really use anything from NetPlayer. We will
 * redesign the Hierarchy of Players and NetPlayers soon as we develop the net package. Look at this
 * as derived from Entity directly.
 *
 * <p>A lot in this class is not final but just for testing the game and developing new features.
 *
 * <p>Here all the player controls are set. The player can: - Move left, right, down (A, D, S) -
 * Jump (UP/SPACE) - Place Dynamite (Q) (Temporary) - Reset Position (T) (Temporary)
 *
 * <p>Handles player entities.collision with blocks (might move partly to a different class)
 *
 * <p>Defines global gravity (this will move to a different class)
 */
public class Player extends NetPlayer {

  public static final Logger logger = LoggerFactory.getLogger(Player.class);
  // Movement Related
  public static final float gravity = -45; // Units per second
  private static final float runSpeed = 20; // Units per second
  private static final float interpolationFactor = 0.15f; // Rate of acceleration via LERP
  private static final float turnSpeed = 720; // Degrees per second
  private static final float jumpPower = 28; // Units per second
  private static final float collisionPushOffset = 0.01f;
  private static final float angle45 = (float) (45 * Math.PI / 180);

  // Resources and Stats
  public int currentGold; // Current coins
  private int currentLives;
  private float digDamage; // Damage per second when colliding with blocks
  private Block collideWithBlockAbove;
  private Block collideWithBlockBelow;

  private float currentSpeed = 0;
  private Vector3f currentVelocity = new Vector3f();
  private Vector3f goalVelocity = new Vector3f();
  private float currentTurnSpeed = 0;
  private float upwardsSpeed = 0;
  private boolean frozen = false;
  private final float torchPlaceDelay = 10f;
  private float torchTimeout = torchPlaceDelay;

  private List<Block> closeBlocks;

  private boolean isJumping = false; // Can't Jump while in the air
  private boolean isInAir = false;

  private boolean controlsDisabled;

  /**
   * Spawn the Player. This will be handled differently in the future when we rework the Player
   * class structure.
   *
   * @param username username of the player
   * @param position world coordinates for player position
   * @param rotX rotation along X axis
   * @param rotY rotation along Y axis
   * @param rotZ rotation along Z axis
   */
  public Player(String username, Vector3f position, float rotX, float rotY, float rotZ) {
    super(0, username, position, rotX, rotY, rotZ);
    digDamage = 1;
    currentGold = 0;
    currentLives = 2;
    controlsDisabled = false;
  }

  /**
   * Updates player position every frame.
   *
   * <p>Called every frame and does all the input reading, position updating, entities.collision
   * handling and potentially server communication
   */
  public void move() {

    // Check if player can move
    controlsDisabled = isDefeated() || frozen || Game.getChat().isEnabled();

    collideWithBlockAbove = null;
    collideWithBlockBelow = null;

    updateCloseBlocks(BlockMaster.getBlocks());
    // We don't want to check entities.collision for all block every frame

    if (Game.getActiveStages().size() == 1 && Game.getActiveStages().get(0) == PLAYING) {
      // Only check inputs if no other stage is active (stages are menu screens)
      checkInputs(); // See which relevant keys are pressed
      digDamage = 1;
    } else {
      currentVelocity = new Vector3f();
      digDamage = 0;
    }

    // Stop turning when facing directly left or right
    if (getRotY() <= -90 && currentTurnSpeed < 0) {
      currentTurnSpeed = 0;
      setRotY(-90);
    } else if (getRotY() >= 90 && currentTurnSpeed > 0) {
      currentTurnSpeed = 0;
      setRotY(90);
    }

    // Update position by distance travelled
    // float distance = (float) (currentSpeed * Game.window.getFrameTimeSeconds());
    // super.increasePosition(distance, 0, 0);
    // Turn character by the turnSpeed (which is set to make a nice turning animation when
    // changing direction)

    // Apply gravity to upwardspeed and change vertical position
    float ipfX = interpolationFactor;
    if (isInAir) {
      goalVelocity.y += gravity * Game.window.getFrameTimeSeconds();
      ipfX /= 5;
    }

    // Interpolate velocity

    // currentVelocity.lerp(goalVelocity, ipfX);
    currentVelocity.x += (goalVelocity.x - currentVelocity.x) * ipfX;
    currentVelocity.y += (goalVelocity.y - currentVelocity.y) * interpolationFactor;

    if (Math.abs(currentVelocity.x) < 0.01) {
      currentVelocity.x = 0;
    }

    // Apply gravity to upwardspeed and change vertical position
    // upwardsSpeed += gravity * Game.window.getFrameTimeSeconds();
    // super.increasePosition(0, (float) (upwardsSpeed * Game.window.getFrameTimeSeconds()), 0);
    increasePosition(new Vector3f(currentVelocity).mul((float) Game.window.getFrameTimeSeconds()));
    this.increaseRotation(0, (float) (currentTurnSpeed * Game.window.getFrameTimeSeconds()), 0);

    // Handle collisions, we only check close blocks to optimize performance
    // Distance is much cheaper to check than overlap
    for (Block closeBlock : closeBlocks) {
      handleCollision(closeBlock);
    }

    isInAir = collideWithBlockBelow == null;

    // Check if crushed by a block and resolve it
    resolveCrush();

    // Turn Headlight on/off
    float pctBrightness = Game.getMap().getLightLevel(getPosition().y);
    if (pctBrightness > .7f) {
      turnHeadlightOff();
    } else {
      turnHeadlightOn();
    }

    // Send server update with update
    if (Game.isConnectedToServer()
        && (!currentVelocity.equals(new Vector3f())
            || upwardsSpeed != 0
            || currentTurnSpeed != 0)) {
      new PacketPos(getPositionXy().x, getPositionXy().y, getRotY()).sendToServer();
    }
  }

  /**
   * Check if a player is crushed, remove a life, trigger the damage splash screen and move the
   * player to a safe place.
   */
  private void resolveCrush() {
    // Check if crushed
    if (collideWithBlockAbove == null || collideWithBlockBelow == null) {
      return;
    }
    // Effects when being crushed
    Playing.showDamageTakenOverlay();
    decreaseCurrentLives();
    // Find a place to move the player to
    Vector2i playerGridPos =
        new Vector2i(collideWithBlockBelow.getGridX(), collideWithBlockBelow.getGridY() - 1);
    Vector2i closestGridPos = null;
    float minDistSq = Float.POSITIVE_INFINITY;
    for (AirBlock airBlock : Game.getMap().getAirBlocks(playerGridPos.y, playerGridPos.x)) {
      Vector2i blockGridPos = new Vector2i(airBlock.getGridX(), airBlock.getGridY());
      float distSq = blockGridPos.distanceSquared(playerGridPos);
      if (distSq < minDistSq) {
        minDistSq = distSq;
        closestGridPos = blockGridPos;
      }
    }

    if (closestGridPos == null) {
      // No empty room to teleport to. Just reset y to above ground.
      setPositionY(5);
    } else {
      // Move player to an empty space
      setPosition(Game.getMap().gridToWorld(closestGridPos));
    }
  }

  /**
   * Check if the player overlaps with a block. Determine from which direction the overlap is and
   * handle it appropriately. This is still very basic but it works. Can be improved if we have
   * time. -> Has been improved to be vector and angle based now. Works much smoother.
   *
   * @param block A block to check entities.collision with.
   */
  private void handleCollision(Block block) {
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
        // Undo the position change to keep the player in place
        // super.increasePosition(0, (float) -(upwardsSpeed * Game.window.getFrameTimeSeconds()),
        // 0);
        // Have a grace distance, if the overlap is too large, we reset to position to prevent
        // hard clipping
        if (getPosition().y + 0.1 < e.getMaxY()) {
          setPositionY(e.getMaxY());
        }
        // Reset jumping ability and downwards momentum
        if (goalVelocity.y < 0) {
          goalVelocity.y = 0;
          currentVelocity.y = 0;
        }
        isJumping = false;
        // If we hold S, dig down
        if (InputHandler.isKeyDown(GLFW_KEY_S) && !controlsDisabled) {
          digBlock(block);
        }
      } else if (theta >= angle45 * 3) {
        // From below
        collideWithBlockAbove = block;
        // Reset Position to below the block, this doesnt flicker since we are falling
        setPositionY(e.getMinY() - p.getDimY());
        // Stop jumping up if we hit something above, will start accelerating down
        if (goalVelocity.y > 0) {
          goalVelocity.y = 0;
          currentVelocity.y = 0;
        }
      } else {
        isJumping = false; // Walljumps! Felt cute. Might delete later.
        setPositionX(
            (float) (getPosition().x - currentVelocity.x * Game.window.getFrameTimeSeconds()));
        currentVelocity.x = 0;
        // Dig blocks whenever we collide horizontal
        digBlock(block);
      }
    }
  }

  /**
   * What happens PER FRAME when we dig a block.
   *
   * @param block block to dig
   */
  private void digBlock(Block block) {
    // Scale with frame time
    //    block.increaseDamage((float) (digDamage * Game.window.getFrameTimeSeconds()), this);
    if (Game.isConnectedToServer()) {
      new PacketBlockDamage(
              block.getGridX(),
              block.getGridY(),
              (float) (digDamage * Game.window.getFrameTimeSeconds()))
          .sendToServer();
    }
  }

  /** VERY simple jump. */
  private void jump() {
    // if (!isJumping) {
    //  this.upwardsSpeed = jumpPower;
    //  isJumping = true;
    // }
    if (!isJumping) {
      goalVelocity.y = jumpPower;
      isJumping = true;
    }
  }

  /**
   * Maintain a list with blocks that are closer than the specified distance. This is used to only
   * check close block for entities.collision or other interaction
   *
   * @param blocks Usually all blocks {@link BlockMaster#getBlocks()}
   * @param maxDistance Maximum distance for the block to be considered close
   */
  private void updateCloseBlocks(List<Block> blocks, float maxDistance) {
    List<Block> closeBlocks = new ArrayList<>();
    // Only 2D (XY) for performance
    for (Block block : blocks) {
      if (block.get2dDistanceFrom(super.getPositionXy()) <= block.getDim() + maxDistance) {
        closeBlocks.add(block);
      }
    }
    this.closeBlocks = closeBlocks;
  }

  /**
   * Maintain a list with blocks that are closer than 5 units. A block is 6 units across, this will
   * get all surrounding blocks This is used to only check close block for entities.collision or
   * other interaction
   *
   * @param blocks Usually all blocks {@link BlockMaster#getBlocks()}
   */
  private void updateCloseBlocks(List<Block> blocks) {
    updateCloseBlocks(blocks, 5);
  }

  /**
   * Check for Keyboard and Mouse inputs and process them
   *
   * <p>If chat is enabled, block all commands since we want to type text
   *
   * <p>Simple movement without acceleration or anything. We can improve on this if there is time.
   */
  private void checkInputs() {

    if (controlsDisabled) {
      goalVelocity.x = 0;
      return;
    }

    // if (InputHandler.isKeyPressed(GLFW_KEY_Q)) {
    //  if (InputHandler.isPlacerMode()) {
    //    MousePlacer.cancelPlacing();
    //  } else {
    //    placeItem(DYNAMITE);
    //  }
    // }
    torchTimeout += Game.window.getFrameTimeSeconds();
    if (InputHandler.isKeyPressed(GLFW_KEY_E)) {
      if (InputHandler.isPlacerMode()) {
        MousePlacer.cancelPlacing();
      } else if (torchTimeout >= torchPlaceDelay) {
        torchTimeout = 0;
        placeItem(TORCH);
      }
    }

    // SIMPLE Movement
    // if (InputHandler.isKeyDown(GLFW_KEY_A)) {
    //  this.currentSpeed = -runSpeed;
    //  this.currentTurnSpeed = -turnSpeed;
    // } else if (InputHandler.isKeyDown(GLFW_KEY_D)) {
    //  this.currentSpeed = runSpeed;
    //  this.currentTurnSpeed = turnSpeed;
    // } else {
    //  this.currentSpeed = 0;
    //  currentTurnSpeed = 0;
    // }

    if (InputHandler.isKeyPressed(GLFW_KEY_A) && goalVelocity.x != -runSpeed) {
      // Set goal velocity
      goalVelocity.x = -runSpeed;
      currentTurnSpeed = -turnSpeed;
    } else if (InputHandler.isKeyReleased(GLFW_KEY_A) && goalVelocity.x != 0) {
      goalVelocity.x = 0;
    }

    if (InputHandler.isKeyPressed(GLFW_KEY_D) && goalVelocity.x != runSpeed) {
      // Set goal velocity
      goalVelocity.x = runSpeed;
      currentTurnSpeed = turnSpeed;
    } else if (InputHandler.isKeyReleased(GLFW_KEY_D) && goalVelocity.x != 0) {
      goalVelocity.x = 0;
    }

    if (InputHandler.isKeyPressed(GLFW_KEY_W) || InputHandler.isKeyPressed(GLFW_KEY_SPACE)) {
      jump();
    }

    if (InputHandler.isKeyPressed(GLFW_KEY_T)) {
      super.setPosition(new Vector3f(100, 0, getPosition().z));
    }
  }

  /**
   * GENERATES an item and places it at the cursor, with a left mouse click the player can then
   * deploy the item on the cursor position.
   *
   * @param itemType Item to place as described in {@link ItemMaster.ItemTypes}
   */
  private void placeItem(ItemMaster.ItemTypes itemType) {
    if (InputHandler.isPlacerMode()) {
      // Already placing an item
      return;
    }

    // Generate item and pass it to Mouseplacer
    MousePlacer.placeEntity(
        /*
        Just place it at the player for the first frame, then update to cursor
        We dont want to run raycasting on every frame, just when the placer is active so
        this is an
        acceptable compromise.
        */
        ItemMaster.generateItem(itemType, getPosition()));
  }

  public void increaseCurrentGold(int gold) {
    currentGold += gold;
  }

  public int getCurrentGold() {
    return currentGold;
  }

  /** updates the player's life status and sends the life status to server. */
  public void increaseCurrentLives() {
    if (currentLives < 2) {
      currentLives++;
    }
    PacketLifeStatus lives = new PacketLifeStatus(String.valueOf(currentLives));
    lives.processData();
  }

  /** updates the player's life status and sends the life status to server. */
  public void decreaseCurrentLives() {
    currentLives--;
    // hier send paket
    PacketLifeStatus lives = new PacketLifeStatus(String.valueOf(currentLives));
    lives.processData();
  }

  public int getCurrentLives() {
    return currentLives;
  }

  public void freeze() {
    this.frozen = true;
  }

  public void defreeze() {
    this.frozen = false;
  }

  public boolean isFrozen() {
    return frozen;
  }
}
