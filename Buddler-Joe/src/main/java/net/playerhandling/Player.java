package net.playerhandling;

public class Player {

    private String username;
    private int clientId;
    private int curLobbyId;

    /**
     * Constructor of the player class to create a new player
     * Creates an instance of the main Player class to save the player information
     * on the server side in the playerList. Contains vital information as well as setters and getters to access the
     * information from the server side.
     * @param username Unique username for the player to be set and which is to be displayed in the game
     * @param clientId to identify the player, unique to every player and assigned by the first login by the
     *                 ServerLogic class
     */

    public Player(String username, int clientId) {
        this.username = username;
        this.clientId = clientId;
        curLobbyId = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getCurLobbyId() { return curLobbyId; }

    public void setCurLobbyId(int curLobbyId) { this.curLobbyId = curLobbyId; }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", clientId=" + clientId +
                ", curLobbyId=" + curLobbyId +
                '}';
    }
}
