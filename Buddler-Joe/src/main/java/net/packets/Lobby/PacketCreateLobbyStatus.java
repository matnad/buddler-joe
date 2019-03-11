package net.packets.Lobby;

import net.PlayerHandling.ServerPlayerList;
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
        validate();
        setData(data);
        setClientId(clientId);
        processData();
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void processData() {
        ServerLogic.sendPacket(getClientId(),this);
    }

    @Override
    public Packet getPackage() {
        return null;
    }

    @Override
    public String toString() {
        String[] temp = getData().split(";");
        String stringToBeCreated = "PacketCreateLobbyStatus{result=" + temp[0];
        if(temp.length>1){
            stringToBeCreated = stringToBeCreated + ", lobbyId=" + temp[1];
        }
        return stringToBeCreated;
    }
}
