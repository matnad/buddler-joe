package net.packets;

import java.util.Map;

public class PacketLobbyOverview extends Packet {

    /**
     * A packed which is send to the client before joining a Lobby.
     * It should contain information to all open lobbys that are available
     * on the server and not full. (Maximum 10)
     */
    public PacketLobbyOverview(int clientId, String data) {
        super("LOBOV");

    }

    @Override
    public boolean validate(String data) {
        return false;
    }

    @Override
    public void processData(String data) {

    }

    @Override
    public String getData() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }


}
