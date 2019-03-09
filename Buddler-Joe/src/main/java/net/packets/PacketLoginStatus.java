package net.packets;

import net.*;

public class PacketLoginStatus extends Packet{

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
    public String getPackage() {
        return "PLOGS" + " " + getData();
    }

    @Override
    public String toString() {
        return "PacketLoginSuccessful{" +
                "clientId=" + getClientId() + '\'' +
                '}';
    }
}
