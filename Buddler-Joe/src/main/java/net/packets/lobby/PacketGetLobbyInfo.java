package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;

import java.util.StringJoiner;

/**
 * A Packet that gets send from the Client to the Server, to get Information about his current lobby.
 * Packet-Code: LOBGI
 * @author Sebastian Schlachter
 */
public class PacketGetLobbyInfo extends Packet {

    /**
     * Constructor that is used by the Server to build the Packet, after receiving the Command "LOBGI".
     * @param clientId ClientId of the Client that has sent the command.
     */
    public PacketGetLobbyInfo(int clientId){
        //server builds
        super(PacketTypes.GET_LOBBY_INFO);
        setClientId(clientId);
    }

    /**
     * Constructor that will be used by the Client to build the Packet. Which can then be send to the Server.
     * There are no parameters necessary here since the Packet has no real content(only a Type, LOBGI).
     */
    public PacketGetLobbyInfo(){
        //client builds
        super(PacketTypes.GET_LOBBY_INFO);
    }

    /**
     * Dummy method. Since there is no content to validate.
     */
    @Override
    public void validate() {
        //Nothing to validate.
    }

    /**
     * Method that lets the Server react to the receiving of this packet.
     * Check that the Client that has sent the packet is logged in and in a lobby.
     * In the case of an error it gets added with {@link Packet#addError(String)}.
     * Constructs a {@link PacketCurLobbyInfo}-Packet that contains the names of all clients that are
     * in the current lobby of the sender, or in the case of an error, a suitable errormessage.
     * (names are separated by "║")
     * If there are no errors "OK" gets added to the String of the {@link PacketCurLobbyInfo}-Packet
     * Sends the {@link PacketCurLobbyInfo}-Packet to the client that has send this packet.
     */
    @Override
    public void processData() {
        if(!isLoggedIn()){
            addError("Not loggedin yet.");
        }
        if(!isInALobby()){
            addError("Not in a lobby.");
        }
        String info;
        if(hasErrors()) {
            info = createErrorMessage();
        }else{
            int lobbyId = ServerLogic.getPlayerList().getPlayers().get(getClientId()).getCurLobbyId();
            info = "OK║" + ServerLogic.getLobbyList().getLobby(lobbyId).getPlayerNames();
        }
        PacketCurLobbyInfo pcli = new PacketCurLobbyInfo(getClientId(),info);
        pcli.sendToClient(getClientId());
    }
}
