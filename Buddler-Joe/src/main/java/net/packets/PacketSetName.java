package net.packets;

import net.ServerPlayerList;

public class PacketSetName extends Packet {

    private int clientId;
    private String data;

    public PacketSetName(int clientId, String data) {
        super("SETNM");

    }

    @Override
    public boolean validate(String data) {
        return false;
    }

    @Override
    public void processData(String data) {

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
