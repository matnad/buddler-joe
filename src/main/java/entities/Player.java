package entities;

import static entities.items.ItemMaster.ItemTypes.TORCH;
import static game.Game.Stage.PLAYING;
import static game.stages.Playing.showFreezeOverlay;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import audio.AudioMaster;
import audio.Source;
import engine.io.InputHandler;
import entities.blocks.AirBlock;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import entities.collision.BoundingBox;
import entities.items.ItemMaster;
import entities.items.Star;
import entities.items.Steroids;
import game.Game;
import game.stages.Playing;
import gui.tutorial.Tutorial;
import net.packets.block.PacketBlockDamage;
import net.packets.playerprop.PacketPos;
import net.packets.playerprop.PacketVelocity;
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
  private static final float digInterval = 0.2f; // Number of dig updates per second
  private static final float torchPlaceDelay = 5f;
  // Resources and Stats
  public int currentGold; // Current coins
  private float digDamage; // Damage per second when colliding with blocks
  private Block lastDiggedBlock = null;
  private float lastDiggedBlockDamage = 0;
  private float digIntervallTimer = 0;
  // Vector & Velocity based speed
  private boolean isJumping = false; // Can't Jump while in the air
  private boolean sendVelocityToServer = false; // If we need to update velocity this frame
  // Other
  private boolean controlsDisabled;

  private float currentRunSpeed;
  private float currentDigDamage;
  private float currentJumpPower;
  private float freezeDuration;
  private float ampedDuration;
  private float torchTimeout = torchPlaceDelay;

  private Source digSoundDirt = new Source(AudioMaster.SoundCategory.DIG);
  private Source pickSoundStone = new Source(AudioMaster.SoundCategory.PICK);
  private Source explosionSound = new Source(AudioMaster.SoundCategory.EXPLOSION);
  private Source fuseSound = new Source(AudioMaster.SoundCategory.FUSE);
  private Source heartSound = new Source(AudioMaster.SoundCategory.HEART);
  private Source freezeSound = new Source(AudioMaster.SoundCategory.FREEZE);
  private Source damageSound = new Source(AudioMaster.SoundCategory.DAMAGE);
  private Source gameOverSound = new Source(AudioMaster.SoundCategory.GAMEOVER);
  private boolean playDigSoundDirt = false;
  private boolean playPickSoundStone = false;

  /**
   * Spawn the ServerPlayer. This will be handled differently in the future when we rework the
   * ServerPlayer class structure.
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
    controlsDisabled = false;
    heartSound.setVolume(0.3f);
  }

  /** Testconstructor for Unit Tests to create a Test User with Mockito. */
  public Player() {
    super(new Vector3f(1, 2, 3), 1.0f, 1.0f, 1.0f);
  }

  public static float getDigInterval() {
    return digInterval;
  }

  /**
   * Updates player position every frame.
   *
   * <p>Called every frame and does all the input reading, position updating, entities.collision
   * handling and potentially server communication
   */
  public void move() {

    // Dont update during the first second
    if (Game.getActiveCamera().isIntro()) {
      return;
    }

    // Check if player can move
    controlsDisabled = isDefeated() || Game.getChat().isEnabled();

    // Apply freeze
    currentRunSpeed = runSpeed;
    currentDigDamage = digDamage;
    currentJumpPower = jumpPower;
    if (frozen) {
      if (!freezeSound.isPlaying()) {
        freezeSound.playIndex(1);
      }
      // Calculate freeze factor
      freezeDuration += Game.dt();
      float freezeFactor;
      if (freezeDuration > 2) {
        freezeFactor = Math.max((freezeDuration - 2) / Star.getFreezeTime() * .4f, 0);
      } else {
        freezeFactor = 0;
      }

      // Slow all actions by freeze factor
      currentRunSpeed = runSpeed * freezeFactor;
      currentDigDamage = digDamage * freezeFactor;
      currentJumpPower = jumpPower * freezeFactor;
    } else if (amped) {
      // Apply steroids
      currentRunSpeed *= Steroids.getMovementMultiplier();
      currentJumpPower *= Steroids.getJumpPowerMultiplier();
      currentDigDamage *= Steroids.getDigDamageMultiplier();

      ampedDuration += Game.dt();
      if (ampedDuration > Steroids.getSteroidsTime()) {
        deAmp();
      }
    }

    sendVelocityToServer = false;
    collideWithBlockAbove = null;
    collideWithBlockBelow = null;

    updateCloseBlocks(BlockMaster.getBlocks());
    // We don't want to check entities.collision for all block every frame

    if (Game.getActiveStages().size() == 1 && Game.getActiveStages().get(0) == PLAYING) {
      // Only check inputs if no other stage is active (stages are menu screens)
      checkInputs(); // See which relevant keys are pressed
    } else {
      stopVelocityX();
      stopVelocityY();
      currentDigDamage = 0;
    }

    // Update position by distance travelled
    // float distance = (float) (currentSpeed * Game.dt());
    // super.increasePosition(distance, 0, 0);
    // Turn character by the turnSpeed (which is set to make a nice turning animation when
    // changing direction)

    // Apply gravity and "slow horizontal correction"
    float ipfX = interpolationFactor;
    float ipfY = interpolationFactor;
    if (isInAir) {
      goalVelocity.y += gravity * Game.dt();
      ipfX /= 5;
      ipfY = Math.min(1, ipfY * 2);
    }

    // Linear Interpolation of current velocity and goal velocity
    currentVelocity.x += (goalVelocity.x - currentVelocity.x) * ipfX;
    currentVelocity.y += (goalVelocity.y - currentVelocity.y) * ipfY;

    // Move player
    increasePosition(new Vector3f(currentVelocity).mul((float) Game.dt()));
    enforceMapBounds();

    // Handle character rotation (check run direction see if we need to rotate more)
    this.increaseRotation(0, (float) (getCurrentTurnSpeed() * Game.dt()), 0);

    playDigSoundDirt = false; // Will be set to true if we dig this frame
    // Handle collisions, we only check close blocks to optimize performance
    // Distance is much cheaper to check than overlap
    playPickSoundStone = false;
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
      if (Tutorial.Topics.TORCH.isEnabled() && !Tutorial.Topics.TORCH.isActive()) {
        Tutorial.Topics.setActive(Tutorial.Topics.TORCH, true);
      }
      turnHeadlightOn();
    }

    // Play a random dig sound
    if (playDigSoundDirt && !digSoundDirt.isPlaying()) {
      digSoundDirt.playRandom();
    }

    if (playPickSoundStone && !pickSoundStone.isPlaying()) {
      pickSoundStone.playRandom();
    }

    if (sendVelocityToServer) {
      new PacketVelocity(currentVelocity.x, currentVelocity.y, goalVelocity.x, goalVelocity.y)
          .sendToServer();
    }

    // Update server once per second
    if (Game.isOncePerSecond()) {
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
    Tutorial.Topics.setActive(Tutorial.Topics.CRUSHED, true);

    // decreaseCurrentLives();

    // Send to server to inform
    informServerOfLifeChange(-1);

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
      // Move player to an empty space and update position for all players
      setPosition(Game.getMap().gridToWorld(closestGridPos));
      new PacketPos(getPositionXy().x, getPositionXy().y, getRotY()).sendToServer();
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
        // Have a grace distance, if the overlap is too large, we reset to position to prevent
        // hard clipping
        if (getPosition().y + 0.1 < e.getMaxY()) {
          setPositionY(e.getMaxY());
        }
        // Reset jumping ability and downwards momentum
        if (goalVelocity.y < 0) {
          setGoalVelocityY(0);
          stopVelocityY();
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
          setGoalVelocityY(0);
          stopVelocityY();
        }
      } else {
        isJumping = false; // Walljumps! Felt cute. Might delete later.
        setPositionX((float) (getPosition().x - currentVelocity.x * Game.dt()));
        stopVelocityX();
        // Dig blocks whenever we collide horizontal
        digBlock(block);
      }
    }
  }

  /**
   * What happens PER FRAME when we dig a block. Sends updates to the server every few frames,
   * specified in digInterval
   *
   * @param block block to dig
   */
  private void digBlock(Block block) {

    Tutorial.Topics.DIGGING.stopTopic();

    // Show block type tutorials
    if (Tutorial.Topics.STONE.isEnabled()
        && !Tutorial.Topics.STONE.isActive()
        && block.getType() == BlockMaster.BlockTypes.STONE) {
      Tutorial.Topics.setActive(Tutorial.Topics.STONE, true);
    } else if (Tutorial.Topics.GOLD.isEnabled()
        && !Tutorial.Topics.GOLD.isActive()
        && block.getType() == BlockMaster.BlockTypes.GOLD) {
      Tutorial.Topics.setActive(Tutorial.Topics.GOLD, true);
    } else if (Tutorial.Topics.OBSIDIAN.isEnabled()
        && !Tutorial.Topics.OBSIDIAN.isActive()
        && block.getType() == BlockMaster.BlockTypes.OBSIDIAN) {
      Tutorial.Topics.setActive(Tutorial.Topics.OBSIDIAN, true);
    }

    // Check if we dig the same block as last time, otherwise throw progress away
    if (lastDiggedBlock != block) {
      lastDiggedBlock = block;
      lastDiggedBlockDamage = 0;
      digIntervallTimer = 0;
    } else if (lastDiggedBlockDamage > 0.1f) {
      // Queue sound
      switch (block.getType()) {
        case DIRT:
        case QMARK:
          playDigSoundDirt = true;
          break;
        case GOLD:
        case STONE:
        case OBSIDIAN:
          playPickSoundStone = true;
          break;
        default:
          break;
      }
    }

    // Update damage and time, save locally
    digIntervallTimer += Game.dt();
    lastDiggedBlockDamage += (float) (currentDigDamage * Game.dt());

    // Check if we hit time threshold to send update to the server
    if (digIntervallTimer >= digInterval) {
      // Make sure we don't send invalid packets. If framerate is below 5, the player will dig
      // slower but not violate any rules
      lastDiggedBlockDamage = Math.min(lastDiggedBlockDamage, currentDigDamage * digInterval);
      new PacketBlockDamage(block.getGridX(), block.getGridY(), lastDiggedBlockDamage)
          .sendToServer();
      // Reset timer without losing overflow
      digIntervallTimer -= digIntervallTimer;
      lastDiggedBlockDamage = 0;
    }
  }

  /** VERY simple jump. */
  private void jump() {
    if (!isJumping) {
      setGoalVelocityY(currentJumpPower);
      isJumping = true;
    }
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
      setGoalVelocityX(0);
      return;
    }

    torchTimeout += Game.dt();
    if (InputHandler.isKeyPressed(GLFW_KEY_E)) {
      if (InputHandler.isPlacerMode()) {
        MousePlacer.cancelPlacing();
      } else if (torchTimeout >= torchPlaceDelay) {
        torchTimeout = 0;
        Tutorial.Topics.TORCH.stopTopic();
        placeItem(TORCH);
      }
    }

    if (InputHandler.isKeyPressed(GLFW_KEY_W) || InputHandler.isKeyPressed(GLFW_KEY_SPACE)) {
      jump();
      Tutorial.Topics.MOVEMENT.stopTopic();
    }

    if (InputHandler.isKeyDown(GLFW_KEY_A) && InputHandler.isKeyDown(GLFW_KEY_D)) {
      return;
    }

    if (InputHandler.isKeyDown(GLFW_KEY_A) && goalVelocity.x != -currentRunSpeed) {
      // Set goal velocity
      Tutorial.Topics.MOVEMENT.stopTopic();
      setGoalVelocityX(-currentRunSpeed);
    } else if (InputHandler.isKeyReleased(GLFW_KEY_A) && goalVelocity.x != 0) {
      setGoalVelocityX(0);
    }
    if (InputHandler.isKeyDown(GLFW_KEY_D) && goalVelocity.x != currentRunSpeed) {
      // Set goal velocity
      Tutorial.Topics.MOVEMENT.stopTopic();
      setGoalVelocityX(currentRunSpeed);
    } else if (InputHandler.isKeyReleased(GLFW_KEY_D) && goalVelocity.x != 0) {
      setGoalVelocityX(0);
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

  public static float getTorchPlaceDelay() {
    return torchPlaceDelay;
  }

  /**
   * Triggers FreezeOverlay. And sets Player as frozen and removes Steroids if present.
   *
   * @param initial the current freeze status.
   */
  public void freeze(boolean initial) {
    frozen = true;
    stopVelocityX();
    if (initial) {
      showFreezeOverlay();
      freezeDuration = 0;
      freezeSound.playIndex(2);
    }
    if (amped) {
      deAmp();
    }
  }

  public void defreeze() {
    freezeSound.stop();
    this.frozen = false;
  }

  /** Apply Steroids and remove freeze. */
  public void ampUp() {
    amped = true;
    ampedDuration = 0;
    if (frozen) {
      defreeze();
    }
  }

  public void deAmp() {
    amped = false;
  }

  private void setGoalVelocityX(float x) {
    if (goalVelocity.x != x) {
      goalVelocity.x = x;
      sendVelocityToServer = true;
    }
  }

  private void setGoalVelocityY(float y) {
    if (goalVelocity.y != y) {
      goalVelocity.y = y;
      sendVelocityToServer = true;
    }
  }

  private void stopVelocityX() {
    if (currentVelocity.x != 0) {
      currentVelocity.x = 0;
      sendVelocityToServer = true;
    }
  }

  private void stopVelocityY() {
    if (currentVelocity.y != 0) {
      currentVelocity.y = 0;
      sendVelocityToServer = true;
    }
  }

  public void playFreezeSound() {
    freezeSound.playIndex(0);
  }

  public boolean getFreezeIsPlaying() {
    return freezeSound.isPlaying();
  }

  public void playExplosionSound(int i) {
    explosionSound.playIndex(i);
    logger.debug("play sound" + "index" + i);
  }

  public boolean getExplosionIsPlaying() {
    return explosionSound.isPlaying();
  }

  public void setExlosionSoundOff() {
    if (explosionSound.isPlaying()) {
      explosionSound.stop();
    }
  }

  public void playFuseSound() {
    fuseSound.playIndex(0);
  }

  public boolean getFuseIsPlaying() {
    return fuseSound.isPlaying();
  }

  public void setFuseSoundOff() {
    if (fuseSound.isPlaying()) {
      fuseSound.stop();
    }
  }

  public void playDamageSound(int i) {
    damageSound.playIndex(i);
    logger.debug("play sound" + "index" + i);
  }

  public void playHeartSound(int i) {
    heartSound.playIndex(i);
  }

  public boolean getHeartIsPlaying() {
    return heartSound.isPlaying();
  }

  public void setHeartSoundOff() {
    if (heartSound.isPlaying()) {
      heartSound.stop();
    }
  }

  public void playGameOverSound() {
    gameOverSound.playIndex(1);
  }
}
