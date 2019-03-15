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


    @Override
    public void validate() {
        isExtendedAscii(lobbyname);
        if(isLoggedIn()){//cheack if logged in at the same time
            isInALobby();
        }
        if(ServerLogic.getLobbyList().getLobbyId(lobbyname) == -1){
            addError("Chosen lobby does not exist.");
        }
    }

    @Override
    public void processData() {
        String status;
        if(hasErrors()){
            StringJoiner statusJ = new StringJoiner("║","ERRORS:║","");
            for (String error : getErrors()) {
                statusJ.add(error);
            }
            status = statusJ.toString();
        }else{
            Player player = ServerLogic.getPlayerList().getPlayer(getClientId());
            int lobbyId = ServerLogic.getLobbyList().getLobbyId(lobbyname);
            status = ServerLogic.getLobbyList().getLobby(lobbyId).addPlayer(player);
            player.setCurLobbyId(lobbyId);

        }
        PacketJoinLobbyStatus p = new PacketJoinLobbyStatus(getClientId(),status);
        p.sendToClient(getClientId());
    }
}
