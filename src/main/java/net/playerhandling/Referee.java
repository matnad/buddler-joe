package net.playerhandling;

import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.life.PacketLifeStatus;

import java.util.concurrent.ConcurrentHashMap;

public class Referee {

  private int playerId;
  private ConcurrentHashMap<Integer, Integer> allPerspectives;
  private Lobby lobby;
  private long timestamp;
  private boolean decided;

  public Referee(int playerId, Lobby lobby) {
    this.decided = false;
    this.playerId = playerId;
    allPerspectives = new ConcurrentHashMap<>();
    this.lobby = lobby;
    this.timestamp = System.currentTimeMillis();
    new java.util.Timer()
        .schedule(
            new java.util.TimerTask() {
              @Override
              public void run() {
                if (!decided) {
                  finalDecision();
                }
              }
            },
            500);
  }

  public void finalDecision() {
    try {
      decided = true;
      if (allPerspectives == null) {
        return;
      }
      int[] votes = new int[3];
      for (int val : allPerspectives.values()) {
        votes[val]++;
      }
      int max = -1;
      int maxInd = -1;
      for (int i = 0; i <= 2; i++) {
        if (votes[i] >= max) {
          maxInd = i;
          max = votes[i];
        }
      }
      if (votes[allPerspectives.get(playerId)] == votes[maxInd]) {
        maxInd = allPerspectives.get(playerId);
      }
      if (allPerspectives.size() < lobby.getPlayerAmount() / 2f) {
        return;
      }
      PacketLifeStatus finalDecision = new PacketLifeStatus(maxInd + "server" + playerId);
      finalDecision.sendToLobby(lobby.getLobbyId());

      lobby.clear(playerId);

      ServerLogic.getPlayerList().getPlayer(playerId).setCurrentLives(maxInd);
    } catch (NullPointerException e) {
      System.out.println("there's a nullpointer in Referee");
    }
  }

  public void add(int clientId, int currentLives) {
    allPerspectives.put(clientId, currentLives);
    System.out.println("meinung von client " + clientId + "zu event von client " + playerId);
    if (allPerspectives.size() == lobby.getPlayerAmount() && !decided) {
      finalDecision();
    }
  }

  public long getTimestamp() {
    return timestamp;
  }

  public ConcurrentHashMap getAllPerspectives() {
    return allPerspectives;
  }
}
