package net.packets;

import net.ClientLogic;
import net.ServerLogic;
import net.ServerPlayerList;

public class PacketDisconnect extends Packet{

    /**
     * Disconnect package to disconnect a player from the server.
     * @param data data contains the username which is to be deleted from
     *             the ServerPlayerList. Calls the removePlayer function from
     *             the ServerPlayerList.
     */

    public PacketDisconnect(int clientId, String data) {
        super("DISCP");
        setClientId(clientId);
    }

    public void processData(String data){

    }

    public boolean validate(String data){
        return true;
    }

    @Override
    public String getData() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "PacketDisconnect{}";
    }



}
