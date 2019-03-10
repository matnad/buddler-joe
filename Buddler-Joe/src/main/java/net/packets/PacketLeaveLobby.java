package net.packets;

public class PacketLeaveLobby extends Packet{

    public PacketLeaveLobby(int clientId, String data) {
        super("LOBLE");

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
