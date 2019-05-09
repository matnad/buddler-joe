package net.lobbyhandling;

import game.History;
import game.map.ServerMap;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.ServerLogic;
import net.highscore.ServerHighscoreSerialiser;
import net.packets.gamestatus.PacketGameEnd;
import net.packets.gamestatus.PacketStartRound;
import net.packets.lobby.PacketCurLobbyInfo;
import net.packets.lobby.PacketJoinLobbyStatus;
import net.packets.lobby.PacketLobbyOverview;
import net.packets.map.PacketBroadcastMap;
import net.playerhandling.Referee;
import net.playerhandling.ServerPlayer;
import net.playerhandling.ServerPlayerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main lobby class to save the vital information which the server has to access at all times.
 *
 * @author Sebastian Schlachter
 */
public class Lobby implements Runnable {

  public static final Logger logger = LoggerFactory.getLogger(Lobby.class);
  private static final int maxPlayers = 7;
  private static int lobbyCounter = 1;
  private int lobbyId;
  private boolean inGame;
  private String lobbyName;
  private CopyOnWriteArrayList<ServerPlayer> lobbyPlayers;
  private CopyOnWriteArrayList<ServerPlayer> aliveLobbyPlayers;
  private CopyOnWriteArrayList<ServerPlayer> archiveLobbyPlayers;
  private ServerMap map;
  private int createrPlayerId;
  private String mapSize;
  private String status;
  private long createdAt;
  private long startedAt;
  private long lastEntry;
  private ServerItemState serverItemState;
  private boolean checked;
  private ConcurrentHashMap<Integer, Referee> refereesForClients;
  private Thread gameLoop;

  /**
   * Constructor of the lobby-class uses by the Server.
   *
   * @param lobbyName The name of the new lobby. {@link Lobby#lobbyId} gets set to equal the {@link
   *     Lobby#lobbyCounter}. {@link Lobby#lobbyCounter} gets raised by one after every lobby
   *     construction.
   * @param createrPlayerId id of the player who is creating the lobby
   * @param mapSize a String that should equal "s", "m" or "l" that describes the mapsize.
   */
  public Lobby(String lobbyName, int createrPlayerId, String mapSize) {
    this.lobbyName = lobbyName;
    this.createrPlayerId = createrPlayerId;
    this.mapSize = mapSize;
    this.status = "open";
    this.lastEntry = System.currentTimeMillis();
    this.inGame = false;
    this.lobbyPlayers = new CopyOnWriteArrayList<>();
    this.aliveLobbyPlayers = new CopyOnWriteArrayList<>();
    this.archiveLobbyPlayers = new CopyOnWriteArrayList<>();
    this.lobbyId = lobbyCounter;
    this.serverItemState = new ServerItemState();
    this.refereesForClients = new ConcurrentHashMap<>();
    lobbyCounter++;
    checked = false;
    map = new ServerMap(mapSize, System.currentTimeMillis());
    createdAt = System.currentTimeMillis();
    gameLoop = new Thread(this);
    gameLoop.start();
  }

  @Override
  public void run() {
    // Loop once per second
    while (!status.equals("finished")) {
      long startOfLoop = System.currentTimeMillis();

      if (status.equals("running")) {
        // Do stuff
        for (ServerPlayer player : aliveLobbyPlayers) {
          if (player.isKicked()) {
            logger.info("Kicking " + player.getUsername() + ".");
            ServerLogic.removePlayer(player.getClientId());
          }
          if (player.isDefeated()) {
            logger.debug("removing " + player.getUsername() + " from alive players.");
            aliveLobbyPlayers.remove(player);
          }
        }

        try {
          if (aliveLobbyPlayers.size() == 0 && !checked) {
            logger.debug("Alive players == 0.");
            Thread.sleep(2500);
            if (getCurrentWinner() != null) {
              gameOver(getCurrentWinner());
            }
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } else if (status.equals("open")) {
        if (getPlayerAmount() == 0 && System.currentTimeMillis() - this.lastEntry > 120000) {
          // TESTZWECKE 20sek, ----> 5min, 300'000 ms
          ServerLogic.getLobbyList().removeLobby(this.lobbyId);
          this.status = "finished";
          History.openRemove(lobbyId);
          logger.debug("deleting lobby " + lobbyName);
        }
      }
      try {
        if (System.currentTimeMillis() + startOfLoop < 1000) {
          Thread.sleep(1000 - System.currentTimeMillis() + startOfLoop);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
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
   * checks the player who collected the largest amount of gold, if all players are defeated. The
   * time is also taken into account.
   *
   * @return the winner
   */
  public ServerPlayer getCurrentWinner() {
    checked = !checked;
    int highestGoldValue = -1;

    for (ServerPlayer player : archiveLobbyPlayers) {
      if (highestGoldValue < player.getCurrentGold()) {
        highestGoldValue = player.getCurrentGold();
      }
    }

    if (highestGoldValue != 0) {
      ConcurrentHashMap<ServerPlayer, Long> winningPlayers = new ConcurrentHashMap<>();
      for (ServerPlayer player : archiveLobbyPlayers) {
        if (highestGoldValue == player.getCurrentGold()) {
          winningPlayers.put(player, player.getTimeStampOfGain());
        }
      }

      long min = 0;
      try {
        min = Collections.min(winningPlayers.values());
      } catch (NoSuchElementException e) {
        logger.warn("No proper winner determination");
      }

      for (ServerPlayer player : winningPlayers.keySet()) {
        if (min == winningPlayers.get(player)) {
          return player;
        }
      }
    }

    Random rnd = new Random(System.currentTimeMillis());
    int r = rnd.nextInt(archiveLobbyPlayers.size());
    return archiveLobbyPlayers.get(r);
  }

  /**
   * Adds a player to this Lobby.
   *
   * @param player The player to be added to the HashMap of this Lobby.
   * @return statement to let the calling instance know, whether the action was successful or not.
   *     ("OK" or "Already joined this lobby.")
   */
  public String addPlayer(ServerPlayer player) {
    if (!status.equals("open")) {
      return "Lobby not open.";
    }
    if (lobbyPlayers.contains(player)) {
      return "Already joined this lobby.";
    }
    lobbyPlayers.add(player);
    this.lastEntry = System.currentTimeMillis();
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
    try {
      if (ServerLogic.getPlayerList().isClientIdInList(clientId)) {
        ServerPlayer player = ServerLogic.getPlayerList().getPlayer(clientId);
        ServerLogic.getPlayerList().resetPlayer(player);
        player.setReady(false);
        lobbyPlayers.remove(player);
        aliveLobbyPlayers.remove(player);
        if (status.equals("open") && allPlayersReady() && !isEmpty()) {
          startRound();
        }
        return "OK";
      } else {
        return "Not in a Lobby";
      }
    } catch (NullPointerException e) {
      logger.warn("nullpointer in remove player. " + e.getMessage());
      for (int i = 0; i < lobbyPlayers.size(); i++) {
        if (lobbyPlayers.get(i).getClientId() == clientId) {
          lobbyPlayers.get(i).setReady(false);
          lobbyPlayers.remove(lobbyPlayers.get(i));
        }
      }
      if (status.equals("open") && allPlayersReady() && !isEmpty()) {
        startRound();
      }
      return "Not connected to the server.";
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

  public CopyOnWriteArrayList<ServerPlayer> getLobbyPlayers() {
    return lobbyPlayers;
  }

  /**
   * Creates a listing of the Players in this Lobby.
   *
   * @return A String with the usernames of all Players in this Lobby seperated by "║".
   */
  public String getPlayerNames() {
    StringBuilder s = new StringBuilder();
    for (ServerPlayer player : lobbyPlayers) {
      s.append(player.getUsername()).append("║");
    }
    return s.toString();
  }

  /**
   * Creates a listing of the Players with their IDs in this Lobby.
   *
   * @return A String with the usernames of all IDs and Players in this Lobby separated by "║".
   */
  public String getPlayerNamesIdsReadies() {
    StringBuilder s = new StringBuilder();
    for (ServerPlayer player : lobbyPlayers) {
      s.append(player.getClientId())
          .append("║")
          .append(player.getUsername())
          .append("║")
          .append(player.isReady())
          .append("║");
    }
    return s.toString();
  }

  /**
   * Gets called if the Round should end. Updates Lobbystatus, updates Highscore, resets
   * ServerPlayer states, informs all clients about the end of the round. Archives the round in the
   * History.
   *
   * @param player the winning player
   */
  public void gameOver(ServerPlayer player) {
    // setStatus("open");
    setStatus("finished");
    History.runningRemove(lobbyId);
    // History.openAdd(lobbyId, lobbyName);
    String userName = player.getUsername();
    History.archive("Lobbyname: " + lobbyName + "       Winner: " + userName);
    long time = System.currentTimeMillis() - this.startedAt;

    // Update highscore
    if (!player.isDefeated()) {
      ServerLogic.getServerHighscore().addPlayer(time, userName);
      ServerHighscoreSerialiser.serialiseServerHighscore(ServerLogic.getServerHighscore());
    }
    // TODO send EndGamepacket here i created a skeleton already.
    // Inform all clients
    new PacketGameEnd(userName, time).sendToLobby(lobbyId);
    // Reset Server Players
    CopyOnWriteArrayList<ServerPlayer> oldLobbyPlayers = new CopyOnWriteArrayList<>();
    for (ServerPlayer lobbyPlayer : lobbyPlayers) {
      oldLobbyPlayers.add(lobbyPlayer);
      removePlayer(lobbyPlayer.getClientId());
    }
    transfer(oldLobbyPlayers);
  }

  /** Creates a new Lobby and transfers all players of this lobby to the new one. */
  private void transfer(CopyOnWriteArrayList<ServerPlayer> oldLobbyPlayers) {
    try {
      Lobby freshLobby = new Lobby(getFreshName(), createrPlayerId, mapSize);
      ServerLogic.getLobbyList().addLobby(freshLobby);
      for (ServerPlayer oldLobbyPlayer : oldLobbyPlayers) {
        // add old players to new Lobby and inform them.
        ServerPlayer serverPlayer =
            ServerLogic.getPlayerList().getPlayer(oldLobbyPlayer.getClientId());
        String transferStatus = freshLobby.addPlayer(serverPlayer);
        if (transferStatus.equals("OK")) {
          serverPlayer.setCurLobbyId(freshLobby.getLobbyId());
        }
        new PacketJoinLobbyStatus(serverPlayer.getClientId(), transferStatus)
            .sendToClient(serverPlayer.getClientId());
      }
      for (ServerPlayer lobbyPlayer : freshLobby.getLobbyPlayers()) {
        PacketCurLobbyInfo pcli =
            new PacketCurLobbyInfo(lobbyPlayer.getClientId(), freshLobby.getLobbyId());
        pcli.sendToClient(lobbyPlayer.getClientId());
        new PacketBroadcastMap(freshLobby.getMap()).sendToClient(lobbyPlayer.getClientId());
      }
      String info = "OK║" + ServerLogic.getLobbyList().getTopTen();
      PacketLobbyOverview packetLobbyOverview =
          new PacketLobbyOverview(1, info); // one is not important
      packetLobbyOverview.sendToClientsNotInALobby();
    } catch (Exception e) {
      logger.error("Error while transferring players to new lobby.");
    }
  }

  /**
   * Creates a new lobbyname consisting of the old lobbyname and a increasing number.
   *
   * @return a valid lobbyname.
   */
  public String getFreshName() {
    StringBuilder oldName = new StringBuilder(lobbyName);
    StringBuilder number = new StringBuilder();
    int digits = 0;
    while (oldName.length() > 0 && Character.isDigit(oldName.charAt(oldName.length() - 1))) {
      digits++;
      number.insert(0, oldName.charAt(oldName.length() - 1));
      oldName.deleteCharAt(oldName.length() - 1);
    }
    long numLong;
    if (number.length() > 0) {
      numLong = Long.parseLong(number.toString());
    } else {
      numLong = 0;
    }
    numLong++;
    number = new StringBuilder(Long.toString(numLong));

    if (number.length() > 16) {
      logger.info("getFreshName(), had to use generic name.");
      number = new StringBuilder(Long.toString(System.currentTimeMillis()));
      while (number.length() > 16) {
        number.deleteCharAt(number.length() - 1);
      }
    } // number is shorter or equal to 16 after this if

    while (oldName.length() > 0 && number.length() + oldName.length() > 16) {
      oldName.deleteCharAt(oldName.length() - 1);
    }

    while (number.length() + oldName.length() < 4) {
      number.append(0);
    }

    oldName.append(number.toString());
    return oldName.toString();
  }

  @Override
  public String toString() {
    return "║" + lobbyName + "║" + getPlayerAmount() + "║" + mapSize;
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
    String old;
    old = this.status;
    if (!status.equals("open") && !status.equals("running") && !status.equals("finished")) {
      logger.error("tried to set unknown lobbystatus.");
      return;
    }
    if (status.equals("running")) {
      for (ServerPlayer player : lobbyPlayers) {
        aliveLobbyPlayers.add(player);
      }
    }
    this.status = status;
    if (!old.equals(this.status)) {
      try {
        String info = "OK║" + ServerLogic.getLobbyList().getTopTen();
        if (getPlayerAmount() != 0) {
          new PacketLobbyOverview(lobbyPlayers.get(0).getClientId(), info)
              .sendToClientsNotInALobby();
        } else {
          new PacketLobbyOverview(1, info).sendToClientsNotInALobby();
          // TODO: check if this works with the "1", do we really need the clientId in the
          // constructor?
        }
      } catch (NullPointerException e) {
        logger.error("Not connected to a server.");
        return;
      }

      if (status.equals("running")) {
        inGame = true;
      } else {
        inGame = false;
      }
    }
  }

  public long getStartedAt() {
    return startedAt;
  }

  public ServerItemState getServerItemState() {
    return serverItemState;
  }

  /**
   * Checks if all lobbymembers are ready.
   *
   * @return true if all players in the lobby are ready, false otherwise.
   */
  public boolean allPlayersReady() {
    for (ServerPlayer lobbyPlayer : lobbyPlayers) {
      if (!lobbyPlayer.isReady()) {
        return false;
      }
    }
    return true;
  }

  /** Starts the Round for this Lobby. */
  public void startRound() {
    for (ServerPlayer player : lobbyPlayers) {
      archiveLobbyPlayers.add(player);
    }
    setStatus("running");
    History.openRemove(lobbyId);
    History.runningAdd(lobbyId, lobbyName);
    this.startedAt = System.currentTimeMillis();
    new PacketStartRound().sendToLobby(lobbyId);
  }

  /**
   * Add the perspective for a life change event from one player.
   *
   * <p>Whenever a player observers himself or another player getting a life change, this is
   * reported to the server and the server adds this perspective to a Referee object. The Referee
   * class will then decide how to proceed.
   *
   * <p>This method will check if there is a running referee event to add the perspective to and if
   * not, will create a new instance.
   *
   * @param voter the clientId of the player who is making the observation
   * @param effected the clientId of the effected player (the player with the life change)
   * @param currentLives the new value of lives for the effected player
   */
  public void addPerspective(int voter, int effected, int currentLives) {
    Referee ref = refereesForClients.get(effected);
    if (ref == null || !ref.isOpen()) {
      ref = new Referee(effected, this);
      refereesForClients.put(effected, ref);
    }
    ref.addPerspective(voter, currentLives);
  }

  public void clearRef(int playerId) {
    refereesForClients.remove(playerId);
  }
}
