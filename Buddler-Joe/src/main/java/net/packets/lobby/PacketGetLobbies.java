package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;

public class PacketGetLobbies extends Packet {


    public PacketGetLobbies(int clientId){
        //server builds
        super(PacketTypes.GET_LOBBIES);
        setClientId(clientId);
    }

    public PacketGetLobbies(){
        //client builds
        super(PacketTypes.GET_LOBBIES);
    }

    @Override
    public void validate() {

    }

    @Override
    public void processData() {
        String info = ServerLogic.getLobbyList().getTopTen();
        PacketLobbyOverview p = new PacketLobbyOverview(getClientId(),info);
        p.sendToClient(getClientId());
    }
}
