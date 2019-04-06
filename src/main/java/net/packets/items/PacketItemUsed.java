package net.packets.items;

import net.packets.Packet;

public class PacketItemUsed extends Packet {

    public PacketItemUsed() {
        super(PacketTypes.BLOCK_DAMAGE);
    }

    @Override
    public void validate() {

    }

    @Override
    public void processData() {

    }
}
