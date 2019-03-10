package net.PlayerHandling;

public class Player {

    private String username;
    private int clientId;
    private int ping;
    ClientThread thread;

    /**
     * Main Player class to save the vital information which the server has to access at all times
     * @param username for the player to be set and which is to be displayed in the game
     * @param clientId to identify the player, unique to every player
     * @param thread on which the client runs on from the server perspective
     * @param ping current ping to be updated every 10 seconds
     */

    public Player(String username, int clientId, ClientThread thread, int ping) {
        this.username = username;
        this.thread = thread;
        this.clientId = clientId;
        this.ping = ping;
    }

    public ClientThread getThread() {
        return thread;
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

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }



    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", clientId=" + clientId +
                ", ping=" + ping +
                '}';
    }

}
