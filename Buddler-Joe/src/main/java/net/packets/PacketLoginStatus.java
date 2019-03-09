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


    }

    @Override
    public boolean validate(String data) {
        return false;
    }

    @Override
    public void processData(String data) {

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
