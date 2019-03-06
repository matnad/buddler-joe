package net.packets;

import entities.NetPlayer;
import net.ClientLogic;
import net.ServerLogic;

import java.net.InetAddress;

public abstract class Packet {

    public static enum PacketTypes {
        INVALID(-1),
        LOGIN(00),
        MOVE(01),
        //PING(02);
        DISCONNECT(99);

        private int packetId;
        private PacketTypes(int packetId) {
            this.packetId = packetId;
        }

        public int getPacketId() {
            return packetId;
        }
    }

    private byte packetId;

    public Packet(int packetId) {
        this.packetId = (byte) packetId;
    }

    public abstract byte[] getData();

    public abstract void writeData(ClientLogic client);
    public abstract void writeData(ServerLogic client);

    public static PacketTypes lookupPacket(String packetId) {
        try {
            return lookupPacket(Integer.parseInt(packetId));
        } catch (NumberFormatException e) {
            return PacketTypes.INVALID;
        }
    }

    public String readData(byte[] data) {
        String message = new String(data).trim();
        return message.substring(2);
    }

    public static PacketTypes lookupPacket(int id) {
        for (PacketTypes p : PacketTypes.values()) {
            if (p.getPacketId() == id) {
                return p;
            }
        }
        return PacketTypes.INVALID;
    }

    public void setPacketId(byte packetId) {
        this.packetId = packetId;
    }
}
