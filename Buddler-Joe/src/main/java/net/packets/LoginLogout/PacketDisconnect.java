package net.packets.LoginLogout;

import net.packets.Packet;

public class PacketDisconnect extends Packet {

    /**
     * Disconnect package to disconnect a player from the server.
     * @param data data contains the username which is to be deleted from
     *             the ServerPlayerList. Calls the removePlayer function from
     *             the ServerPlayerList.
     */

    public PacketDisconnect(int clientId, String data) {
        super(PacketTypes.DISCONNECT);
        setClientId(clientId);
        setData(data);

        if(!validate()){
            setPacketId(PacketTypes.INVALID);
            return;
        }
    }

    public void processData(){

    }

    public boolean validate(){
        return true;
    }

    @Override
    public Packet getPackage() {
        return null;
    }

    @Override
    public String toString() {
        return "PacketDisconnect{}";
    }



}
