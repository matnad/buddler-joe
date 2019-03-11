package net.packets.Lobby;

import net.ServerLogic;
import net.packets.Packet;

public class PacketLeaveLobby extends Packet {

    /**
     * A packed which is send from the client to the Server if
     * he wants to leave his current lobby
     */
    public PacketLeaveLobby(int clientId, String data) {
        super(PacketTypes.LEAVE_LOBBY);
        validate();
        setData(data);
        setClientId(clientId);
        processData();
    }

    @Override
    public boolean validate() {
        return false;
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
        return null;
    }
}
