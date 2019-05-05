package net.playerhandling;

import entities.NetPlayer;
import entities.Player;
import entities.items.Star;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.chat.PacketChatMessageToClient;
import net.packets.playerprop.PacketDefeated;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Util;

public class ServerPlayer {

  public static final Logger logger = LoggerFactory.getLogger(ServerPlayer.class);

  private String username;
  private int clientId;
  private int curLobbyId;
  private boolean ready;

  private int currentGold;
  private int currentLives;
  private float digDamage;
  private long lastDig;

  private boolean defeated;
  private long timeStampOfGain;
  private boolean frozen;
  private long frozenAt;

  private Vector2f pos2d = new Vector2f();
  private Vector2f pos2dOld = new Vector2f();
  private Vector2f currentVelocity2d = new Vector2f();
  private Vector2f goalVelocity2d = new Vector2f();
  private float rotY;

  private int movementViolations = -1;
  private int damageViolations = 0;
  private boolean kicked = false;

  /**
   * Constructor of the player class to create a new player Creates an instance of the main
   * ServerPlayer class to save the player information on the server side in the playerList.
   * Contains vital information as well as setters and getters to access the information from the
   * server side.
   *
   * @param username Unique username for the player to be set and which is to be displayed in the
   *     game
   * @param clientId to identify the player, unique to every player and assigned by the first login
   *     by the ServerLogic class
   */
  public ServerPlayer(String username, int clientId) {
    this.username = username;
    this.clientId = clientId;
    ready = false;
    curLobbyId = 0;
    currentLives = 2;
    digDamage = 1;
    lastDig = System.currentTimeMillis();
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

  public int getCurLobbyId() {
    return curLobbyId;
  }

  public void setCurLobbyId(int curLobbyId) {
    this.curLobbyId = curLobbyId;
  }

  /**
   * Returns the lobby of the player or null if the player is not in a lobby.
   *
   * @return Lobby the player is in
   */
  public Lobby getLobby() {
    if (curLobbyId == 0) {
      return null;
    } else {
      return ServerLogic.getLobbyList().getLobby(curLobbyId);
    }
  }

  @Override
  public String toString() {
    return "ServerPlayer{"
        + "username='"
        + username
        + '\''
        + ", clientId="
        + clientId
        + ", curLobbyId="
        + curLobbyId
        + '}';
  }

  /**
   * Increases the Gold counter.
   *
   * @param goldValue number by which the currentGold should be increased.
   */
  public void increaseCurrentGold(int goldValue) {
    currentGold += goldValue;
    Lobby lobby = ServerLogic.getLobbyList().getLobby(curLobbyId);
    timeStampOfGain = System.currentTimeMillis() - lobby.getStartedAt();
    if (currentGold >= 2000) { // TODO: set to 3000
      System.out.println("Game Over");
      lobby.gameOver(this);
    }
  }

  public int getCurrentGold() {
    return currentGold;
  }

  public int getCurrentLives() {
    return currentLives;
  }

  /**
   * updates currentLives when getting informations from client.
   *
   * @param currentLives is the actual life status.
   */
  public void setCurrentLives(int currentLives) {
    this.currentLives = currentLives;
    if (currentLives <= 0) {
      setDefeated(true);
    }
  }

  public Vector2f getPos2d() {
    return pos2d;
  }

  /**
   * Update the player position and validate if this "move" violates any rules. This is triggered by
   * the update position packet and should happen once every second.
   *
   * @param pos2d new position received by the player
   */
  public void setPos2d(Vector2f pos2d) {
    this.pos2dOld = this.pos2d;
    this.pos2d = pos2d;
    if (!validatePos2d(1f) && movementViolations > 0) {
      logger.warn(
          getUsername()
              + ": Total "
              + movementViolations
              + " out of 5 allowed movement violations.");
    }
  }

  /**
   * Validate if the previous position and the current position violate any constraints. Check if
   * the velocities are within bounds and if the movement during the last second is possible (with
   * some tolerance).
   *
   * <p>If there are any violations, they will be logged and added to the player's count.
   *
   * @param intervall the frequency of calls to this function per second. 0.5f means twice per
   *     second for example. (Currently not implemented. Intervall always 1.)
   * @return true if there are no violations
   */
  public boolean validatePos2d(float intervall) {

    float allowedRunSpeed = NetPlayer.getRunSpeed();

    if (frozen) {
      allowedRunSpeed *= getFreezeFactor();
    }

    // Sanity check for goal velocity
    if (goalVelocity2d.x > allowedRunSpeed + 1
        || goalVelocity2d.x < -allowedRunSpeed - 1
        || goalVelocity2d.y > NetPlayer.getJumpPower() + 1) {
      logger.warn(getUsername() + ": Goal velocity exceeds allowed limits. 1 violation.");
      logger.debug(
          "allowed: "
              + allowedRunSpeed
              + " factor: "
              + getFreezeFactor()
              + " current: "
              + goalVelocity2d.x);
      addMovementViolations(1);

      // If the velocity is far too high, add another 4 violations
      if (goalVelocity2d.x > allowedRunSpeed * 2
          || goalVelocity2d.x < -allowedRunSpeed * 2
          || goalVelocity2d.y > NetPlayer.getJumpPower() * 2) {
        logger.warn(getUsername() + ": Goal velocity is off the charts! 4 violations.");
        addMovementViolations(4);
      }
      return false;
    }

    // Sanity check for current velocity
    if (currentVelocity2d.x > NetPlayer.getRunSpeed()
        || currentVelocity2d.x < -NetPlayer.getRunSpeed()
        || currentVelocity2d.y > NetPlayer.getJumpPower() + 1) {
      logger.warn(getUsername() + ": Current velocity exceeds allowed limits. 1 violation.");
      logger.debug(
          "allowed: "
              + allowedRunSpeed
              + " factor: "
              + getFreezeFactor()
              + " current: "
              + currentVelocity2d.x);
      addMovementViolations(1);

      // If the velocity is far too high, add another 4 violations
      if (currentVelocity2d.x > NetPlayer.getRunSpeed() * 2
          || currentVelocity2d.x < -NetPlayer.getRunSpeed() * 2
          || currentVelocity2d.y > NetPlayer.getJumpPower() * 2) {
        logger.warn(getUsername() + ": Current velocity is off the charts! 4 violations.");
        addMovementViolations(4);
      }
      return false;
    }

    // Check moved distance with some margin
    // System.out.println("moved: " + Math.abs(pos2dOld.x - pos2d.x));
    if (Math.abs(pos2dOld.x - pos2d.x) > allowedRunSpeed + 5) {
      addMovementViolations(1);
      if (movementViolations > 0) {
        // The first "violation" is for placing the player and will be ignored
        logger.warn(getUsername() + " is moving too fast. 1 violation.");
        // If the moved distance is more than twice the allowed, add another 4 violations
        if (Math.abs(pos2dOld.x - pos2d.x) > allowedRunSpeed * 2) {
          logger.warn(getUsername() + " is teleporting. 4 violations.");
          addMovementViolations(4);
        }
      }
      return false;
    }

    if (Math.abs(pos2dOld.y - pos2d.y) > 200) {
      addMovementViolations(1);
      logger.warn(getUsername() + "  falling or jumping too fast. 1 violation.");
      return false;
    }

    return true;
  }

  /**
   * Validate if the block damage packet violates any constraints (distance, damage amount and
   * frequency).
   *
   * <p>If there are any violations, they will be logged and added to the player's count.
   *
   * @param posX grid X position of the block
   * @param posY grid Y position of the block
   * @param damage damage to be dealt to the block
   * @return true if the block damage packet doesn't violate any constraints
   */
  public boolean validateBlockDamage(int posX, int posY, float damage) {

    // If player has an active dynamite, we don't check stuff for now
    if (!getLobby().getServerItemState().hasDynamiteOwnedBy(clientId)) {

      float maxDmg = Player.getDigInterval() * digDamage;

      if (frozen) {
        maxDmg *= getFreezeFactor();
      }

      // Check if damage is too high (with tolerance)
      if (damage > maxDmg * 1.2f) {
        logger.warn("Too much dig damage for one packet. 1 violation.");
        logger.debug(
            "allowed: " + maxDmg + " factor: " + getFreezeFactor() + " current: " + damage);
        addDamageViolations(1);
        return false;
      }
      // Check if packets are sent too fast
      if (System.currentTimeMillis() - lastDig < 900 * Player.getDigInterval()) {
        // We dont give violations for digging too fast since this can happen out of the player's
        // control. However, we do ignore any dig attempts that violate the dig interval.
        logger.warn("Player digging too fast.");
        return false;
      }
      // Check if player is too far away from block. This is fairly generous since we only update
      // positions once per second
      Vector3f blockPos = getLobby().getMap().gridToWorld(new Vector2i(posX, posY));
      if (new Vector2f(blockPos.x, blockPos.y).distance(getPos2d()) > Player.getRunSpeed() * 2) {
        logger.warn(getUsername() + ": Trying to dig a block too far away. 1 violation.");
        addDamageViolations(1);

        // Another 4 violations if the block is way too far away
        if (new Vector2f(blockPos.x, blockPos.y).distance(getPos2d()) > Player.getRunSpeed() * 4) {
          logger.warn(getUsername() + ": Trying to dig a block accross the map... 4 violations.");
          addDamageViolations(4);
        }
        return false;
      }
    }
    lastDig = System.currentTimeMillis();
    return true;
  }

  public void setCurrentVelocity2d(Vector2f currentVelocity2d) {
    this.currentVelocity2d = currentVelocity2d;
  }

  public void setGoalVelocity2d(Vector2f goalVelocity2d) {
    this.goalVelocity2d = goalVelocity2d;
  }

  public void setRotY(float rotY) {
    this.rotY = rotY;
  }

  public int getMovementViolations() {
    return movementViolations;
  }

  /**
   * Add a number of violations and enforce actions when too many violations are accumulated.
   *
   * @param violations number of violations to add
   */
  private void addMovementViolations(int violations) {
    movementViolations += violations;
    if (movementViolations >= 5 && !kicked) {
      kick();
    }
  }

  /**
   * Add a number of violations and enforce actions when too many violations are accumulated.
   *
   * @param violations number of violations to add
   */
  private void addDamageViolations(int violations) {
    damageViolations += violations;
    if (damageViolations >= 5 && !kicked) {
      kick();
    }
  }

  private void kick() {
    // Set kicked flag. Will be handled by the lobby game loop.
    kicked = true;
    // Inform GameLobby
    String timestamp = Util.getFormattedTimestamp();
    new PacketChatMessageToClient(
            clientId,
            "[SERVER-" + timestamp + "] removing " + getUsername() + " due to inconsistencies.")
        .sendToLobby(getCurLobbyId());
  }

  /**
   * Set a player as defeated and inform the clients. Sends a PacketDefeated with the client ID and
   * a Message to all players in the lobby.
   *
   * @param defeated can only be true for now. No way to revive
   */
  public void setDefeated(boolean defeated) {
    if (defeated) {
      this.defeated = defeated;
      if (getCurLobbyId() > 0) {
        new PacketDefeated(getClientId()).sendToLobby(getCurLobbyId());
        new PacketChatMessageToClient(getUsername() + " has been defeated.")
            .sendToLobby(getCurLobbyId());
      }
    }
  }

  public long getTimeStampOfGain() {
    return timeStampOfGain;
  }

  public ClientThread getClientThread() {
    return ServerLogic.getThreadByClientId(getClientId());
  }

  public void freeze() {
    frozen = true;
    frozenAt = System.currentTimeMillis();
  }

  private float getFreezeFactor() {
    // Allow up to 500ms delay
    float freezeDuration = (System.currentTimeMillis() - frozenAt) / 1000f;
    if (freezeDuration < .5f) {
      return 1;
    } else if (freezeDuration > Star.getFreezeTime() - 2f) {
      frozen = false;
      return 1;
    }

    float freezeFactor;
    if (freezeDuration > 1.5f) {
      freezeFactor = Math.max((freezeDuration - 2) / Star.getFreezeTime() * .4f, 0);
    } else {
      freezeFactor = 0;
    }
    return freezeFactor;
  }

  public boolean isDefeated() {
    return this.defeated;
  }

  public boolean isReady() {
    return ready;
  }

  public void setReady(boolean ready) {
    this.ready = ready;
  }

  public boolean isKicked() {
    return kicked;
  }
}
