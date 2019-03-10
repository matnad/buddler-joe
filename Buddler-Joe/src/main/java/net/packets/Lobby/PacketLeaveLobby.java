package net.packets.Lobby;

import net.packets.Packet;

public class PacketLeaveLobby extends Packet {

    public PacketLeaveLobby(int clientId, String data) {
        super(PacketTypes.LEAVE_LOBBY);

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
