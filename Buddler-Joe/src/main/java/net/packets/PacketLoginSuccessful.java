package net.packets;

import net.*;

public class PacketLoginSuccessful extends Packet{

    private int clientId;
    private ServerPlayerList playerList;

    /**
     * Package to respond to the client that the Login has been successful
     * @param playerList playerlist to find the thread belonging to this client
     * @param clientId to find the player in the list
     */

    //TODO: Exceptions!


    public PacketLoginSuccessful(ServerPlayerList playerList, int clientId) {
        super(PLOGS);
        this.clientId = clientId;
        this.playerList = playerList;
        ClientThread thread = playerList.searchThread(clientId);
        notifyClient(thread);
    }

    /**
     * Method to respond to the player via the thread.
     * @param thread to write to the player.
     */

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
