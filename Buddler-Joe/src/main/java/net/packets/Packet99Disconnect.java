package net.packets;

import net.ClientLogic;
import net.ServerLogic;

public class Packet99Disconnect extends Packet{

    private String username;

    public Packet99Disconnect(byte[] data) {
        super(99);
        this.username = readData(data);
    }

    public Packet99Disconnect(String username) {
        super(99);
        this.username = username;
    }

    @Override
    public void writeData(ClientLogic client) {
        //client.sendData(getData());
    }

    @Override
    public void writeData(ServerLogic server) {
        //server.sendDataToAllClients(getData());
    }

    @Override
    public byte[] getData() {
        return ("99" + this.username).getBytes();
    }

    public String getUsername() {
        return username;
    }
}
