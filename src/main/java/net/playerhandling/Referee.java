package net.playerhandling;

import java.util.concurrent.ConcurrentHashMap;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.life.PacketLifeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Form a consensus decision if a player's life total changed based on votes by all the clients. */
public class Referee {

  public static final Logger logger = LoggerFactory.getLogger(Referee.class);

  private int effectedId;
  private ServerPlayer effectedPlayer;
  private ConcurrentHashMap<Integer, Integer> allPerspectives;
  private Lobby lobby;
  private long createdAt;
  private volatile boolean decided;

  /**
   * Create a new Referee to make a decision for one specific life changing event for one player.
   * The event will last until all clients have voted or maximum 500ms. If the player reports his
   * own damage, the process is cut short since there is no potential for abuse.
   *
   * @param effectedId clientId for the effected player (the player with the life change)
   * @param lobby the lobby where the referee is spawned
   */
  public Referee(int effectedId, Lobby lobby) {
    this.decided = false;
    this.effectedId = effectedId;
    this.effectedPlayer = ServerLogic.getPlayerList().getPlayer(effectedId);
    if (effectedPlayer == null) {
      logger.warn("Invalid player reported. This should never happen.");
      createdAt = System.currentTimeMillis() + 1000; // Invalid player, set lobby to expired
      return;
    }
    this.allPerspectives = new ConcurrentHashMap<>();
    this.lobby = lobby;
    this.createdAt = System.currentTimeMillis();

    // Schedule to make a decision in 500 ms
    new java.util.Timer()
        .schedule(
            new java.util.TimerTask() {
              @Override
              public void run() {
                logger.info("Voting time concerning " + effectedPlayer.getUsername() + " is over.");
                finalDecision();
              }
            },
            500);
  }

  /** Decide based on the current information how to change the effected player's life total. */
  public void finalDecision() {
    // Ignore if decision invalid or already resolved
    if (decided || allPerspectives == null) {
      return;
    }

    // Ignore if less than half of the players have an opinion
    if (allPerspectives.size() < lobby.getPlayerAmount() / 2f) {
      return;
    }

    // The values of the votes have been validated in the LifeStatusPacket
    // Count up votes for each life total and determine the consensus
    int[] votes = new int[3];
    for (int val : allPerspectives.values()) {
      votes[val]++;
    }
    int maxVotes = -1;
    int winningLifetotal = -1;
    for (int i = 0; i <= 2; i++) {
      if (votes[i] >= maxVotes) {
        winningLifetotal = i;
        maxVotes = votes[i];
      }
    }

    // Break ties in favour of the player
    if (allPerspectives.containsKey(effectedId)
        && votes[allPerspectives.get(effectedId)] == votes[winningLifetotal]) {
      winningLifetotal = allPerspectives.get(effectedId);
    }

    resolve(winningLifetotal);
  }

  /**
   * Resolve a decision by changing the variables server side, updating the clients via packet and
   * cleaning up the referee instance.
   *
   * @param newLives new value for lives of the player
   */
  private void resolve(int newLives) {
    if (decided) {
      return;
    }
    decided = true;

    if (newLives == effectedPlayer.getCurrentLives()) {
      // No change in life total, No action required.
      logger.info(
          "I have decided that no life change occurred for " + effectedPlayer.getUsername());
    } else {
      // Lifetotal changed
      logger.info(
          "I have decided to set the lives for player "
              + effectedPlayer.getUsername()
              + " from "
              + effectedPlayer.getCurrentLives()
              + " to "
              + newLives
              + ".");
      PacketLifeStatus finalDecision = new PacketLifeStatus(newLives, effectedId);
      finalDecision.sendToLobby(lobby.getLobbyId());
      effectedPlayer.setCurrentLives(newLives);
    }

    // Remove Referee instance
    lobby.clearRef(effectedId);
  }

  /**
   * Add the perspective of a single client to the list.
   *
   * <p>If the effected player adds his own perspective and reports a life loss, we immediately
   * resolve the event.
   *
   * <p>After adding a new perspective we check if all the votes are in and trigger the decision if
   * so.
   *
   * @param clientId clientId of the client reporting the event
   * @param currentLives the new life total that the reporter would like to change to
   */
  public void addPerspective(int clientId, int currentLives) {
    if (decided) {
      return;
    }
    allPerspectives.put(clientId, currentLives);
    logger.info(
        "Client "
            + ServerLogic.getPlayerList().getUsername(clientId)
            + " would like to set the lives of "
            + effectedPlayer.getUsername()
            + " to "
            + currentLives);

    if (clientId == effectedId && effectedPlayer.getCurrentLives() > currentLives) {
      // Player reporting his own damage or no change -> shortcut
      logger.info("Player " + effectedPlayer.getUsername() + " reporting his own damage. Closing.");
      resolve(currentLives);
    }

    if (allPerspectives.size() == lobby.getPlayerAmount()) {
      finalDecision();
    }
  }

  /**
   * Checks if the referee class is still valid to add perspectives.
   *
   * @return True if the referee is still taking perspectives
   */
  public boolean isOpen() {
    return System.currentTimeMillis() - createdAt < 500;
  }
}
