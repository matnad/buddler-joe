package net.packets.name;

import net.packets.Packet;

public class PacketSetName extends Packet {

    private int clientId;
    private String data;

    public PacketSetName(int clientId, String data) {
        super(PacketTypes.SET_NAME);
        setClientId(clientId);
        setData(data);
        validate();
    }


    @Override
    public void validate() {

    }

    @Override
    public void processData() {

    }
}
