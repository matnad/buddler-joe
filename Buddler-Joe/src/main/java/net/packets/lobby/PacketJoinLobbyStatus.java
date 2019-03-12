package net.packets.lobby;

import net.playerhandling.ServerPlayerList;
import net.ServerLogic;
import net.packets.Packet;

public class PacketJoinLobbyStatus extends Packet{
    private ServerPlayerList playerList;

    /**
     * Package to inform the client over the result of the lobby-join attempt
     * @param clientId to find the player in the list
     * @param data a String with "code;lobbyId"
     */

    public PacketJoinLobbyStatus(int clientId, String data) {
        super(Packet.PacketTypes.JOIN_LOBBY_STATUS);
        setData(data);
        setClientId(clientId);
        validate();

    }


    @Override
    public void validate() {

    }

    @Override
    public void processData() {

    }
}
