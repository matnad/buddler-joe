package net.playerhandling;

import java.util.HashMap;

public class ServerPlayerList {

    private HashMap<Integer, Player> players;

    public ServerPlayerList() {
        this.players = new HashMap<Integer, Player>();
    }

    /**
     * Method to add a player the the HashMap of all to the server connected players.
     * @param player The player to be added to the HashMap
     * @return statement to let for example the PackageLogin instance know,
     * whether the action was successful or not
     */

    public String addPlayer(Player player){
        if(players.containsKey(player.getClientId())){
            return "Already loggedin.";
        }
        for(Player p : players.values()){
            if(player.getUsername().equals(p.getUsername())) {
                return "Username already taken.";
            }
        }
        players.put(player.getClientId(), player);
        return "OK";

    }

    /**
     * Method to search for a players name in the Hashmap by using the clientId
     * @param clientId the looked tor clientId
     * @return either the correct name or null
     */

    public String getUsername(int clientId){
        return players.get(clientId).getUsername();
    }

    /**
     * Method to search and return a player by clientId
     * @param clientId
     * @return either the Player or null
     */

    public Player getPlayer(int clientId) {
        return players.get(clientId);
    }

    /**
     * Method to remove a player by his clientId
     * @param clientId the clientId of the player to be removed
     * @return true or false depending on wheter the player was in the list or not
     */

    public boolean removePlayer(int clientId){
        if(players.containsKey(clientId)){
            players.remove(clientId);
            return true;
        } else{
            return false;
        }
    }

    public HashMap<Integer, Player> getPlayers() {
        return players;
    }
}
