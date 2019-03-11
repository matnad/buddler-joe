package net.packets.login_logout;

import net.*;
import net.playerhandling.ServerPlayerList;
import net.packets.Packet;

public class PacketLoginStatus extends Packet {

    private ServerPlayerList playerList;

    /**
     * Package to respond to the client that the Login has been successful
     * @param clientId to find the player in the list
     */

    public PacketLoginStatus(int clientId, String data) {
        super(PacketTypes.LOGIN_STATUS);
        if(!validate()){
            setPacketId(PacketTypes.INVALID);
            return;
        }
        setData(data);
        setClientId(clientId);
        processData();

    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void processData() {
        ServerLogic.sendPacket(getClientId(),this);
    }

    @Override
    public Packet getPackage() {
        return null;
    }

    @Override
    public String toString() {
        return "PacketLoginSuccessful{" +
                "clientId=" + getClientId() + '\'' +
                '}';
    }
}
