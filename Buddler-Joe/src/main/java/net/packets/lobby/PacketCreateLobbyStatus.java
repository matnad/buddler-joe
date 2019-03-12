package net.packets.lobby;

import net.playerhandling.ServerPlayerList;
import net.ServerLogic;
import net.packets.Packet;

public class PacketCreateLobbyStatus extends Packet{
    private ServerPlayerList playerList;

    /**
     * Package to inform the client over the result of the lobby-creation attempt
     * @param clientId to find the player in the list
     * @param data a String with "code;lobbyId"
     */

    public PacketCreateLobbyStatus(int clientId, String data) {
        super(Packet.PacketTypes.CREATE_LOBBY_STATUS);
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
