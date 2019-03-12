package net.packets.pingpong;

import net.packets.Packet;

public class PacketPong extends Packet {


    public PacketPong(int clientId, String data) {
        super(Packet.PacketTypes.PONG);
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
