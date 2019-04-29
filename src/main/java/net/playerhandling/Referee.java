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

  public Referee(int playerId, Lobby lobby) {
    this.playerId = playerId; // wem dieser referee gehört
    allPerspectives = new ConcurrentHashMap<>();
    this.lobby = lobby;

    // nach 500 ms führt es finalDecision aus
    new java.util.Timer()
        .schedule(
            new java.util.TimerTask() {
              @Override
              public void run() {
                finalDecision();
                lobby.getReferees().put(playerId, null); // sich löschen wenn abgelaufen
              }
            },
            500);
  }

  public void finalDecision() {
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
    if (allPerspectives.size() < lobby.getPlayerAmount() / 2) {
      return;
    }
    PacketLifeStatus finalDecision = new PacketLifeStatus(maxInd + "server" + playerId);
    finalDecision.sendToLobby(lobby.getLobbyId());
    ServerLogic.getPlayerList().getPlayer(playerId).setCurrentLives(maxInd);
  }

  public void add(int clientId, int currentLives) {
    allPerspectives.put(clientId, currentLives);
  }
}
