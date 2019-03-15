package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;

import java.util.StringJoiner;

public class PacketGetLobbies extends Packet {


    public PacketGetLobbies(int clientId){
        //server builds
        super(PacketTypes.GET_LOBBIES);
        setClientId(clientId);
        validate();
    }

    public PacketGetLobbies(){
        //client builds
        super(PacketTypes.GET_LOBBIES);
    }

    @Override
    public void validate() {
        isLoggedIn();
    }

    @Override
    public void processData() {
        String info;
        //checks if the client is logged in or not.
        if(hasErrors()) {
            StringJoiner statusJ = new StringJoiner("\n", "ERRORS: ", "");
            for (String error : getErrors()) {
                statusJ.add(error);
            }
            info = statusJ.toString();
        }else{
            info = "OK║" + ServerLogic.getLobbyList().getTopTen();
        }
        PacketLobbyOverview p = new PacketLobbyOverview(getClientId(),info);
        p.sendToClient(getClientId());
    }
}
