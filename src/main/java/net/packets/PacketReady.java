package net.packets;

import game.History;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.playerhandling.Player;

public class PacketReady extends Packet {


    public PacketReady(int clientId) {
        // server builds
        super(PacketTypes.READY);
        setClientId(clientId);
        validate();
    }

    public PacketReady() {
        // client builds
        super(PacketTypes.READY);
        validate();
    }



    @Override
    public void validate() {
        // No data to validate since it is a Empty Packet
    }

    @Override
    public void processData() {
        if (isLoggedIn() && isInALobby()) {
            Player player = ServerLogic.getPlayerList().getPlayer(getClientId());
            int lobbyId = player.getCurLobbyId();
            Lobby lobby = ServerLogic.getLobbyList().getLobby(lobbyId);
            //check ob sender der ersteller ist.
            if(getClientId() == lobby.getCreaterPlayerId()){
                lobby.setStatus("running");
                History.openRemove(lobby.getLobbyId());
                History.runningAdd(lobby.getLobbyId(),lobby.getLobbyName());
                new PacketStartRound().sendToLobby(lobby.getLobbyId());
                //TODO: only start if all players are ready
            }else{
                //TODO: set ready for the sender
            }

        }
    }
}
