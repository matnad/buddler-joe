package net.packets.lobby;

import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;
import net.packets.login_logout.PacketLoginStatus;
import net.playerhandling.Player;

import java.util.StringJoiner;

public class PacketJoinLobby extends Packet {

    private String lobbyname;
    /**
     * A packed which is send from the client to the Server once
     * he has chosen a lobby to join. Server should then move the client in
     * the chosen lobby
     * @param clientId of the player to be added to specified lobby
     * @param data lobbyId of the chosen lobby
     */
    public PacketJoinLobby(int clientId, String data) {
        //server builds
        super(PacketTypes.JOIN_LOBBY);
        setData(data);
        setClientId(clientId);
        lobbyname = getData();
        validate();
    }

    public PacketJoinLobby(String data) {
        //client builds
        super(PacketTypes.JOIN_LOBBY);
        setData(data);
        lobbyname = getData();
        validate();
    }

    /**
     * This Method checks if the recived lobbyname is a valid, existing lobbyname.
     * And if the Player that wants to join a Lobby is logged in and if so, ih he is in a Lobby already or not.
     */
    @Override
    public void validate() {
        isExtendedAscii(lobbyname);
    }

    @Override
    public void processData() {
        String status;
        if(ServerLogic.getLobbyList().getLobbyId(lobbyname) == -1){
            addError("Chosen lobby does not exist.");
        }
        if(!isLoggedIn()){
            addError("Not loggedin yet.");
        }
        if(isInALobby()){
            addError("Already in a lobby, leave current lobby first.");
        }
        if(hasErrors()){
            status = createErrorMessage();
        }else{
            Player player = ServerLogic.getPlayerList().getPlayer(getClientId());
            int lobbyId = ServerLogic.getLobbyList().getLobbyId(lobbyname);
            status = ServerLogic.getLobbyList().getLobby(lobbyId).addPlayer(player);
            player.setCurLobbyId(lobbyId);

        }
        PacketJoinLobbyStatus p = new PacketJoinLobbyStatus(getClientId(),status);
        p.sendToClient(getClientId());
        if(!hasErrors() && status.equals("OK")){
            //CurrentLobbyInfo Update jor clients in this lobby
            int lobbyId = ServerLogic.getLobbyList().getLobbyId(lobbyname);
            String info = "OK║" + ServerLogic.getLobbyList().getLobby(lobbyId).getPlayerNames();
            PacketCurLobbyInfo pcli = new PacketCurLobbyInfo(getClientId(),info);
            pcli.sendToLobby(lobbyId);
            //LobbyOverview update jor clients currently not in a Lobby
            info = "OK║" + ServerLogic.getLobbyList().getTopTen();
            PacketLobbyOverview packetLobbyOverview = new PacketLobbyOverview(getClientId(),info);
            packetLobbyOverview.sendToClientsNotInALobby();
        }
    }
}
