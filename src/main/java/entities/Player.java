package entities;

import static entities.items.ItemMaster.ItemTypes.DYNAMITE;
import static entities.items.ItemMaster.ItemTypes.TORCH;
import static game.Game.Stage.PLAYING;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import collision.BoundingBox;
import engine.io.InputHandler;
import engine.models.TexturedModel;
import entities.blocks.AirBlock;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
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
 * <p>Handles player collision with blocks (might move partly to a different class)
 *
 * <p>Defines global gravity (this will move to a different class)
 */
public class Player extends NetPlayer {

  public static final Logger logger = LoggerFactory.getLogger(Player.class);

  // Resources and Stats
  public int currentGold; // Current coins
    private int currentLives;
  private float digDamage; // Damage per second when colliding with blocks

  // Movement Related
  public static final float gravity = -45; // Units per second
  private static final float runSpeed = 20; // Units per second
  private static final float turnSpeed = 720; // Degrees per second
  private static final float jumpPower = 25; // Units per second

  private static final float collisionPushOffset = 0.1f;
  private static final float angle45 = (float) (45 * Math.PI / 180);

  private Block collideWithBlockAbove;
  private Block collideWithBlockBelow;

  private float currentSpeed = 0;
  private float currentTurnSpeed = 0;
  private float upwardsSpeed = 0;

  private List<Block> closeBlocks;

  private boolean isInAir = false; // Can't Jump while in the air

  /**
   * Spawn the Player. This will be handled differently in the future when we rework the Player
   * class structure.
   *
   * @param model player model
   * @param username username of the player
   * @param position world coordinates for player position
   * @param rotX rotation along X axis
   * @param rotY rotation along Y axis
   * @param rotZ rotation along Z axis
   * @param scale scale factor
   */
  public Player(
      String username,
      TexturedModel model,
      Vector3f position,
      float rotX,
      float rotY,
      float rotZ,
      float scale) {
    super(0, username, model, position, rotX, rotY, rotZ, scale);
    digDamage = 1;
    currentGold = 0;
    currentLives = 2;
  }

  /**
   * Updates player position every frame.
   *
   * <p>Called every frame and does all the input reading, position updating, collision handling and
   * potentially server communication
   */
  public void move() {

    collideWithBlockAbove = null;
    collideWithBlockBelow = null;

    updateCloseBlocks(BlockMaster.getBlocks()); // We don't want to check collision for all blocks
    // every frame

    if (Game.getActiveStages().size() == 1 && Game.getActiveStages().get(0) == PLAYING) {
      // Only check inputs if no other stage is active (stages are menu screens)
      checkInputs(); // See which relevant keys are pressed
      digDamage = 1;
    } else {
      currentSpeed = 0;
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
    float distance = (float) (currentSpeed * Game.window.getFrameTimeSeconds());
    super.increasePosition(distance, 0, 0);
    // Turn character by the turnSpeed (which is set to make a nice turning animation when
    // changing direction)
    this.increaseRotation(0, (float) (currentTurnSpeed * Game.window.getFrameTimeSeconds()), 0);

    // Apply gravity to upwardspeed and change vertical position
    upwardsSpeed += gravity * Game.window.getFrameTimeSeconds();
    super.increasePosition(0, (float) (upwardsSpeed * Game.window.getFrameTimeSeconds()), 0);

    // Handle collisions, we only check close blocks to optimize performance
    // Distance is much cheaper to check than overlap
    for (Block closeBlock : closeBlocks) {
      handleCollision(closeBlock);
    }

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
        && (currentSpeed != 0 || upwardsSpeed != 0 || currentTurnSpeed != 0)) {
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
   * @param block A block to check collision with.
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
        super.increasePosition(0, (float) -(upwardsSpeed * Game.window.getFrameTimeSeconds()), 0);
        // Have a grace distance, if the overlap is too large, we reset to position to prevent
        // hard clipping
        if (getPosition().y + 0.1 < e.getMaxY()) {
          setPositionY(e.getMaxY());
        }
        // Reset jumping ability and downwards momentum
        if (upwardsSpeed < 0) {
          upwardsSpeed = 0;
        }
        isInAir = false;
        // If we hold S, dig down
        if (InputHandler.isKeyDown(GLFW_KEY_S)) {
          digBlock(block);
        }
      } else if (theta >= angle45 * 3) {
        // From below
        collideWithBlockAbove = block;
        // Reset Position to below the block, this doesnt flicker since we are falling
        setPositionY(e.getMinY() - p.getDimY());
        // Stop jumping up if we hit something above, will start accelerating down
        if (upwardsSpeed > 0) {
          upwardsSpeed = 0;
        }
      } else {
        if (direction.x > 0) {
          // Have a small offset for smoother collision
          setPositionX(e.getMaxX() + p.getDimX() / 2 + collisionPushOffset);
          currentSpeed = 0; // Stop moving
          isInAir = false; // Walljumps! Felt cute. Might delete later.
        } else {
          setPositionX(e.getMinX() - p.getDimX() / 2 - collisionPushOffset);
          currentSpeed = 0;
          isInAir = false;
        }
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
    if (!isInAir) {
      this.upwardsSpeed = jumpPower;
      isInAir = true;
    }
  }

  /**
   * Maintain a list with blocks that are closer than the specified distance. This is used to only
   * check close block for collision or other interaction
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
   * get all surrounding blocks This is used to only check close block for collision or other
   * interaction
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

    if (Game.getChat().isEnabled()) {
      currentSpeed = 0;
      return;
    }

    if (InputHandler.isKeyPressed(GLFW_KEY_Q)) {
      if (InputHandler.isPlacerMode()) {
        MousePlacer.cancelPlacing();
      } else {
        placeItem(DYNAMITE);
      }
    }

    if (InputHandler.isKeyPressed(GLFW_KEY_E)) {
      if (InputHandler.isPlacerMode()) {
        MousePlacer.cancelPlacing();
      } else {
        placeItem(TORCH);
      }
    }

    // SIMPLE Movement
    if (InputHandler.isKeyDown(GLFW_KEY_A)) {
      this.currentSpeed = -runSpeed;
      this.currentTurnSpeed = -turnSpeed;
    } else if (InputHandler.isKeyDown(GLFW_KEY_D)) {
      this.currentSpeed = runSpeed;
      this.currentTurnSpeed = turnSpeed;
    } else {
      this.currentSpeed = 0;
      currentTurnSpeed = 0;
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

  public void increaseCurrentLives() {
      if (currentLives < 2) {
          currentLives++;
      }
      PacketLifeStatus lives = new PacketLifeStatus(String.valueOf(currentLives));
    lives.processData();
  }

  public void decreaseCurrentLives() {
    currentLives--;
    //hier send paket
    PacketLifeStatus lives = new PacketLifeStatus(String.valueOf(currentLives));
    lives.processData();
  }

  public int getCurrentLives() {
      return currentLives;
  }

}
