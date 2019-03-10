package net.packets.Name;

import net.packets.Packet;

public class PacketSetName extends Packet {

    private int clientId;
    private String data;

    public PacketSetName(int clientId, String data) {
        super(PacketTypes.SET_NAME);

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
