package net.packets;

public class PacketCreateLobby extends Packet {

    /**
     * A packed which is send from the client to the Server if he wants
     * to create a new lobby. Containing the information to do so.
     */
    public PacketCreateLobby(int clientId, String data) {
        super("LOBCR");

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
