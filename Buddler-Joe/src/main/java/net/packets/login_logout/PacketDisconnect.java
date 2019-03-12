package net.packets.login_logout;

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
        validate();
    }

    @Override
    public void validate() {

    }

    @Override
    public void processData() {

    }
}
