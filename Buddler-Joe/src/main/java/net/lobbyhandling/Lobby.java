package net.lobbyhandling;

import net.playerhandling.ClientThread;
import net.playerhandling.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Lobby {

    private int lobbyId;
    private String lobbyName;
    private ArrayList<Player> lobbyPlayers; //TODO: replace with arraylist
    private static int lobbyCounter = 0;
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

    public int addPlayer(Player player){
        if(lobbyPlayers.contains(player)){
            return -1;
        }
        lobbyPlayers.add(player);
        return 1;
    }

    /**
     * Method to remove a player by his clientId
     * @param player the player to be removed
     * @return true or false depending on wheter the player was in the list or not
     */

    public int removePlayer(Player player){
        if(lobbyPlayers.contains(player)){
            lobbyPlayers.remove(player);
            return 1;
        } else{
            return -1;
        }
    }

    public int getPlayerAmount(){ return lobbyPlayers.size(); }
    public int getLobbyId() { return lobbyId; }
    public String getLobbyName() { return lobbyName; }

    public String toString(){
        String s = "Name: " + lobbyName + ", LobbyId: " + lobbyId + ", Spieler: " + getPlayerAmount() + "\n";
        return s;
    }
}
