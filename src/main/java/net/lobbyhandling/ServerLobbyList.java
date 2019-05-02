package net.lobbyhandling;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class acts as a list. It is used by the Server to handle the current lobbies.
 *
 * @author Sebastian Schlachter
 */
public class ServerLobbyList {
  private ConcurrentHashMap<Integer, Lobby> lobbies;

  /**
   * Constructs a {@link ServerLobbyList}.
   *
   * <p>Lobby list is initially empty.
   */
  public ServerLobbyList() {
    lobbies = new ConcurrentHashMap<>();
  }

  /**
   * Adds a lobby to the "list" of lobbies.
   *
   * @param lobby The lobby to be added to the HashMap.
   * @return statement to let the calling instance know, whether the adding-attempt was successful
   *     or not. Checks if the given lobby is already in the list. Checks if desired lobbyname is
   *     not taken yet. In the case on an error a suitable errormessage gets returned
   */
  public String addLobby(Lobby lobby) {
    if (lobbies.containsKey(lobby.getLobbyId())) {
      return "Lobby already created.";
    }
    for (Lobby l : lobbies.values()) {
      if (lobby.getLobbyName().equals(l.getLobbyName())) {
        return "Lobbyname already taken.";
      }
    }
    lobbies.put(lobby.getLobbyId(), lobby);
    return "OK";
  }

  /**
   * Removes a lobby from the "list" of lobbies.
   *
   * @param lobbyId The lobbyId of the lobby to be removed
   * @return 1 or -1 depending on whether the lobby was successfully removed or not.
   */
  public int removeLobby(int lobbyId) {
    if (lobbies.containsKey(lobbyId)) {
      lobbies.remove(lobbyId);
      return 1;
    } else {
      return -1;
    }
  }

  /**
   * Searches for a lobbies name in the list by using the lobbyId.
   *
   * @param lobbyId the lobbyId of the desired Lobby
   * @return either the correct name or null
   */
  public String getName(int lobbyId) {
    return lobbies.get(lobbyId).getLobbyName();
  }

  /**
   * Searches for a lobby in the list by using the lobbyId.
   *
   * @param lobbyId the lobbyId of the desired Lobby.
   * @return either the lobby or null if not found.
   */
  public Lobby getLobby(int lobbyId) {
    return lobbies.get(lobbyId);
  }

  /**
   * Searches for a lobbies lobbyId in the list by using the lobbyname.
   *
   * @param lobbyName the lobbyName of the desired Lobby.
   * @return either the lobbyId or null if not found.
   */
  public int getLobbyId(String lobbyName) {
    for (Lobby l : lobbies.values()) {
      if (lobbyName.equals(l.getLobbyName())) {
        return l.getLobbyId();
      }
    }
    return -1;
  }

  /**
   * Creates a listing of at max 10 lobbies.
   *
   * @return A String that contains a List of max 10 lobbies (that are not full). Each line contains
   *     the Lobbies: Name,LobbyId, and the Amount of Players in the Lobby. If no such lobbies are
   *     available the String contains the information about that.
   */
  public String getTopTen() {
    StringBuilder s = new StringBuilder();
    int counter = 0;
    if (lobbies.size() > 0) {
      for (Lobby l : lobbies.values()) {
        if (counter == 10) {
          break;
        }
        if (l.getPlayerAmount() == Lobby.getMaxPlayers() || !l.getStatus().equals("open")) {
          continue;
        } else {
          s.append(l.toString());
          counter++;
        }
      }
      s.insert(0, counter);
      if (s.toString().equals("0")) {
        s = new StringBuilder("No open Lobbies");
      }
    } else {
      s = new StringBuilder("No Lobbies online");
    }
    return s.toString();
  }

  /**
   * Creates a list of all lobbies currently in a game.
   *
   * @return A String that contains a list of all lobbies in a game. Each Line contains Lobbies:
   *     Name,LobbyId, and the Amount of Players in the Lobby. If no lobbies currently in a game are
   *     available, the String contains the information about that.
   */
  public String getLobbiesInGame() {
    StringBuilder s = new StringBuilder();
    if (lobbies.size() > 0) {
      for (Lobby l : lobbies.values()) {
        if (l.isInGame()) {
          s.append(l.toString()).append("║");
        }
      }
    } else {
      s = new StringBuilder("No Lobbies online");
    }
    if (s.toString().equals("")) {
      return "No Games active";
    }
    return s.toString();
  }

  public ConcurrentHashMap<Integer, Lobby> getLobbies() {
    return lobbies;
  }
}
