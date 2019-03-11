package net.packets.Lobby;

import net.ServerLogic;
import net.packets.Packet;

public class PacketLobbyOverview extends Packet {

    /**
     * A packed which is sent to the client before joining a Lobby.
     * It should contain information to all open lobbys that are available
     * on the server and not full. (Maximum 10)
     */

    public PacketLobbyOverview(int clientId, String data) {
        super(PacketTypes.GET_LOBBIES);
        setData(data);
        setClientId(clientId);
        validate();
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
        return null;
    }


}
