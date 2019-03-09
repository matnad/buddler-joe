package net.packets;

import net.ClientLogic;
import net.ServerLogic;

import java.util.Map;

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
        GET_NAME("GETNM"),
        SET_NAME("SETNM");

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
    private Map<Integer, Boolean> sentToPlayer;
    private String data;

    public Packet(String packetId) {
        this.packetId = packetId;
    }

    /**
     * Abstract classes to be implemented by all subclasses, perform standard operations every
     * Package needs to have.
     */
    public abstract boolean validate(String data);

    public abstract void processData(String data);

    public abstract String getData();

    public abstract String toString();

    /**
     * Communication method to send data to another client
     * @param receiver the receiving clientId
     */

    public void sendToClient(int receiver) {
        if(sentToPlayer.get(receiver) != true){
            sentToPlayer.replace(receiver,true);
            ServerLogic.sendPacket(receiver, this);
        } else{
            return;
        }
    }

    public void sendToLobby(int lobbyId){

        //TODO: While loop over the whole lobby to send the package to the players.

        ServerLogic.sendPacketToLobby(lobbyId, this);
    };

    public void sendToServer(){
        ClientLogic.sendToServer(this.getData());
    };

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

    public String getPacketId() {
        return packetId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public Map<Integer, Boolean> getSentToPlayer() {
        return sentToPlayer;
    }

    public void setSentToPlayer(Map<Integer, Boolean> sentToPlayer) {
        this.sentToPlayer = sentToPlayer;
    }

    public void setData(String data) {
        this.data = data;
    }
}
