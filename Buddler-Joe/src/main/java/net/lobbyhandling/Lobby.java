package net.lobbyhandling;

import net.ServerLogic;
import net.playerhandling.ClientThread;
import net.playerhandling.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Lobby {

    private int lobbyId;
    private String lobbyName;
    private ArrayList<Player> lobbyPlayers; //TODO: replace with arraylist
    private static int lobbyCounter = 1;
    //private Game game;
    //private ChatRoom chatRoom;

    /**
     * Main Player class to save the vital information which the server has to access at all times
     * @param lobbyName for the lobby to be set and which is to be displayed in the game
     */

    public Lobby(String lobbyName)  {
        this.lobbyId = lobbyId;
        this.lobbyName = lobbyName;
        //this.game = new Game();
        //this.chatRoom = new ChatRoom();
        this.lobbyPlayers = new ArrayList<>();
        this.lobbyId = lobbyCounter;
        lobbyCounter++;
    }

    /**
     * Method to add a player the the HashMap of all players in this lobby.
     * @param player The player to be added to the HashMap
     * @return statement to let for example the PackageJoinLobby instance know,
     * whether the action was successful or not
     */

    public String addPlayer(Player player){
        if(lobbyPlayers.contains(player)){
            return "Already joined this lobby.";
        }
        lobbyPlayers.add(player);
        return "OK";
    }

    /**
     * Method to remove a player by his clientId
     * @param clientId of the player to be removed
     * @return String with "OK" or "Not in a Lobby" depending on if the removing was succesfull or not
     */

    public String removePlayer(int clientId){
        if(ServerLogic.getPlayerList().isPlayerIdInList(clientId)){
            Player player = ServerLogic.getPlayerList().getPlayer(clientId);
            lobbyPlayers.remove(player);
            return "OK";
        }else{
            return "Not in a Lobby";
        }

    }


    public int getPlayerAmount(){ return lobbyPlayers.size(); }
    public int getLobbyId() { return lobbyId; }
    public String getLobbyName() { return lobbyName; }

    public ArrayList<Player> getLobbyPlayers() {
        return lobbyPlayers;
    }

    /**
     *
     * @return a String with the usernames of all Playeers in this Lobby seperated by "║".
     */
    public String getPlayerNames(){
        String s = "";
        for (Player player : lobbyPlayers) {
            s = s + player.getUsername() + "║";
        }
        return s;
    }

    public String toString(){
        String s = "Name: " + lobbyName + ", LobbyId: " + lobbyId + ", Spieler: " + getPlayerAmount();
        return s;
    }

}
