package net.packets.Lobby;

import net.packets.Packet;

public class PacketJoinLobby extends Packet {

    /**
     * A packed which is send from the client to the Server once
     * he has choosen a Lobby to join. Server should then move the client in
     * the choosen Lobby
     */
    public PacketJoinLobby(int clientId, String data) {
        super(PacketTypes.JOIN_LOBBY);

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
