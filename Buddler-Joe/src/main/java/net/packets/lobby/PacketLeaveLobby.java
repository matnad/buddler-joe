package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;

public class PacketLeaveLobby extends Packet {

    /**
     * A packed which is send from the client to the Server if
     * he wants to leave his current lobby
     */
    public PacketLeaveLobby(int clientId, String data) {
        super(PacketTypes.LEAVE_LOBBY);
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
