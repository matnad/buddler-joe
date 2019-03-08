package net.packets;

import net.ClientLogic;
import net.ServerLogic;
import net.ServerPlayerList;

public class PacketDisconnect extends Packet{

    private String username;
    private ServerPlayerList playerList;

    /**
     * Disconnect package to disconnect a player from the server.
     * @param data data contains the username which is to be deleted from
     *             the ServerPlayerList. Calls the removePlayer function from
     *             the ServerPlayerList.
     */

    public PacketDisconnect(String data, ServerPlayerList playerList) {
        super(DISCP);
        this.playerList = playerList;
        this.username = data;
        deletePlayer();
    }

    /**
     * Method to delete the player from the ServerPlayerList.
     */

    //TODO: Exceptions!

    public void deletePlayer(){
        playerList.removePlayer(playerList.searchClientId(username));
    }
//    public PacketDisconnect(String username) {
//        super(DISCP);
//        this.username = username;
//    }

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

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "PacketDisconnect{" +
                "username='" + username + '\'' +
                '}';
    }
}
