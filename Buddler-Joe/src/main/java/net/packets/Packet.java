package net.packets;

/**
 *  Abstract Packet class which all Packets implement and build upon
 */

public abstract class Packet {

    public enum PacketTypes {

        INVALID("INVAL"),
        LOGIN("PLOGI"),
        LOGIN_SUCCESSFUL("PLOGS"),
        //MOVE(MOVEP),
        DISCONNECT("DISCP"),
        GETNAME("GETNM");

        private final String packetId;

        /**
         * Constructor to assign the packet type to the subclass
         * @param packetId
         */
        PacketTypes(String packetId) {
            this.packetId = packetId;
        }

        public String getPacketId() {
            return packetId;
        }
    }

    private String packetId;
    private int clientId;
    private boolean sent;
    private String data;

    public Packet(String packetId) {
        this.packetId = packetId;
    }

    /**
     * Abstract classes to be implemented by all subclasses, perform standard operations every
     * Package needs to have.
     */
    public abstract boolean validate(String data);

    public abstract void processData();

    public abstract String getData();

    public abstract void sendToClient(int clientId);

    public abstract void sendToLobby(int lobbyId);

    public abstract void sendToServer();

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
        return PacketTypes.INVALID;
    }

    public String readData(String data) {
        return data.substring(5,data.length()-1);
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }
}
