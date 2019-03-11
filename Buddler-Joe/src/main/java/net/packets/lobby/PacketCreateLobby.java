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
        processData();
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void processData() {
        this.lobby = new Lobby(getData());
        int code = lobbyList.addLobby(lobby);
        String result = "" + code + ";" + lobby.getLobbyId();
        PacketCreateLobbyStatus status = new PacketCreateLobbyStatus(getClientId(), result);
        status.sendToClient(getClientId());
    }

    @Override
    public Packet getPackage() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Player player) {
        this.lobby = lobby;
    }

    public ServerLobbyList getLobbyList() {
        return lobbyList;
    }

    public void setLobbyList(ServerLobbyList lobbyList) {
        this.lobbyList = lobbyList;
    }

}
