package net.packets;

import net.ClientLogic;
import net.ServerLogic;

public class PacketDisconnect extends Packet{

    private String username;

    public PacketDisconnect(String data) {
        super(DISCP);
        this.username = data;
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
