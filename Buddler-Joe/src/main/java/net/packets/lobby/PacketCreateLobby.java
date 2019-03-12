package net.packets.lobby;

import net.lobbyhandling.Lobby;
import net.lobbyhandling.ServerLobbyList;
import net.playerhandling.Player;
import net.ServerLogic;
import net.packets.Packet;

public class PacketCreateLobby extends Packet {

    private Lobby lobby;
    private ServerLobbyList lobbyList;

    /**
     * A packed which is send from the client to the Server if he wants
     * to create a new lobby. Containing the information to do so.
     */
    public PacketCreateLobby(int clientId, String data) {
        super(PacketTypes.CREATE_LOBBY);
        this.lobbyList = ServerLogic.getLobbyList();
        setClientId(clientId);
        setData(data);
        validate();
    }

    @Override
    public void validate() {

    }

    @Override
    public void processData() {
//        this.lobby = new Lobby(getData());
//        int code = lobbyList.addLobby(lobby);
//        String result = "" + code + ";" + lobby.getLobbyId();
//        PacketCreateLobbyStatus status = new PacketCreateLobbyStatus(getClientId(), result);
//        status.sendToClient(getClientId());
    }
}
