package net.packets;

import net.*;

public class PacketLoginStatus extends Packet{

    private int clientId;
    private String data;
    private ServerPlayerList playerList;

    /**
     * Package to respond to the client that the Login has been successful
     * @param clientId to find the player in the list
     */

    //TODO: Exceptions!


    public PacketLoginStatus(int clientId, String data) {
        super("PLOGS");
        this.data = data;
        this.clientId = clientId;
        this.playerList = ServerLogic.getPlayerList();
        Integer.parseInt(data);

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
