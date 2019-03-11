package net.packets.Lobby;

import net.LobbyHandling.Lobby;
import net.LobbyHandling.ServerLobbyList;
import net.PlayerHandling.Player;
import net.PlayerHandling.ServerPlayerList;
import net.ServerLogic;
import net.packets.Packet;

public class PacketJoinLobby extends Packet {

    private Lobby lobby;
    private Player player;
    private ServerPlayerList playerList;
    private ServerLobbyList lobbyList;
    /**
     * A packed which is send from the client to the Server once
     * he has chosen a Lobby to join. Server should then move the client in
     * the chosen Lobby
     * @param clientId of the player to be added to specified lobby
     * @param data lobbyId of the chosen lobby
     */
    public PacketJoinLobby(int clientId, String data) {
        super(PacketTypes.JOIN_LOBBY);
        validate();
        this.playerList = ServerLogic.getPlayerList();
        this.lobbyList = ServerLogic.getLobbyList();
        setData(data);
        setClientId(clientId);
        processData();
    }

    @Override
    public boolean validate() { return true; }

    @Override
    public void processData() {
        this.player = playerList.searchPlayer(getClientId());
        this.lobby = lobbyList.searchLobby(Integer.parseInt(getData()));
        int code = lobby.addPlayer(player);
        String result = "" + code + ";" + lobby.getLobbyId();
        PacketJoinLobbyStatus status = new PacketJoinLobbyStatus(getClientId(), result);
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
}
