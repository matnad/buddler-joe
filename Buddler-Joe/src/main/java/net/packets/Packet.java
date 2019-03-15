package net.packets;

import net.ClientLogic;
import net.ServerLogic;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 *  Abstract Packet class which all Packets implement and build upon.
 *  The enum represents all possible packages which can be implemented by the server/client
 */

public abstract class Packet {

    public enum PacketTypes {

        INVALID("INVAL"),
        LOGIN("PLOGI"),
        LOGIN_STATUS("PLOGS"),
        DISCONNECT("DISCP"),
        GET_NAME("GETNM"),
        SEND_NAME("SENDN"),
        SET_NAME("SETNM"),
        SET_NAME_STATUS("STNMS"),
        GET_LOBBIES("LOBOV"),
        LEAVE_LOBBY("LOBLE"),
        JOIN_LOBBY("LOBJO"),
        CREATE_LOBBY("LOBCRE"),
        PING("UPING"),
        PONG("PONGU"),
        CREATE_LOBBY_STATUS("LOBCS"),
        JOIN_LOBBY_STATUS("LOBJS");


        private final String packetCode;

        /**
         * Constructor to assign the packet type to the subclass
         * @param packetCode to Assign the packet ID so that the subclass is
         *                 clearly identified
         */
        PacketTypes(String packetCode) {
            this.packetCode = packetCode;
        }

        public String getPacketCode() {
            return packetCode;
        }
    }

    /**
     * Variables which are accessible for the subclasses with the later Getter/Setter methods.
     */
    private List<String> errors = new ArrayList<>();
    private PacketTypes packetType;
    private int clientId;

    private String data;

    public Packet(PacketTypes packetType) {
        this.packetType = packetType;
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

    public abstract void validate();

    public abstract void processData();

    public static PacketTypes lookupPacket(String code) {
        for (PacketTypes p : PacketTypes.values()) {
            if (p.getPacketCode().equals(code)) {
                return p;
            }
        }
        return PacketTypes.INVALID;
    }
    /**
     * Communication method to send data to another client. The destination address is determined by their
     * clientId. At the same time it is also checked wheter the package has already been sent to this player.
     * @param receiver the receiving clientId
     */

    public void sendToClient(int receiver) {
            ServerLogic.sendPacket(receiver, this);
    }

    public void sendToLobby(int lobbyId){
        //TODO: Method to get the players from one lobby and then send them the package
    }

    public void sendToServer(){
        ClientLogic.sendToServer(this.toString());
    };

    public void setPacketType(PacketTypes packetType) {
        this.packetType = packetType;
    }

    public PacketTypes getPacketType() {
        return packetType;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }



    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors(){
        return errors.size() > 0;
    }

    public void addError(String name){
        errors.add(name);
    }

    protected boolean isExtendedAscii(String s){
        char[] charArray = s.toCharArray();
        for (char c : charArray) {
            if(c>255){
                addError("Invalid characters in username. Only extended ASCII.");
                return false;
            }
        }
        return true;
    }

    protected boolean isInt(String s) {
        boolean h = true;
        try {
            int i = Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException nfe) {
            h = false;
        }
        return h;
    }

    protected String createErrorMessage(){
        String message = "";
        StringJoiner statusJ = new StringJoiner("\n","ERRORS: ","");
        for (String error : getErrors()) {
            statusJ.add(error);
        }
        return message = statusJ.toString();
    }

    protected void checkUsername(String username){
        if(username == null){
            addError("No username found.");
            return;
        }
        if(username.length() > 30){
            addError("Username to long. Maximum is 30 Characters.");
        }else if(username.length() < 4){
            addError("Username to short. Minimum is 4 Characters.");
        }
        isExtendedAscii(username);
    }

    public String toString() {
        return getPacketType().getPacketCode() + " " + getData();
    }


}
