package net.packets.login_logout;

import net.*;
import net.playerhandling.Player;
import net.packets.Packet;

import java.util.StringJoiner;

public class PacketLogin extends Packet {


    private String username;
    /**
     * Login Packet which gets sent first by the client. Validates the Login package, checks whether the
     * Username is already taken and if not adds the player to the ServerPlayerList. Also creates instance
     * of a player with the needed information to forward to the ServerPlayerList.
     * @param clientId of the player to be added to the Player instance
     * @param data username to create the player
     */

    public PacketLogin(int clientId, String data) {
        super(PacketTypes.LOGIN);
        setData(data);
        setClientId(clientId);
        username = getData();
        validate();
    }

    public void validate(){
        checkUsername(username);
    }

    public void processData(){
        String status;
        if(hasErrors()){
            status = createErrorMessage();
        }else{
            Player player = new Player(username,getClientId());
            status = ServerLogic.getPlayerList().addPlayer(player);
        }
        PacketLoginStatus p = new PacketLoginStatus(getClientId(),status);
        p.sendToClient(getClientId());
    }


}
