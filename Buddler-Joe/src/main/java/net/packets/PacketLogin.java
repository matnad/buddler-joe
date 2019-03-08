package net.packets;

import net.*;

public class PacketLogin extends Packet{

    private Player player;
    private ServerPlayerList playerList;
    private ClientThread thread;

    /**
     * Login Packet which gets sent first by the client. Validates the Login package, checks whether the
     * Username is already taken and if not adds the player to the ServerPlayerList. Also creates instance
     * of a player with the needed information to forward to the ServerPlayerList.
     * @param playerList ServerPlayerList of the Server
     * @param clientId of the player to be added to the Player instance
     * @param data username to create the player
     * @param thread the player thread to be added to the Player instance to be used later
     */

    //TODO: Exceptions!


    public PacketLogin(ServerPlayerList playerList, int clientId, String data, ClientThread thread) {
        super(PLOGI);
        if(!validate(data)){
            return;
        }
        this.thread = thread;
        this.playerList = playerList;
        this.player = new Player(data, clientId, thread, 0);
        if(playerList.addPlayer(player) == 1){
            PacketLoginSuccessful successful = new PacketLoginSuccessful(playerList, clientId, thread);
        } else {
            thread.sendToClient("Login unsuccessful, please try with another name.");
        }
    }

    public boolean validate(String data){
        return true;
    }

    public void writeData(ClientLogic client) {
        //client.sendData(getData());
    }

    @Override
    public void writeData(ServerLogic server) {
        //server.sendDataToAllClients(getData());
    }

    @Override
    public String getData() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "PacketLogin{" +
                "player=" + player.toString() +
                ", playerList=" + playerList.toString() + '\'' +
                '}';
    }

    public String getUsername() {
        return player.getUsername();
    }

    public int getPing() {
        return player.getPing();
    }

    public int getThreadNr() {
        return player.getClientId();
    }

}
