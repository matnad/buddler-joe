package net.packets;

public class PacketChooseLobby extends Packet {

    /**
     * A packed which is send from the client to the Server once
     * he has choosen a Lobby to join. Server should then move the client in
     * the choosen Lobby
     */
    public PacketChooseLobby(int clientId, String data) {
        super("LOBJO");

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
