package net;

import bin.Game;

import java.util.HashMap;

public class Lobby {

    private int lobbyId;
    private String lobbyName;
    private HashMap<Integer, Player> lobbyPlayers;
    //private Game game;
    //private ChatRoom chatRoom;

    /**
     * Main Player class to save the vital information which the server has to access at all times
     * @param lobbyName for the lobby to be set and which is to be displayed in the game
     * @param lobbyId to identify the lobby, unique to every lobby
     */

    public Lobby(int lobbyId, String lobbyName)  {
        this.lobbyId = lobbyId;
        this.lobbyName = lobbyName;
        //this.game = new Game();
        //this.chatRoom = new ChatRoom();
        this.lobbyPlayers = new HashMap<Integer, Player>();

    }

    /**
     * Method to add a player the the HashMap of all players in this lobby.
     * @param player The player to be added to the HashMap
     * @return statement to let for example the PackageJoinLobby instance know,
     * whether the action was successful or not
     */

    public int addPlayer(Player player){
        if(lobbyPlayers.containsKey(player.getClientId())){
            return -1;
        }
        lobbyPlayers.put(player.getClientId(), player);
        return 1;
    }

    /**
     * Method to remove a player by his clientId
     * @param clientId the clientId of the player to be removed
     * @return true or false depending on wheter the player was in the list or not
     */

    public boolean removePlayer(int clientId){
        if(lobbyPlayers.containsKey(clientId)){
            lobbyPlayers.remove(clientId);
            return true;
        } else{
            return false;
        }
    }

    /**
     * Method to search for a players name in the Hashmap by using the clientId
     * @param clientId the looked tor clientId
     * @return either the correct name or null
     */

    public String searchName(int clientId){
        return lobbyPlayers.get(clientId).getUsername();
    }

    /**
     * Method to seach for a certain thread via the clientId to then return it to the method called
     * @param clientId
     * @return either the thread or null
     */

    public ClientThread searchThread(int clientId) {
        return lobbyPlayers.get(clientId).getThread();
    }

    /**
     * search a client via the username to find the clientId in case only one of the two was supplied
     * to the method.
     * @param username of the player to be found in the list
     * @return either the clientId or -1 if not found
     */

    public int searchClientId(String username){
        for(Player p : lobbyPlayers.values()){
            if(username == p.getUsername()) {
                return p.getClientId();
            }
        }
        return -1;
    }

    /**
     * @return the number of Players in this Lobby
     */

    public int getPlayerAmount(){ return lobbyPlayers.size(); }


    public int getLobbyId() { return lobbyId; }
    public String getLobbyName() { return lobbyName; }

}
