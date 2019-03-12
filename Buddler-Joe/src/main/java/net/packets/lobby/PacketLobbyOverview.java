package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;

public class PacketLobbyOverview extends Packet {

    /**
     * A packed which is sent to the client before joining a lobby.
     * It should contain information to all open lobbys that are available
     * on the server and not full. (Maximum 10)
     */

    public PacketLobbyOverview(int clientId, String data) {
        super(PacketTypes.GET_LOBBIES);
        setData(data);
        setClientId(clientId);
        validate();
    }

    @Override
    public void validate() {

    }

    @Override
    public void processData() {

    }
}
