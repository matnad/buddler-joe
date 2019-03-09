package net.packets;

public class PacketPong extends Packet {

    private int clientId;
    private String data;

    public PacketPong(int clientId, String data) {
        super(Packet.PacketTypes.PONG);

        if(!validate()){
            setPacketId(Packet.PacketTypes.PONG);
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
