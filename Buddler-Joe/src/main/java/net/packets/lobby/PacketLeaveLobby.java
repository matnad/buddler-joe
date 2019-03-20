package net.packets.lobby;

import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;
import net.playerhandling.Player;

public class PacketLeaveLobby extends Packet {

    /**
     * A packed which is send from the client to the Server if
     * he wants to leave his current lobby
     */
    public PacketLeaveLobby(int clientId) {
        //Server builds
        super(PacketTypes.LEAVE_LOBBY);
        setClientId(clientId);
        validate();
    }


    public PacketLeaveLobby() {
        //client builds
        super(PacketTypes.LEAVE_LOBBY);
    }

    @Override
    public void validate() {
        //Nothing to validate
    }

    @Override
    public void processData() {
        String status;
        int lobbyId = -1;
        if(!isLoggedIn()){
            addError("Not loggedin yet.");
        }
        if(!isInALobby()) {
            addError("You are not in a lobby.");
        }
        if(hasErrors()){
            status = createErrorMessage();
        }else{
            Player player = ServerLogic.getPlayerList().getPlayer(getClientId());
            lobbyId = player.getCurLobbyId();
            Lobby lobby = ServerLogic.getLobbyList().getLobby(lobbyId);
            status = lobby.removePlayer(getClientId());
            player.setCurLobbyId(0);
        }
        PacketLeaveLobbyStatus packetLeaveLobbyStatus = new PacketLeaveLobbyStatus(getClientId(),status);
        packetLeaveLobbyStatus.sendToClient(getClientId());

        if(!hasErrors() && status.equals("OK")){
            //LobbyOverview Update for clients that are not in a lobby
            String info = "OK║" + ServerLogic.getLobbyList().getTopTen();
            PacketLobbyOverview p = new PacketLobbyOverview(getClientId(),info);
            p.sendToClientsNotInALobby();
            //CurrentLobbyInfo Update for clients in this Lobby.
            info = "OK║" + ServerLogic.getLobbyList().getLobby(lobbyId).getPlayerNames();
            PacketCurLobbyInfo packetCurLobbyInfo = new PacketCurLobbyInfo(getClientId(),info);
            packetCurLobbyInfo.sendToLobby(lobbyId);
        }

    }
}
