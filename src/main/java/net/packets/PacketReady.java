package net.packets;

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
                new PacketStartRound().sendToLobby(lobby.getLobbyId());
                //TODO:Try to Trigger start packet
            }else{
                //TODO: set ready for the sender
            }

        }
    }
}
