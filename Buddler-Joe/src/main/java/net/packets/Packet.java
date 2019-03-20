package net.packets;

import net.ClientLogic;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.playerhandling.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import static net.ServerLogic.getPlayerList;

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
        GET_LOBBIES("LOBGE"),
        LEAVE_LOBBY("LOBLE"),
        JOIN_LOBBY("LOBJO"),
        CREATE_LOBBY("LOBCR"),
        PING("UPING"),
        PONG("PONGU"),
        CREATE_LOBBY_STATUS("LOBCS"),
        JOIN_LOBBY_STATUS("LOBJS"),
        LOBBY_OVERVIEW("LOBOV"),
        CUR_LOBBY_INFO("LOBCI"),
        GET_LOBBY_INFO("LOBGI"),
        LEAVE_LOBBY_STATUS("LOBLS"),
        CHAT_MESSAGE_TO_SERVER("CHATS"),
        CHAT_MESSAGE_TO_CLIENT("CHATC"),
        CHAT_MESSAGE_STATUS("CHATN");

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

    /**
     * This Method calls the sendToClient Method for each player in the specified lobby.
     * @param lobbyId the lobbyId of the lobby to which the packet should be send.
     */
    public void sendToLobby(int lobbyId){
        Lobby lobby = ServerLogic.getLobbyList().getLobby(lobbyId);
        ArrayList<Player> players = lobby.getLobbyPlayers();
        for (Player p : players) {
            sendToClient(p.getClientId());
        }
    }

    public void sendToAllClients(){
        HashMap<Integer, Player> players = ServerLogic.getPlayerList().getPlayers();
        for (Player p : players.values()) {
            sendToClient(p.getClientId());
        }
    }

    /**
     * This Method calls the sendToClient Method for each player on the server that is currently not in a Lobby.
     */
    public void sendToClientsNotInALobby(){
        HashMap<Integer, Player> players = ServerLogic.getPlayerList().getPlayers();
        for (Player p : players.values()) {
            //System.out.println("Username: " + p.getUsername() + " curLobbyId: " + p.getCurLobbyId());
            if(p.getCurLobbyId() == 0) {
                sendToClient(p.getClientId());
            }
        }
    }

    public void sendToServer(){
        ClientLogic.sendToServer(this.toString());
    }

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
        //System.out.println(s);
        char[] charArray = s.toCharArray();
        for (char c : charArray) {
            //System.out.println((int)c + "   " +c);
            if(c>255){
                addError("Invalid characters, only extended ASCII.");
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
        StringJoiner statusJ = new StringJoiner(" ","ERRORS: ","");
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

    /**
     * This method checks if the client how send this Packet is logged in or not.
     * This method should only be called on the serverside, since it will always return false on the clientside.
     * @return true if logged in else false.
     */
    public boolean isLoggedIn(){
        try{
            if(!ServerLogic.getPlayerList().getPlayers().containsKey(getClientId())){
                //addError("Not loggedin yet.");
                return false;
            }else{
                return true;
            }
        }catch(NullPointerException e){
            return false;
        }
    }

    /**
     * This method checks if the client how send this Packet is currently in a Lobby.
     * This method should only be called on the serverside, since it will always return false on the clientside.
     * @return true if in a Lobby else false.
     */
    public boolean isInALobby(){
        try{
            int lobbyId = ServerLogic.getPlayerList().getPlayers().get(getClientId()).getCurLobbyId();
            if(lobbyId != 0){
                //addError("Already in a lobby");
                return true;
            }else{
                return false;
            }
        }catch(NullPointerException e){
            return false;
        }
    }


    public String toString() {
        return getPacketType().getPacketCode() + " " + getData();
    }


}
