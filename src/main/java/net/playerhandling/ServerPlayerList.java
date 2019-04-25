package net.playerhandling;

import java.util.concurrent.ConcurrentHashMap;

public class ServerPlayerList {

  private ConcurrentHashMap<Integer, Player> players;

  public ServerPlayerList() {
    this.players = new ConcurrentHashMap<>();
  }

  /**
   * Method to add a player the the HashMap of all to the server connected players. Also checks
   * whether a username is already in use and then assigns a name with a number added at the end to
   * avoid the same username twice.
   *
   * @param player The player to be added to the HashMap
   * @return answer the the caller of the method to let them know whether the chosen username or a
   *     changed version of the username has been added to the playerList
   */
  public String addPlayer(Player player) {
    String answer;
    if (players.containsKey(player.getClientId())) {
      answer = "Already logged in.";
    } else if (isUsernameInList(player.getUsername())) {
      int len = player.getUsername().length();
      String name = player.getUsername();
      int counter = 1;
      while (isUsernameInList(player.getUsername())) {
        name = name.substring(0, len);
        name = name + "_" + counter;
        player.setUsername(name);
        counter++;
      }
      players.put(player.getClientId(), player);
      answer = "CHANGE" + player.getUsername();
    } else {
      players.put(player.getClientId(), player);
      answer = "OK" + player.getUsername();
    }
    return answer;
  }

  /**
   * Method to search for a players name in the playerList by the clientId.
   *
   * @param clientId the looked for clientId
   * @return either the correct name or null
   */
  public String getUsername(int clientId) {
    return players.get(clientId).getUsername();
  }

  /**
   * Method to search and return a player by clientId.
   *
   * @param clientId The looked for clientId
   * @return either the Player or null
   */
  public Player getPlayer(int clientId) {
    return players.get(clientId);
  }

  /**
   * Method to remove a player by his clientId.
   *
   * @param clientId the clientId of the player to be removed
   * @return true or false depending on whether the player was in the list or not
   */
  public boolean removePlayer(int clientId) {
    if (players.containsKey(clientId)) {
      players.remove(clientId);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Boolean method to check, whether a player is in the playerList or not.
   *
   * @param clientId The clientId of the player to be looked up
   * @return True if the clientId is in the List, false if not
   */
  public boolean isClientIdInList(int clientId) {
    if (getPlayer(clientId) != null) {
      return true;
    }
    return false;
  }

  /**
   * Boolean method to check, whether an username is in the playerList or not Loops over the whole
   * playerList and checks, whether the username is already in the list or not.
   *
   * @param username The username which should be looked up and compared to all the username in the
   *     list
   * @return True if the username is in the list or false if it is not yet in the list
   */
  public boolean isUsernameInList(String username) {
    try {
    for (Player p : players.values()) {
      if (username.equals(p.getUsername())) {
        return true;
      }
    } } catch (NullPointerException e) {
      return false;
    }
    return false;
  }

  /**
   * Get the client id for the user after the "@" in a String.
   *
   * <p>Checks for subsets of names too.
   *
   * @param message the message to be sent
   * @return the client id for the target player, 0 for lobby chat message or -2 for a broadcast
   *     message.
   */
  public int getClientIdForWhisper(String message) {
    int x = 0;
    int j = 0;
    message = message.substring(1);

    if (message.startsWith("all")) {
      return -2;
    }

    ConcurrentHashMap<Integer, Integer> player = new ConcurrentHashMap<>();
    for (Player Player : players.values()) {
      if (message.startsWith(Player.getUsername())) {

        x++;
        for (int i = 0; i < Player.getUsername().length(); i++) {
          if (message.charAt(i) == Player.getUsername().charAt(i)) {
            j++;
          }
          player.put(j, Player.getClientId());
        }
      }
    }
    if (x == 1) {
      return player.get(j);
    }
    if (x == 0) {
      return -1;
    } else {
      int max = 0;
      for (int i : player.keySet()) {
        if (max < i) {
          max = i;
        }
      }
      return player.get(max);
    }
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    if (players.size() > 0) {
      s.append("OK║");
      for (Player l : players.values()) {
        s.append(l.getUsername()).append("║");
      }
    } else {
      s = new StringBuilder("No Players online");
    }
    return s.toString();
  }

  /**
   * Method to return all players in the playerList.
   *
   * @return all players in the playerList
   */
  public ConcurrentHashMap<Integer, Player> getPlayers() {
    return players;
  }
}
