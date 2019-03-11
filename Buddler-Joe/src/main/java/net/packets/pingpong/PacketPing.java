package net.packets.pingpong;

import net.packets.Packet;

public class PacketPing extends Packet {

    private int clientId;
    private String data;

    public PacketPing(int clientId, String data) {
        super(Packet.PacketTypes.PING);

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
    public Packet getPackage() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }
}
