package net.packets.Lobby;

import net.packets.Packet;

public class PacketCreateLobby extends Packet {

    /**
     * A packed which is send from the client to the Server if he wants
     * to create a new lobby. Containing the information to do so.
     */
    public PacketCreateLobby(int clientId, String data) {
        super(PacketTypes.CREATE_LOBBY);

    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void processData() {

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
