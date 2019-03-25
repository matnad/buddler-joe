package net.lobbyhandling;

import net.ServerLogic;
import net.playerhandling.Player;

import java.util.ArrayList;

/**
 * Main lobby class to save the vital information which the server has to access at all times.
 * @author Sebastian Schlachter
 */
public class Lobby {

    private int lobbyId;
    private String lobbyName;
    private ArrayList<Player> lobbyPlayers;
    private static int lobbyCounter = 1;


    /**
     * Constructor of the lobby-class uses by the Server.
     * @param lobbyName The name of the new lobby.
     * {@link Lobby#lobbyId} gets set to equal the {@link Lobby#lobbyCounter}.
     * {@link Lobby#lobbyCounter} gets raised by one after every lobby construction.
     */
    public Lobby(String lobbyName)  {
        this.lobbyName = lobbyName;
        this.lobbyPlayers = new ArrayList<>();
        this.lobbyId = lobbyCounter;
        lobbyCounter++;
    }

    /**
     * Adds a player to this Lobby.
     * @param player The player to be added to the HashMap of this Lobby.
     * @return statement to let the calling instance know,
     * whether the action was successful or not.
     * ("OK" or "Already joined this lobby.")
     */
    public String addPlayer(Player player){
        if(lobbyPlayers.contains(player)){
            return "Already joined this lobby.";
        }
        lobbyPlayers.add(player);
        return "OK";
    }

    /**
     * Removes a player from this Lobby.
     * @param clientId of the player to be removed.
     * @return String with "OK" or "Not in a Lobby" depending on if the removing was successful or not.
     */
    public String removePlayer(int clientId){
        if(ServerLogic.getPlayerList().isClientIdInList(clientId)){
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
     * Creates a listing of the Players in this Lobby.
     * @return A String with the usernames of all Players in this Lobby seperated by "║".
     */
    public String getPlayerNames(){
        StringBuilder s = new StringBuilder();
        for (Player player : lobbyPlayers) {
            s.append(player.getUsername()).append("║");
        }
        return s.toString();
    }

    @Override
    public String toString(){
        return "Name: " + lobbyName + ", LobbyId: " + lobbyId + ", Spieler: " + getPlayerAmount();
    }

    /**
     * @return true if the lobby has no players in it
     */
    public boolean isEmpty() {
        return lobbyPlayers.size() == 0;
    }

}
