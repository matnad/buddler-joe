package net.playerhandling;

import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.life.PacketLifeStatus;

import java.util.concurrent.ConcurrentHashMap;

public class Referee {

  int playerId;
  // private long timestamp;
  private ConcurrentHashMap<Integer, Integer> allPerspectives;
  private Lobby lobby;
  private long timestamp;
  private boolean decided;

  public Referee(int playerId, Lobby lobby) {
    // System.out.println("here");
    this.decided = false;
    this.playerId = playerId; // wem dieser referee gehört
    allPerspectives = new ConcurrentHashMap<>();
    this.lobby = lobby;
    this.timestamp = System.currentTimeMillis();
    // nach 500 ms führt es finalDecision aus
    new java.util.Timer()
        .schedule(
            new java.util.TimerTask() {
              @Override
              public void run() {
                if (!decided) {
                  // System.out.println("here-aus constructor");
                  finalDecision();
                  // lobby.getReferees().put(playerId, null); // sich löschen wenn abgelaufen
                  // lobby.clear(playerId);
                }
              }
            },
            500);
  }

  public void finalDecision() {
    try {
      // System.out.println("here");
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
      // System.out.println("here");
      ServerLogic.getPlayerList().getPlayer(playerId).setCurrentLives(maxInd);
    } catch (NullPointerException e) {
      System.out.println("theres a nullpointer in referee");
    }
  }

  public void add(int clientId, int currentLives) {
    allPerspectives.put(clientId, currentLives);
    System.out.println("meinung von client " + clientId + "zu event von client " + playerId);
    if (allPerspectives.size() == lobby.getPlayerAmount() && !decided) {
      // System.out.println("here-aus add()");
      finalDecision();
      // lobby.clear(playerId);
    }
  }

  public long getTimestamp() {
    return timestamp;
  }

  public boolean isInAllPerspectives(int clientId) {
    for (int id : allPerspectives.keySet()) {
      if (id == clientId) {
        return true;
      }
    }
    return false;
  }
}
