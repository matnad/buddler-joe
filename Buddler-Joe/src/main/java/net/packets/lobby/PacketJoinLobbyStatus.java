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
        String stringToBeCreated = "PacketJoinLobbyStatus{result=" + temp[0];
        if(temp.length>1){
            stringToBeCreated = stringToBeCreated + ", lobbyId=" + temp[1];
        }
        return stringToBeCreated;
    }
}
