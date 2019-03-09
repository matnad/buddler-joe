package net.packets;

public class PacketPing extends Packet {

    private int clientId;
    private String data;

    public PacketPing(int clientId, String data) {
        super(Packet.PacketTypes.PING);

        if(!validate()){
            setPacketId(Packet.PacketTypes.PING);
            return;
        }
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void processData() {

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
