package net.playerhandling;

import java.util.concurrent.ConcurrentHashMap;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.life.PacketLifeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Referee {

  public static final Logger logger = LoggerFactory.getLogger(Referee.class);

  private int effectedId;
  private ServerPlayer effectedPlayer;
  private ConcurrentHashMap<Integer, Integer> allPerspectives;
  private Lobby lobby;
  private long createdAt;
  private volatile boolean decided;

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
    new java.util.Timer()
        .schedule(
            new java.util.TimerTask() {
              @Override
              public void run() {
                  finalDecision();
              }
            },
            500);
  }

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

  private void resolve(int newLives) {
    if (decided) {
      return;
    }
    decided = true;
    logger.info(
        "I have decided to set the lives for player "
            + effectedPlayer.getUsername()
            + " from "
            + effectedPlayer.getCurrentLives()
            + " to "
            + newLives
            + ".");
    PacketLifeStatus finalDecision = new PacketLifeStatus(newLives + "server" + effectedId);
    finalDecision.sendToLobby(lobby.getLobbyId());
    effectedPlayer.setCurrentLives(newLives);
    lobby.clearRef(effectedId);
  }

  public void add(int clientId, int currentLives) {
    if (decided) {
      return;
    }
    allPerspectives.put(clientId, currentLives);
    logger.info(
        "Client #"
            + clientId
            + " would like to set the lives of "
            + effectedPlayer.getUsername()
            + " to "
            + currentLives);

    if (clientId == effectedId && effectedPlayer.getCurrentLives() > currentLives) {
      // Player reporting his own damage or no change
      logger.info("Player " + effectedPlayer.getUsername() + " reporting his own damage. Closing.");
      resolve(currentLives);
    }

    if (allPerspectives.size() == lobby.getPlayerAmount()) {
      finalDecision();
    }
  }

  public boolean isOpen() {
    return System.currentTimeMillis() - createdAt < 500;
  }
}
