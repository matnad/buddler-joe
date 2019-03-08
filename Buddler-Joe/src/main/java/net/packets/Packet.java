package net.packets;

import net.ServerLogic;

public abstract class Packet {

    public enum PacketTypes {

        //TODO: Fix enum...

        INVALID(INVAL),
        LOGIN(PLOGI),
        LOGIN_SUCCESSFUL(PLOGS),
        //MOVE(MOVEP),
        DISCONNECT(DISCP),
        GETNAME(GETNM);

        private final String packetId;
        PacketTypes(String packetId) {
            this.packetId = packetId;
        }

        public String getPacketId() {
            return packetId;
        }
    }

    public String packetId;

    public Packet(String packetId) {
        this.packetId = packetId;
    }

    public abstract String getData();

    public abstract void writeData(ServerLogic client);

    public static PacketTypes lookupPacket(String packetId) {
        try {
            for (PacketTypes p : PacketTypes.values()) {
                if (p.getPacketId() == packetId) {
                    return p;
                }
            }
        } catch (NumberFormatException e) {
            return PacketTypes.INVALID;
        }
    }

    public String readData(String data) {
        return data.substring(5,data.length()-1);
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }
}
