package net.packets;

import net.*;

public class PacketLoginSuccessful extends Packet{

    private int clientId;
    private ServerPlayerList playerList;
    ClientThread thread;



    public PacketLoginSuccessful(ServerPlayerList playerList, int clientId, ClientThread thread) {
        super(PLOGS);
        this.clientId = clientId;
        this.playerList = playerList;
        this.thread = thread;

        playerList.searchName(clientId);

    }

    private void notifyClient(ClientThread thread){
        thread.sendToClient("Successful login.");
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
        return "PacketLoginSuccessful{" +
                "clientId=" + clientId + '\'' +
                '}';
    }
}
