package net;

public class Player {

    private String username;
    private int clientId;
    private int ping;
    ClientThread thread;

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
