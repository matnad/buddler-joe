package net.lobbyhandling;

import game.History;
import game.map.ServerMap;
import java.util.ArrayList;
import net.ServerLogic;
import net.highscore.ServerHighscoreSerialiser;
import net.packets.gamestatus.PacketGameEnd;
import net.packets.lobby.PacketLobbyOverview;
import net.playerhandling.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main lobby class to save the vital information which the server has to access at all times.
 *
 * @author Sebastian Schlachter
 */
public class Lobby {

  public static final Logger logger = LoggerFactory.getLogger(Lobby.class);
  private static final int maxPlayers = 7;
  private static int lobbyCounter = 1;
  private int lobbyId;
  private boolean inGame;
  private String lobbyName;
  private ArrayList<Player> lobbyPlayers;
  private ServerMap map;
  private int createrPlayerId;
  private String mapSize;
  private String status;
  private long createdAt;
  private ServerItemState serverItemState;

  /**
   * Constructor of the lobby-class uses by the Server.
   *
   * @param lobbyName The name of the new lobby. {@link Lobby#lobbyId} gets set to equal the {@link
   *     Lobby#lobbyCounter}. {@link Lobby#lobbyCounter} gets raised by one after every lobby
   *     construction.
   * @param createrPlayerId id of the player who is creating the lobby
   */
  public Lobby(String lobbyName, int createrPlayerId, String mapSize) {
    this.lobbyName = lobbyName;
    this.createrPlayerId = createrPlayerId;
    this.mapSize  = mapSize;
    this.status = "open";
    this.inGame = false;
    this.lobbyPlayers = new ArrayList<>();
    this.lobbyId = lobbyCounter;
    this.serverItemState = new ServerItemState();
    lobbyCounter++;
    map = new ServerMap(33, 40, System.currentTimeMillis());
  }

  /**
   * Getter that return the max amount of players per Lobby.
   *
   * @return the maximum number of players for all lobbies
   */
  public static int getMaxPlayers() {
    return maxPlayers;
  }

  /**
   * Adds a player to this Lobby.
   *
   * @param player The player to be added to the HashMap of this Lobby.
   * @return statement to let the calling instance know, whether the action was successful or not.
   *     ("OK" or "Already joined this lobby.")
   */
  public String addPlayer(Player player) {
    if (!status.equals("open")) {
      return "Lobby not open.";
    }
    if (lobbyPlayers.contains(player)) {
      return "Already joined this lobby.";
    }
    lobbyPlayers.add(player);
    return "OK";
  }

  /**
   * Removes a player from this Lobby.
   *
   * @param clientId of the player to be removed.
   * @return String with "OK" or "Not in a Lobby" depending on if the removing was successful or
   *     not.
   */
  public String removePlayer(int clientId) {
    if (ServerLogic.getPlayerList().isClientIdInList(clientId)) {
      Player player = ServerLogic.getPlayerList().getPlayer(clientId);
      lobbyPlayers.remove(player);
      return "OK";
    } else {
      return "Not in a Lobby";
    }
  }

  public int getPlayerAmount() {
    return lobbyPlayers.size();
  }

  public int getLobbyId() {
    return lobbyId;
  }

  public String getLobbyName() {
    return lobbyName;
  }

  public ArrayList<Player> getLobbyPlayers() {
    return lobbyPlayers;
  }

  /**
   * Creates a listing of the Players in this Lobby.
   *
   * @return A String with the usernames of all Players in this Lobby seperated by "║".
   */
  public String getPlayerNames() {
    StringBuilder s = new StringBuilder();
    for (Player player : lobbyPlayers) {
      s.append(player.getUsername()).append("║");
    }
    return s.toString();
  }

  /**
   * Creates a listing of the Players with their IDs in this Lobby.
   *
   * @return A String with the usernames of all IDs and Players in this Lobby separated by "║".
   */
  public String getPlayerNamesAndIds() {
    StringBuilder s = new StringBuilder();
    for (Player player : lobbyPlayers) {
      s.append(player.getClientId()).append("║").append(player.getUsername()).append("║");
    }
    return s.toString();
  }

  /**
   * Gets called if the Round should end. Updates Lobbystatus, updates Highscore, resets Player
   * states, informs all clients about the end of the round. Archives the round in the History.
   *
   * @param clientId id of the winning player
   */
  public void gameOver(int clientId) {

    // setStatus("open");
    History.runningRemove(lobbyId);
    // History.openAdd(lobbyId, lobbyName);
    String userName = ServerLogic.getPlayerList().getUsername(clientId);
    History.archive("Lobbyname: " + lobbyName + "       Winner: " + userName);
    long time = System.currentTimeMillis() - getCreatedAt();

    // Update highscore
    ServerLogic.getServerHighscore().addPlayer(time, userName);
    ServerHighscoreSerialiser.serialiseServerHighscore(ServerLogic.getServerHighscore());

    // TODO send EndGamepacket here i created a skeleton already.
    // Inform all clients
    new PacketGameEnd(userName, time).sendToLobby(lobbyId);
    // create new Map and broadcast
    // map = new ServerMap(60, 40, System.currentTimeMillis());
    // new PacketBroadcastMap(map).sendToLobby(lobbyId);
    // for (Player player : lobbyPlayers) {
    // player.setCurrentGold(0);
    // }
  }

  @Override
  public String toString() {
    return "║" + lobbyName + "║" + getPlayerAmount();
  }

  /**
   * Method to check whether lobby is empty.
   *
   * @return true if the lobby has no players in it
   */
  public boolean isEmpty() {
    return lobbyPlayers.size() == 0;
  }

  /**
   * Getter to check whether a lobby is currently in a game or not.
   *
   * @return a boolean value which says whether the lobby is in a game or not.
   */
  public boolean isInGame() {
    return inGame;
  }

  /**
   * Setter to change the state of the Lobby to inGame.
   *
   * @param inGame The boolean value to which the Lobby should be changed to.
   */
  public void setInGame(boolean inGame) {
    this.inGame = inGame;
  }

  public ServerMap getMap() {
    return map;
  }

  /**
   * Getter that returns the PlayerId of the player that created this lobby.
   *
   * @return the client id of the player that created this lobby
   */
  public int getCreaterPlayerId() {
    return createrPlayerId;
  }

  /**
   * Getter that returns the status of the lobby as String.
   *
   * @return status of the lobby ["open", "running", "finished"]
   */
  public String getStatus() {
    return status;
  }

  /**
   * Setter for status, only "open", "running" and "finished" gets accepted.
   *
   * @param status the new status. Should be in ["open", "running", "finished"]
   */
  public void setStatus(String status) {
    String old = this.status;
    if (!status.equals("open") && !status.equals("running") && !status.equals("finished")) {
      logger.error("tried to set unknown lobbystatus.");
      return;
    }
    this.status = status;
    if (!old.equals(this.status)) {
      String info = "OK║" + ServerLogic.getLobbyList().getTopTen();
      if (getPlayerAmount() != 0) {
        new PacketLobbyOverview(lobbyPlayers.get(0).getClientId(), info).sendToClientsNotInALobby();
      } else {
        new PacketLobbyOverview(1, info).sendToClientsNotInALobby();
        // TODO: check if this works with the "1", do we really need the clientId in the
        // constructor?
      }
      if (status.equals("running")) {
        inGame = true;
        createdAt = System.currentTimeMillis();
      } else {
        inGame = false;
      }
    }
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public ServerItemState getServerItemState() {
    return serverItemState;
  }
}
