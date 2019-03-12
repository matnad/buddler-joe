package net.packets.pingpong;

import net.packets.Packet;

public class PacketPing extends Packet {



    public PacketPing(int clientId, String data) {
        super(Packet.PacketTypes.PING);
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
