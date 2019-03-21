package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;

import java.util.StringJoiner;

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
        //No data to validate since it is a Empty Packet
    }

    @Override
    public void processData() {
        String info;
        if(!isLoggedIn()){
            addError("Not loggedin yet");
        }
        if(hasErrors()) {
            info = createErrorMessage();
        }else{
            info = "OK║" + ServerLogic.getLobbyList().getTopTen();
        }
        PacketLobbyOverview p = new PacketLobbyOverview(getClientId(),info);
        p.sendToClient(getClientId());
    }
}
