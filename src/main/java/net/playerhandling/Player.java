package net.playerhandling;

import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.chat.PacketChatMessageToClient;
import net.packets.playerprop.PacketDefeated;
import org.joml.Vector2f;

public class Player {

  private String username;
  private int clientId;
  private int curLobbyId;

  private int currentGold;
  private int currentLives;

  private boolean defeated;

  private Vector2f pos2d;
  private float rotY;

  /**
   * Constructor of the player class to create a new player Creates an instance of the main Player
   * class to save the player information on the server side in the playerList. Contains vital
   * information as well as setters and getters to access the information from the server side.
   *
   * @param username Unique username for the player to be set and which is to be displayed in the
   *     game
   * @param clientId to identify the player, unique to every player and assigned by the first login
   *     by the ServerLogic class
   */
  public Player(String username, int clientId) {
    this.username = username;
    this.clientId = clientId;
    curLobbyId = 0;
    currentLives = 2;
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
    return "Player{"
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
    if (currentGold >= 500) { // TODO: set to 3000
      Lobby lobby = ServerLogic.getLobbyList().getLobby(curLobbyId);
      System.out.println("Game Over");
      lobby.gameOver(clientId);
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

  public void setPos2d(Vector2f pos2d) {
    this.pos2d = pos2d;
  }

  public void setRotY(float rotY) {
    this.rotY = rotY;
  }

  /**
   * Set a player as defeated and inform the clients.
   * Sends a PacketDefeated with the client ID and a Message to all players in the lobby.
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
}
