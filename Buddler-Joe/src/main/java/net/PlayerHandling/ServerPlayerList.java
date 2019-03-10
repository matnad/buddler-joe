package net.PlayerHandling;

import net.PlayerHandling.ClientThread;
import net.PlayerHandling.Player;

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

    public int addPlayer(Player player){
        if(players.containsKey(player.getClientId())){
            return -1;
        }
        for(Player p : players.values()){
            if(player.getUsername() == p.getUsername()) {
                return -2;
            }
        }
        players.put(player.getClientId(), player);
        return 1;

    }

    /**
     * Method to search for a players name in the Hashmap by using the clientId
     * @param clientId the looked tor clientId
     * @return either the correct name or null
     */

    public String searchName(int clientId){
        return players.get(clientId).getUsername();
    }

    /**
     * Method to seach for a certain thread via the clientId to then return it to the method called
     * @param clientId
     * @return either the thread or null
     */

    public ClientThread searchThread(int clientId) {
        return players.get(clientId).getThread();
    }

    /**
     * search a client via the username to find the clientId in case only one of the two was supplied
     * to the method.
     * @param username of the player to be found in the list
     * @return either the clientId or -1 if not found
     */

    public int searchClientId(String username){
        for(Player p : players.values()){
            if(username == p.getUsername()) {
                return p.getClientId();
            }
        }
        return -1;
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

    /**
     * Method to print out the full player list
     * @return the full player List as a String
     */

    //TODO: print out the player list
    @Override
    public String toString() {
        return "ServerPlayerList{" +
                "players=" +
                '}';
    }
}
