package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;

import java.util.StringJoiner;

public class PacketGetLobbyInfo extends Packet {


    public PacketGetLobbyInfo(int clientId){
        //server builds
        super(PacketTypes.GET_LOBBY_INFO);
        setClientId(clientId);
    }

    public PacketGetLobbyInfo(){
        //client builds
        super(PacketTypes.GET_LOBBY_INFO);
    }

    @Override
    public void validate() {

    }

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
