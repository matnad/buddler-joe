package net.lobbyhandling;

import java.util.HashMap;

public class ClientCurrentLobby {
  private HashMap<Integer, Lobby> playersInLobby;
  private boolean inGame;
  private String lobbyName;

  public ClientCurrentLobby() {}

  public HashMap<Integer, Lobby> getPlayersInLobby() {
    return playersInLobby;
  }

  public void setPlayersInLobby(HashMap<Integer, Lobby> playersInLobby) {
    this.playersInLobby = playersInLobby;
  }

  public boolean isInGame() {
    return inGame;
  }

  public void setInGame(boolean inGame) {
    this.inGame = inGame;
  }

  public String getLobbyName() {
    return lobbyName;
  }

  public void setLobbyName(String lobbyName) {
    this.lobbyName = lobbyName;
  }
}
