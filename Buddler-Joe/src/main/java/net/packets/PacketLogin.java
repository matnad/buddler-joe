package net.packets;

import net.*;

public class PacketLogin extends Packet{

    private Player player;
    private ServerPlayerList playerList;
    private ClientThread thread;


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
