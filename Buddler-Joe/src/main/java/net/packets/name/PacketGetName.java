package net.packets.name;

import net.packets.Packet;

public class PacketGetName extends Packet {


    public PacketGetName(int clientId, String data) {
        super(PacketTypes.GET_NAME);
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
