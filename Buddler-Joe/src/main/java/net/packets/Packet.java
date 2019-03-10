package net.packets;

import net.ClientLogic;
import net.ServerLogic;

import java.util.HashMap;

/**
 *  Abstract Packet class which all Packets implement and build upon.
 *  The enum represents all possible packages which can be implemented by the server/client
 */

public abstract class Packet {

    public enum PacketTypes {

        INVALID("INVAL"),
        LOGIN("PLOGI"),
        LOGIN_STATUS("PLOGS"),
        //MOVE(MOVEP),
        DISCONNECT("DISCP"),
        GET_NAME("GETNM"),
        PING("UPING"),
        PONG("PONGU"),
        SET_NAME("SETNM");

        private final String packetId;

        /**
         * Constructor to assign the packet type to the subclass
         * @param packetId to Assign the packet ID so that the subclass is
         *                 clearly identified
         */
        PacketTypes(String packetId) {
            this.packetId = packetId;
        }

        public String getPacketId() {
            return packetId;
        }
    }

    /**
     * Variables which are accessible for the subclasses with the later Getter/Setter methods.
     */

    private PacketTypes packetId;
    private int clientId;
    private HashMap<Integer, Boolean> sentToPlayer;
    private String data;

    public Packet(PacketTypes packetId) {
        this.packetId = packetId;
        this.sentToPlayer = new HashMap<Integer, Boolean>();
    }

    /**
     *
     * Abstract classes which are vital to the functionality of every package.
     *
     * The validate method is meant to be used to validate the data sent to the specific package,
     *
     * Process data is where the packages do their work and handle the package according to its
     * functionality.
     *
     * Get Package creates a package which then can be sent.
     *
     * toString() to display the package and make it human readable.
     *
     */
    public abstract boolean validate();

    public abstract void processData();

    public abstract String getPackage();

    public abstract String toString();

    /**
     * Communication method to send data to another client. The destination address is determined by their
     * clientId. At the same time it is also checked wheter the package has already been sent to this player.
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
        //TODO: Method to get the players from one lobby and then send them the package
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

    public void setPacketId(PacketTypes packetId) {
        this.packetId = packetId;
    }

    public PacketTypes getPacketId() {
        return packetId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public HashMap<Integer, Boolean> getSentToPlayer() {
        return sentToPlayer;
    }

    public void setSentToPlayer(HashMap<Integer, Boolean> sentToPlayer) {
        this.sentToPlayer = sentToPlayer;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    /**
     * Method to add a player to the Hashmap which saves the players and whether the package has already
     * been sent to this certain player
     * @param receiverId The playerId of the player which should receive the package
     * @return boolean to check wheter it was possible to add the player to the list or not.
     */

    public boolean addPlayerToSentToPlayer(int receiverId){
        if(!sentToPlayer.containsKey(receiverId)){
            sentToPlayer.put(receiverId,false);
        }
        return false;
    }
}
