package net.packets;

import net.ServerLogic;
import net.ServerPlayerList;

public class PacketGetName extends Packet{

    private ServerPlayerList playerList;
    private int clientId;
    private String username;

    public PacketGetName(ServerPlayerList playerList, String data, int clientId) {
        super(GETNM);
        this.playerList = playerList;
        this.clientId = clientId;
        if(!validate(data)){
            return;
        }

        playerList.searchName(clientId);

    }

    private boolean validate(String data){
        //TODO: Write the validation method
        return true;
    }

    public PacketGetName(String username, int clientId) {
        super(GETNM);
        this.clientId = clientId;
        this.username = username;

    }

    public String getUsername() {
        return username;
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
        return "PacketGetName{" +
                "playerList=" + playerList.toString() +
                ", clientId=" + clientId +
                ", username='" + username + '\'' +
                '}';
    }

}
