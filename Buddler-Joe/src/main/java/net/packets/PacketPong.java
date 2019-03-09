package net.packets;

public class PacketPong extends Packet {

    private int clientId;
    private String data;

    public PacketPong(int clientId, String data) {
        super(Packet.PacketTypes.PONG);

        if(!validate()){
            setPacketId(PacketTypes.INVALID);
            return;
        }
        setClientId(clientId);
        setData(data);
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void processData() {

    }

    @Override
    public String getPackage() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

}
